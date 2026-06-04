package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.FinancialSummaryDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.TransactionType;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.monthlySpendingLimit.SpendingLimitNotConfiguredException;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.FinancialTransactionRepository;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.SpendingLimitRepository;

@Service
@Transactional(readOnly = true)
public class FinancialSummaryService {

    private final SpendingLimitRepository spendingLimitRepository;
    private final FinancialTransactionRepository transactionRepository;

    private static final BigDecimal NEAR_LIMIT_THRESHOLD = new BigDecimal("80");

    public FinancialSummaryService(
            SpendingLimitRepository spendingLimitRepository,
            FinancialTransactionRepository transactionRepository) {
        this.spendingLimitRepository = spendingLimitRepository;
        this.transactionRepository = transactionRepository;
    }

    public FinancialSummaryDTO getFinancialSummary(User authenticatedUser) {
        var spendingLimit = spendingLimitRepository
                .findByUser(authenticatedUser)
                .orElseThrow(SpendingLimitNotConfiguredException::new);

        BigDecimal monthlyLimitAmount = spendingLimit.getAmount();

        // Get current month dates
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        LocalDate lastDayOfMonth = currentMonth.atEndOfMonth();

        // Calculate monthly expense total
        var monthlyExpenses = transactionRepository
                .findByUserAndTypeAndDateBetween(
                        authenticatedUser,
                        TransactionType.EXPENSE,
                        firstDayOfMonth,
                        lastDayOfMonth);

        BigDecimal totalExpenses = monthlyExpenses
                .stream()
                .map(transaction -> transaction.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate percentage used
        BigDecimal percentageUsed = calculatePercentage(totalExpenses, monthlyLimitAmount);

        // Determine if near limit (>= 80%)
        boolean isNearLimit = percentageUsed.compareTo(NEAR_LIMIT_THRESHOLD) >= 0;

        // Determine if exceeded
        boolean isExceeded = totalExpenses.compareTo(monthlyLimitAmount) > 0;

        return new FinancialSummaryDTO(
                monthlyLimitAmount,
                totalExpenses,
                percentageUsed,
                isNearLimit,
                isExceeded);
    }

    private BigDecimal calculatePercentage(BigDecimal amount, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return amount
                .divide(total, 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }
}
