package br.edu.ifpe.pdsc.investCalc.investCalc.services.repeatedExpense;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.repeatedExpense.RepeatedExpenseKey;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.repeatedExpense.RepeatedExpenseResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.FinancialTransaction;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.TransactionType;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.FinancialTransactionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RepeatedExpenseService {

    private final FinancialTransactionRepository financialTransactionRepository;

    public List<RepeatedExpenseResponse> getRecurringExpenses(User user) {

        List<FinancialTransaction> expenses = financialTransactionRepository.findByUserAndType(
                user,
                TransactionType.EXPENSE);

        Map<RepeatedExpenseKey, List<FinancialTransaction>> groupedExpenses = expenses.stream()
                .collect(Collectors.groupingBy(
                        transaction -> new RepeatedExpenseKey(
                                normalize(transaction.getDescription()),
                                transaction.getSubcategory()
                                        .getCategory()
                                        .getName(),
                                transaction.getSubcategory()
                                        .getName())));

        return groupedExpenses.entrySet()
                .stream()
                .filter(entry -> isRecurring(entry.getValue()))
                .map(entry -> buildResponse(
                        entry.getKey(),
                        entry.getValue()))
                .sorted((a, b) -> Integer.compare(
                        b.frequency(),
                        a.frequency()))
                .toList();
    }

    private boolean isRecurring(
            List<FinancialTransaction> transactions) {

        Set<YearMonth> months = transactions.stream()
                .map(transaction -> YearMonth.from(transaction.getDate()))
                .collect(Collectors.toSet());

        return months.size() >= 2;
    }

    private RepeatedExpenseResponse buildResponse(
            RepeatedExpenseKey key,
            List<FinancialTransaction> transactions) {

        BigDecimal averageAmount = transactions.stream()
                .map(FinancialTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(
                        BigDecimal.valueOf(transactions.size()),
                        2,
                        RoundingMode.HALF_UP);

        Set<YearMonth> months = transactions.stream()
                .map(transaction -> YearMonth.from(transaction.getDate()))
                .collect(Collectors.toSet());

        return new RepeatedExpenseResponse(
                key.description(),
                key.category(),
                key.subcategory(),
                averageAmount,
                months.size());
    }

    private String normalize(String description) {

        return description == null
                ? ""
                : description.trim().toLowerCase();
    }
}
