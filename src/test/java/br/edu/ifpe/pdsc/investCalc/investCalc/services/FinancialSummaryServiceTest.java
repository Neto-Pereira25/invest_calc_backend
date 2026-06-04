package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.FinancialSummaryDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.FinancialTransaction;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.SpendingLimit;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.TransactionType;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.monthlySpendingLimit.SpendingLimitNotConfiguredException;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.FinancialTransactionRepository;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.SpendingLimitRepository;

@ExtendWith(MockitoExtension.class)
class FinancialSummaryServiceTest {

    @Mock
    private SpendingLimitRepository spendingLimitRepository;

    @Mock
    private FinancialTransactionRepository transactionRepository;

    @InjectMocks
    private FinancialSummaryService service;

    private User authenticatedUser;
    private SpendingLimit spendingLimit;

    @BeforeEach
    void setup() {
        authenticatedUser = new User();
        authenticatedUser.setId(1L);
        authenticatedUser.setEmail("user@email.com");

        spendingLimit = new SpendingLimit();
        spendingLimit.setId(10L);
        spendingLimit.setAmount(BigDecimal.valueOf(1000));
        spendingLimit.setUser(authenticatedUser);
    }

    @Test
    @DisplayName("Should return financial summary when user is under the monthly limit")
    void shouldReturnFinancialSummaryWhenUserIsUnderTheMonthlyLimit() {
        FinancialTransaction expense1 = new FinancialTransaction();
        expense1.setAmount(BigDecimal.valueOf(200));

        FinancialTransaction expense2 = new FinancialTransaction();
        expense2.setAmount(BigDecimal.valueOf(100));

        when(spendingLimitRepository.findByUser(authenticatedUser)).thenReturn(Optional.of(spendingLimit));
        when(transactionRepository.findByUserAndTypeAndDateBetween(
                eq(authenticatedUser),
                eq(TransactionType.EXPENSE),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(List.of(expense1, expense2));

        FinancialSummaryDTO response = service.getFinancialSummary(authenticatedUser);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(1000), response.monthlyLimit());
        assertEquals(BigDecimal.valueOf(300), response.monthlyExpenseTotal());
        assertEquals(new BigDecimal("30.00"), response.percentageUsed());
        assertFalse(response.isNearLimit());
        assertFalse(response.isExceeded());
    }

    @Test
    @DisplayName("Should return financial summary when user reaches the warning threshold")
    void shouldReturnFinancialSummaryWhenUserReachesTheWarningThreshold() {
        FinancialTransaction expense = new FinancialTransaction();
        expense.setAmount(BigDecimal.valueOf(800));

        when(spendingLimitRepository.findByUser(authenticatedUser)).thenReturn(Optional.of(spendingLimit));
        when(transactionRepository.findByUserAndTypeAndDateBetween(
                eq(authenticatedUser),
                eq(TransactionType.EXPENSE),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(List.of(expense));

        FinancialSummaryDTO response = service.getFinancialSummary(authenticatedUser);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(800), response.monthlyExpenseTotal());
        assertEquals(new BigDecimal("80.00"), response.percentageUsed());
        assertTrue(response.isNearLimit());
        assertFalse(response.isExceeded());
    }

    @Test
    @DisplayName("Should return financial summary when user exceeds the monthly limit")
    void shouldReturnFinancialSummaryWhenUserExceedsTheMonthlyLimit() {
        FinancialTransaction expense = new FinancialTransaction();
        expense.setAmount(BigDecimal.valueOf(1200));

        when(spendingLimitRepository.findByUser(authenticatedUser)).thenReturn(Optional.of(spendingLimit));
        when(transactionRepository.findByUserAndTypeAndDateBetween(
                eq(authenticatedUser),
                eq(TransactionType.EXPENSE),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(List.of(expense));

        FinancialSummaryDTO response = service.getFinancialSummary(authenticatedUser);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(1200), response.monthlyExpenseTotal());
        assertEquals(new BigDecimal("120.00"), response.percentageUsed());
        assertTrue(response.isNearLimit());
        assertTrue(response.isExceeded());
    }

    @Test
    @DisplayName("Should throw exception when monthly limit is not configured")
    void shouldThrowExceptionWhenMonthlyLimitIsNotConfigured() {
        when(spendingLimitRepository.findByUser(authenticatedUser)).thenReturn(Optional.empty());

        assertThrows(
                SpendingLimitNotConfiguredException.class,
                () -> service.getFinancialSummary(authenticatedUser));

        verify(transactionRepository, never()).findByUserAndTypeAndDateBetween(
                any(User.class),
                any(TransactionType.class),
                any(LocalDate.class),
                any(LocalDate.class));
    }

    @Test
    @DisplayName("Should use the current month date range when querying expenses")
    void shouldUseCurrentMonthDateRangeWhenQueryingExpenses() {
        when(spendingLimitRepository.findByUser(authenticatedUser)).thenReturn(Optional.of(spendingLimit));
        when(transactionRepository.findByUserAndTypeAndDateBetween(
                eq(authenticatedUser),
                eq(TransactionType.EXPENSE),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(List.of());

        service.getFinancialSummary(authenticatedUser);

        YearMonth currentMonth = YearMonth.now();
        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        LocalDate lastDayOfMonth = currentMonth.atEndOfMonth();

        verify(transactionRepository).findByUserAndTypeAndDateBetween(
                eq(authenticatedUser),
                eq(TransactionType.EXPENSE),
                eq(firstDayOfMonth),
                eq(lastDayOfMonth));
    }
}