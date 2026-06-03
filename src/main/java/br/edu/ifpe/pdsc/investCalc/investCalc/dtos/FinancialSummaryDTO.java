package br.edu.ifpe.pdsc.investCalc.investCalc.dtos;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FinancialSummaryDTO(
        BigDecimal monthlyLimit,
        BigDecimal monthlyExpenseTotal,
        BigDecimal percentageUsed,
        Boolean isNearLimit,
        Boolean isExceeded) {
}
