package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.FinancialTransactionRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.FinancialTransactionResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.Category;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.FinancialTransaction;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.Subcategory;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.TransactionType;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.transaction.SubcategoryNotFoundException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.transaction.TransactionNotFoundException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.transaction.UnauthorizedTransactionAccessException;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.FinancialTransactionRepository;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.SubcategoryRepository;

@ExtendWith(MockitoExtension.class)
class FinancialTransactionServiceTest {

        @Mock
        private FinancialTransactionRepository transactionRepository;

        @Mock
        private SubcategoryRepository subcategoryRepository;

        @InjectMocks
        private FinancialTransactionService transactionService;

        private FinancialTransactionRequest request;
        private FinancialTransaction transaction;
        private Long transactionId;
        private Category category;
        private Subcategory subcategory;
        private User user;

        @BeforeEach
        void setup() {
                request = new FinancialTransactionRequest();
                request.setAmount(BigDecimal.valueOf(100));
                request.setDescription("Almoço");
                request.setDate(LocalDate.now());
                request.setSubcategoryId(1L);

                category = new Category();
                category.setId(1L);
                category.setName("Moradia");
                category.setType(TransactionType.EXPENSE);

                subcategory = new Subcategory();
                subcategory.setId(1L);
                subcategory.setName("Aluguel");
                subcategory.setCategory(category);

                user = new User();
                user.setId(1L);

                transactionId = 10L;

                transaction = new FinancialTransaction();
                transaction.setId(transactionId);
                transaction.setDescription("Antigo");
                transaction.setAmount(BigDecimal.valueOf(50));
                transaction.setDate(LocalDate.now());
                transaction.setSubcategory(subcategory);
                transaction.setUser(user);
                transaction.setType(TransactionType.EXPENSE);
        }

        @Test
        void shouldCreateTransactionSuccessfully() {

                // ARRANGE
                when(subcategoryRepository.findById(request.getSubcategoryId()))
                                .thenReturn(Optional.of(subcategory));

                when(transactionRepository.save(any()))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // ACT
                FinancialTransactionResponse result = transactionService.createFinancialTransaction(request, user);

                // ASSERT
                assertEquals(request.getAmount(), result.getAmount());
                assertEquals(request.getDescription(), result.getDescription());
                assertEquals(request.getDate(), result.getDate());
                assertEquals(subcategory.getName(), result.getSubcategory());
        }

        @Test
        void shouldThrowExceptionWhenSubcategoryNotFound() {

                // ARRANGE
                when(subcategoryRepository.findById(request.getSubcategoryId()))
                                .thenReturn(Optional.empty());

                // ACT & ASSERT
                SubcategoryNotFoundException exception = Assertions.assertThrows(
                                SubcategoryNotFoundException.class,
                                () -> transactionService.createFinancialTransaction(request, user));

                assertEquals("Subcategoria nao encontrada", exception.getMessage());
        }

        @Test
        void shouldListTransactionsByUser() {

                // ARRANGE
                Category category = new Category();
                category.setName("Alimentação");
                category.setType(TransactionType.EXPENSE);

                Subcategory subcategory = new Subcategory();
                subcategory.setName("Restaurante");
                subcategory.setCategory(category);

                FinancialTransaction transaction = new FinancialTransaction();
                transaction.setDescription("Almoço");
                transaction.setAmount(BigDecimal.valueOf(50));
                transaction.setDate(LocalDate.now());
                transaction.setSubcategory(subcategory);
                transaction.setType(TransactionType.EXPENSE);

                when(transactionRepository.findByUser(user))
                                .thenReturn(List.of(transaction));

                // ACT
                List<FinancialTransactionResponse> result = transactionService.listByUser(user);

                // ASSERT
                assertEquals(1, result.size());
                assertEquals("Almoço", result.get(0).getDescription());
                assertEquals("Restaurante", result.get(0).getSubcategory());
        }

        @Test
        void shouldReturnEmptyListWhenUserHasNoTransactions() {

                when(transactionRepository.findByUser(user))
                                .thenReturn(List.of());

                List<FinancialTransactionResponse> result = transactionService.listByUser(user);

                assertTrue(result.isEmpty());
        }

        @Test
        void shouldUpdateTransactionSuccessfully() {

                // ARRANGE
                when(transactionRepository.findById(transactionId))
                                .thenReturn(Optional.of(transaction));

                when(subcategoryRepository.findById(request.getSubcategoryId()))
                                .thenReturn(Optional.of(subcategory));

                when(transactionRepository.save(any()))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // ACT
                FinancialTransactionResponse result = transactionService.updateFinancialTransaction(transactionId,
                                request,
                                user);

                // ASSERT
                assertEquals(request.getDescription(), result.getDescription());
                assertEquals(request.getAmount(), result.getAmount());
                assertEquals(subcategory.getName(), result.getSubcategory());
        }

        @Test
        void shouldThrowExceptionWhenTransactionNotFound() {

                when(transactionRepository.findById(transactionId))
                                .thenReturn(Optional.empty());

                TransactionNotFoundException exception = assertThrows(
                                TransactionNotFoundException.class,
                                () -> transactionService.updateFinancialTransaction(transactionId, request, user));

                assertEquals("Transacao nao encontrada", exception.getMessage());
        }

        @Test
        void shouldThrowExceptionWhenUserIsNotOwner() {

                // ARRANGE
                User anotherUser = new User();
                anotherUser.setId(999L);

                transaction.setUser(anotherUser);

                when(transactionRepository.findById(transactionId))
                                .thenReturn(Optional.of(transaction));

                // ACT & ASSERT
                UnauthorizedTransactionAccessException exception = assertThrows(
                                UnauthorizedTransactionAccessException.class,
                                () -> transactionService.updateFinancialTransaction(transactionId, request, user));

                assertEquals("Usuario nao autorizado a realizar esta operacao", exception.getMessage());
        }

        @Test
        void shouldDeleteTransactionSuccessfully() {

                // ARRANGE
                when(transactionRepository.findById(transactionId))
                                .thenReturn(Optional.of(transaction));

                doNothing().when(transactionRepository).delete(transaction);

                // ACT
                transactionService.deleteFinancialTransaction(transactionId, user);

                // ASSERT
                verify(transactionRepository, times(1)).delete(transaction);
        }

        @Test
        void shouldThrowExceptionWhenDeletingNonExistingTransaction() {

                when(transactionRepository.findById(transactionId))
                                .thenReturn(Optional.empty());

                TransactionNotFoundException exception = assertThrows(
                                TransactionNotFoundException.class,
                                () -> transactionService.deleteFinancialTransaction(transactionId, user));

                assertEquals("Transacao nao encontrada", exception.getMessage());
        }

        @Test
        void shouldThrowExceptionWhenDeletingTransactionFromAnotherUser() {

                User anotherUser = new User();
                anotherUser.setId(999L);

                transaction.setUser(anotherUser);

                when(transactionRepository.findById(transactionId))
                                .thenReturn(Optional.of(transaction));

                UnauthorizedTransactionAccessException exception = assertThrows(
                                UnauthorizedTransactionAccessException.class,
                                () -> transactionService.deleteFinancialTransaction(transactionId, user));

                assertEquals("Usuario nao autorizado a realizar esta operacao", exception.getMessage());
        }
}
