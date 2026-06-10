package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.RetirementSimulatorRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.RetirementSimulatorResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.PeriodType;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.RateType;

@Service
public class RetirementSimulatorService {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal TWELVE = BigDecimal.valueOf(12);
    private static final BigDecimal DEFAULT_ANNUAL_INFLATION_RATE = BigDecimal.ZERO;
    private static final BigDecimal DEFAULT_SAFE_WITHDRAWAL_RATE = BigDecimal.valueOf(4);

    public RetirementSimulatorResponse simulate(RetirementSimulatorRequest request) {

        BigDecimal annualInflationRate = getAnnualInflationRate(request);
        BigDecimal safeWithdrawalRate = getSafeWithdrawalRate(request);
        BigDecimal normalizedAnnualInflationRate = normalizeRate(annualInflationRate);
        BigDecimal normalizedSafeWithdrawalRate = normalizeRate(safeWithdrawalRate);
        BigDecimal monthlyRate = convertRate(request.getInterestRate(), request.getRateType());
        int monthsToRetirement = convertToMonths(request.getPeriod(), request.getPeriodType());

        BigDecimal inflationAdjustedMonthlyIncome = applyInflation(
                request.getDesiredMonthlyIncome(),
                normalizedAnnualInflationRate,
                monthsToRetirement);

        BigDecimal targetAmount = calculateTargetAmount(inflationAdjustedMonthlyIncome, normalizedSafeWithdrawalRate);
        BigDecimal requiredMonthlyContribution = calculateRequiredMonthlyContribution(
                targetAmount,
                monthlyRate,
                monthsToRetirement);

        return new RetirementSimulatorResponse(
                request.getDesiredMonthlyIncome().setScale(2, RoundingMode.HALF_UP),
                inflationAdjustedMonthlyIncome.setScale(2, RoundingMode.HALF_UP),
                targetAmount.setScale(2, RoundingMode.HALF_UP),
                requiredMonthlyContribution.setScale(2, RoundingMode.HALF_UP),
                toPercentRate(normalizedAnnualInflationRate).setScale(2, RoundingMode.HALF_UP),
                toPercentRate(normalizedSafeWithdrawalRate).setScale(2, RoundingMode.HALF_UP),
                monthsToRetirement);
    }

    private BigDecimal calculateTargetAmount(BigDecimal monthlyIncome, BigDecimal safeWithdrawalRate) {
        BigDecimal yearlyIncome = monthlyIncome.multiply(TWELVE);
        return yearlyIncome.divide(safeWithdrawalRate, 10, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateRequiredMonthlyContribution(
            BigDecimal targetAmount,
            BigDecimal monthlyRate,
            int monthsToRetirement) {

        double growthFactor = Math.pow(1 + monthlyRate.doubleValue(), monthsToRetirement);
        double annuityFactor = (growthFactor - 1) / monthlyRate.doubleValue();

        return BigDecimal.valueOf(targetAmount.doubleValue() / annuityFactor);
    }

    private BigDecimal applyInflation(
            BigDecimal desiredMonthlyIncome,
            BigDecimal annualInflationRate,
            int monthsToRetirement) {

        if (annualInflationRate.compareTo(BigDecimal.ZERO) == 0) {
            return desiredMonthlyIncome;
        }

        BigDecimal monthlyInflationRate = convertAnnualRateToMonthly(annualInflationRate);
        double adjustedValue = desiredMonthlyIncome.doubleValue()
                * Math.pow(1 + monthlyInflationRate.doubleValue(), monthsToRetirement);

        return BigDecimal.valueOf(adjustedValue);
    }

    private BigDecimal convertRate(BigDecimal interestRate, RateType rateType) {

        BigDecimal rate = normalizeRate(interestRate);

        if (RateType.YEARLY.equals(rateType)) {
            return convertAnnualRateToMonthly(interestRate);
        }

        return rate;
    }

    private BigDecimal convertAnnualRateToMonthly(BigDecimal annualRatePercentage) {
        BigDecimal annualRateDecimal = normalizeRate(annualRatePercentage);
        return BigDecimal.valueOf(Math.pow(1 + annualRateDecimal.doubleValue(), 1.0 / 12) - 1);
    }

    private BigDecimal normalizeRate(BigDecimal value) {
        if (value.compareTo(BigDecimal.ONE) <= 0) {
            return value;
        }

        return value.divide(ONE_HUNDRED, 10, RoundingMode.HALF_UP);
    }

    private BigDecimal toPercentRate(BigDecimal decimalRate) {
        return decimalRate.multiply(ONE_HUNDRED);
    }

    private int convertToMonths(Integer period, PeriodType periodType) {

        if (PeriodType.ANNUAL.equals(periodType)) {
            return period * 12;
        }

        return period;
    }

    private BigDecimal getAnnualInflationRate(RetirementSimulatorRequest request) {
        return request.getAnnualInflationRate() == null
                ? DEFAULT_ANNUAL_INFLATION_RATE
                : request.getAnnualInflationRate();
    }

    private BigDecimal getSafeWithdrawalRate(RetirementSimulatorRequest request) {
        return request.getSafeWithdrawalRate() == null
                ? DEFAULT_SAFE_WITHDRAWAL_RATE
                : request.getSafeWithdrawalRate();
    }
}
