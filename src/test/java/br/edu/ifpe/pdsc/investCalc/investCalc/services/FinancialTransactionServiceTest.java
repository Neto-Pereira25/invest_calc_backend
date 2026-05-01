package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.CreateFinancialTransactionRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.FinancialTransaction;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.Subcategory;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
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

    private CreateFinancialTransactionRequest request;
    private Subcategory subcategory;
    private User user;

    @BeforeEach
    void setup() {
        request = new CreateFinancialTransactionRequest();
        request.setAmount(BigDecimal.valueOf(100));
        request.setDescription("Almoço");
        request.setDate(LocalDate.now());
        request.setSubcategoryId(UUID.randomUUID());

        subcategory = new Subcategory();
        subcategory.setId(request.getSubcategoryId());

        user = new User();
        user.setId(1L);
    }

    @Test
    void shouldCreateTransactionSuccessfully() {

        // ARRANGE
        when(subcategoryRepository.findById(request.getSubcategoryId()))
                .thenReturn(Optional.of(subcategory));

        when(transactionRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        FinancialTransaction result = transactionService.createTransaction(request, user);

        // ASSERT
        assertEquals(request.getAmount(), result.getAmount());
        assertEquals(request.getDescription(), result.getDescription());
        assertEquals(request.getDate(), result.getDate());
        assertEquals(subcategory, result.getSubcategory());
        assertEquals(user, result.getUser());
    }

    @Test
    void shouldThrowExceptionWhenSubcategoryNotFound() {

        // ARRANGE
        when(subcategoryRepository.findById(request.getSubcategoryId()))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> transactionService.createTransaction(request, user));

        assertEquals("Subcategoria não encontrada", exception.getMessage());
    }
}
