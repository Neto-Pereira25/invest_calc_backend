package br.edu.ifpe.pdsc.investCalc.investCalc.dtos.scenarioComparison;

import java.math.BigDecimal;

public record ScenarioComparisonResponse(
        String scenarioName,
        BigDecimal investedAmount,
        BigDecimal totalInterest,
        BigDecimal finalAmount) {
}
