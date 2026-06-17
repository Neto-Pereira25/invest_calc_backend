package br.edu.ifpe.pdsc.investCalc.investCalc.services.repeatedExpense;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.repeatedExpense.RepeatedExpenseResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.Category;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.FinancialTransaction;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.Subcategory;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.TransactionType;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.FinancialTransactionRepository;

@ExtendWith(MockitoExtension.class)
class RepeatedExpenseServiceTest {

    @Mock
    private FinancialTransactionRepository financialTransactionRepository;

    @InjectMocks
    private RepeatedExpenseService repeatedExpenseService;

    private User user;
    private Subcategory streamingSubcategory;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);

        Category lazerCategory = new Category();
        lazerCategory.setName("Lazer");
        lazerCategory.setType(TransactionType.EXPENSE);

        streamingSubcategory = new Subcategory();
        streamingSubcategory.setName("Streaming");
        streamingSubcategory.setCategory(lazerCategory);
    }

    @Test
    void shouldReturnRecurringExpenseWhenPresentInTwoDifferentMonths() {

        List<FinancialTransaction> expenses = List.of(
                createExpense("Netflix", "39.90", LocalDate.of(2026, 1, 10)),
                createExpense("Netflix", "39.90", LocalDate.of(2026, 2, 10)));

        when(financialTransactionRepository.findByUserAndType(user, TransactionType.EXPENSE)).thenReturn(expenses);

        List<RepeatedExpenseResponse> result = repeatedExpenseService.getRecurringExpenses(user);

        assertEquals(1, result.size());
        assertEquals("netflix", result.get(0).description());
        assertEquals(2, result.get(0).frequency());
        assertEquals(new BigDecimal("39.90"), result.get(0).averageAmount());
    }

    @Test
    void shouldNotReturnRecurringExpenseWhenPresentOnlyInOneMonth() {

        List<FinancialTransaction> expenses = List.of(
                createExpense("Netflix", "39.90", LocalDate.of(2026, 1, 10)),
                createExpense("Netflix", "39.90", LocalDate.of(2026, 1, 25)));

        when(financialTransactionRepository.findByUserAndType(user, TransactionType.EXPENSE)).thenReturn(expenses);

        List<RepeatedExpenseResponse> result = repeatedExpenseService.getRecurringExpenses(user);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCalculateFrequencyAndAverageForThreeMonths() {

        List<FinancialTransaction> expenses = List.of(
                createExpense("Netflix", "39.90", LocalDate.of(2026, 1, 10)),
                createExpense("Netflix", "39.90", LocalDate.of(2026, 2, 10)),
                createExpense("Netflix", "44.90", LocalDate.of(2026, 3, 10)));

        when(financialTransactionRepository.findByUserAndType(user, TransactionType.EXPENSE)).thenReturn(expenses);

        List<RepeatedExpenseResponse> result = repeatedExpenseService.getRecurringExpenses(user);

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).frequency());
        assertEquals(new BigDecimal("41.57"), result.get(0).averageAmount());
    }

    @Test
    void shouldReturnIndependentRecurrencesForDifferentDescriptions() {

        List<FinancialTransaction> expenses = List.of(
                createExpense("Netflix", "39.90", LocalDate.of(2026, 1, 10)),
                createExpense("Netflix", "39.90", LocalDate.of(2026, 2, 10)),
                createExpense("Spotify", "21.90", LocalDate.of(2026, 1, 12)),
                createExpense("Spotify", "21.90", LocalDate.of(2026, 2, 12)));

        when(financialTransactionRepository.findByUserAndType(user, TransactionType.EXPENSE)).thenReturn(expenses);

        List<RepeatedExpenseResponse> result = repeatedExpenseService.getRecurringExpenses(user);

        assertEquals(2, result.size());
        assertEquals(2, result.get(0).frequency());
        assertEquals(2, result.get(1).frequency());
    }

    private FinancialTransaction createExpense(String description, String amount, LocalDate date) {

        FinancialTransaction transaction = new FinancialTransaction();
        transaction.setDescription(description);
        transaction.setAmount(new BigDecimal(amount));
        transaction.setDate(date);
        transaction.setSubcategory(streamingSubcategory);
        transaction.setType(TransactionType.EXPENSE);
        transaction.setUser(user);

        return transaction;
    }
}
