package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ReverseSimulationRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ReverseSimulationResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.PeriodType;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.RateInputType;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.RateType;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.ReverseSimulationMode;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.InvalidReverseSimulationRequestException;

@Service
public class ReverseSimulationService {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal TWELVE = BigDecimal.valueOf(12);

    public ReverseSimulationResponse simulate(ReverseSimulationRequest request) {

        BigDecimal monthlyRate = convertRate(
                request.getInterestRate(),
                request.getRateType(),
                request.getInterestRateInputType());

        if (ReverseSimulationMode.CALCULATE_CONTRIBUTION.equals(request.getMode())) {
            validateContributionMode(request);

            int months = convertToMonths(request.getPeriod(), request.getPeriodType());
            BigDecimal contribution = calculateRequiredMonthlyContribution(request.getTargetAmount(), monthlyRate,
                    months);

            return new ReverseSimulationResponse(
                    request.getMode(),
                    request.getTargetAmount().setScale(2, RoundingMode.HALF_UP),
                    toPercentRate(monthlyRate).setScale(4, RoundingMode.HALF_UP),
                    null,
                    request.getPeriod(),
                    request.getPeriodType(),
                    contribution.setScale(2, RoundingMode.HALF_UP),
                    null,
                    null);
        }

        if (ReverseSimulationMode.CALCULATE_PERIOD.equals(request.getMode())) {
            validatePeriodMode(request);

            int requiredMonths = calculateRequiredMonths(request.getTargetAmount(), request.getMonthlyContribution(),
                    monthlyRate);
            BigDecimal requiredYears = BigDecimal.valueOf(requiredMonths)
                    .divide(TWELVE, 2, RoundingMode.HALF_UP);

            return new ReverseSimulationResponse(
                    request.getMode(),
                    request.getTargetAmount().setScale(2, RoundingMode.HALF_UP),
                    toPercentRate(monthlyRate).setScale(4, RoundingMode.HALF_UP),
                    request.getMonthlyContribution().setScale(2, RoundingMode.HALF_UP),
                    null,
                    null,
                    null,
                    requiredMonths,
                    requiredYears);
        }

        throw new InvalidReverseSimulationRequestException("Modo da simulação reversa é inválido");
    }

    private void validateContributionMode(ReverseSimulationRequest request) {
        if (request.getPeriod() == null || request.getPeriod() < 1) {
            throw new InvalidReverseSimulationRequestException(
                    "Para calcular aporte, informe um período válido");
        }

        if (request.getPeriodType() == null) {
            throw new InvalidReverseSimulationRequestException(
                    "Para calcular aporte, informe o tipo de período");
        }
    }

    private void validatePeriodMode(ReverseSimulationRequest request) {
        if (request.getMonthlyContribution() == null
                || request.getMonthlyContribution().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidReverseSimulationRequestException(
                    "Para calcular prazo, informe um aporte mensal maior que zero");
        }
    }

    private BigDecimal calculateRequiredMonthlyContribution(BigDecimal targetAmount, BigDecimal monthlyRate,
            int months) {
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return targetAmount.divide(BigDecimal.valueOf(months), 10, RoundingMode.HALF_UP);
        }

        double growthFactor = Math.pow(1 + monthlyRate.doubleValue(), months);
        double annuityFactor = (growthFactor - 1) / monthlyRate.doubleValue();

        return BigDecimal.valueOf(targetAmount.doubleValue() / annuityFactor);
    }

    private int calculateRequiredMonths(BigDecimal targetAmount, BigDecimal monthlyContribution,
            BigDecimal monthlyRate) {
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(Math.ceil(targetAmount.doubleValue() / monthlyContribution.doubleValue()))
                    .intValue();
        }

        double numerator = Math
                .log((targetAmount.doubleValue() * monthlyRate.doubleValue() / monthlyContribution.doubleValue()) + 1);
        double denominator = Math.log(1 + monthlyRate.doubleValue());

        return (int) Math.ceil(numerator / denominator);
    }

    private BigDecimal convertRate(BigDecimal interestRate, RateType rateType, RateInputType rateInputType) {

        BigDecimal rate = normalizeInterestRate(interestRate, rateType, rateInputType);

        if (RateType.YEARLY.equals(rateType)) {
            return convertAnnualRateToMonthlyFromDecimal(rate);
        }

        return rate;
    }

    private BigDecimal normalizeInterestRate(
            BigDecimal interestRate,
            RateType rateType,
            RateInputType rateInputType) {
        if (RateInputType.DECIMAL.equals(rateInputType)) {
            return interestRate;
        }

        if (RateInputType.PERCENTAGE.equals(rateInputType)) {
            return interestRate.divide(ONE_HUNDRED, 10, RoundingMode.HALF_UP);
        }

        if (RateType.MONTHLY.equals(rateType) && interestRate.compareTo(BigDecimal.ONE) <= 0) {
            return interestRate.divide(ONE_HUNDRED, 10, RoundingMode.HALF_UP);
        }

        return normalizeRate(interestRate);
    }

    private BigDecimal convertAnnualRateToMonthly(BigDecimal annualRatePercentage) {
        BigDecimal annualRateDecimal = normalizeRate(annualRatePercentage);
        return convertAnnualRateToMonthlyFromDecimal(annualRateDecimal);
    }

    private BigDecimal convertAnnualRateToMonthlyFromDecimal(BigDecimal annualRateDecimal) {
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
}
