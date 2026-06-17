package br.edu.ifpe.pdsc.investCalc.investCalc.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetirementSimulatorResponse {

    private BigDecimal desiredMonthlyIncome;
    private BigDecimal inflationAdjustedMonthlyIncome;
    private BigDecimal targetAmount;
    private BigDecimal requiredMonthlyContribution;
    private BigDecimal usedAnnualInflationRate;
    private BigDecimal usedSafeWithdrawalRate;
    private Integer monthsToRetirement;
}
