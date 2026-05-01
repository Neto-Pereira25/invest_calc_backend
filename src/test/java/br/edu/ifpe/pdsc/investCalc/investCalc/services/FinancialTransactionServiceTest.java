package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.Subcategory;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.TransactionType;
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
        RuntimeException exception = Assertions.assertThrows(
                RuntimeException.class,
                () -> transactionService.createFinancialTransaction(request, user));

        assertEquals("Subcategoria não encontrada", exception.getMessage());
    }
}
