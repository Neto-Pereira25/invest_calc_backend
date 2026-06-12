package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.RetirementSimulatorRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.RetirementSimulatorResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.PeriodType;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.RateInputType;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.RateType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class RetirementSimulatorServiceTest {

    private RetirementSimulatorService service;
    private Validator validator;

    @BeforeEach
    void setup() {
        service = new RetirementSimulatorService();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldSimulateRetirementUsingDefaultAssumptions() {

        RetirementSimulatorRequest request = new RetirementSimulatorRequest(
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(12),
                10,
                PeriodType.ANNUAL,
                RateType.YEARLY,
                null,
                null,
                null);

        RetirementSimulatorResponse response = service.simulate(request);

        assertEquals(BigDecimal.valueOf(1000.00).setScale(2), response.getDesiredMonthlyIncome());
        assertEquals(BigDecimal.valueOf(1000.00).setScale(2), response.getInflationAdjustedMonthlyIncome());
        assertEquals(BigDecimal.valueOf(300000.00).setScale(2), response.getTargetAmount());
        assertEquals(BigDecimal.valueOf(1351.78).setScale(2), response.getRequiredMonthlyContribution());
        assertEquals(BigDecimal.ZERO.setScale(2), response.getUsedAnnualInflationRate());
        assertEquals(BigDecimal.valueOf(4.00).setScale(2), response.getUsedSafeWithdrawalRate());
        assertEquals(120, response.getMonthsToRetirement());
    }

    @Test
    void shouldApplyInflationToTargetIncome() {

        RetirementSimulatorRequest request = new RetirementSimulatorRequest(
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(12),
                1,
                PeriodType.ANNUAL,
                RateType.YEARLY,
                null,
                BigDecimal.valueOf(12),
                BigDecimal.valueOf(4));

        RetirementSimulatorResponse response = service.simulate(request);

        assertEquals(BigDecimal.valueOf(1120.00).setScale(2), response.getInflationAdjustedMonthlyIncome());
        assertEquals(BigDecimal.valueOf(336000.00).setScale(2), response.getTargetAmount());
        assertEquals(BigDecimal.valueOf(12.00).setScale(2), response.getUsedAnnualInflationRate());
        assertEquals(BigDecimal.valueOf(4.00).setScale(2), response.getUsedSafeWithdrawalRate());
    }

    @Test
    void shouldConvertMonthlyPeriodWithoutChangingMonthsToRetirement() {

        RetirementSimulatorRequest request = new RetirementSimulatorRequest(
                BigDecimal.valueOf(2000),
                BigDecimal.valueOf(1),
                24,
                PeriodType.MONTHLY,
                RateType.MONTHLY,
                null,
                BigDecimal.ZERO,
                BigDecimal.valueOf(5));

        RetirementSimulatorResponse response = service.simulate(request);

        assertEquals(24, response.getMonthsToRetirement());
        assertEquals(BigDecimal.valueOf(480000.00).setScale(2), response.getTargetAmount());
    }

    @Test
    void shouldSupportDecimalRateInputs() {

        RetirementSimulatorRequest request = new RetirementSimulatorRequest(
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(0.10),
                25,
                PeriodType.ANNUAL,
                RateType.YEARLY,
                null,
                BigDecimal.valueOf(0.04),
                BigDecimal.valueOf(0.04));

        RetirementSimulatorResponse response = service.simulate(request);

        assertEquals(BigDecimal.valueOf(13329.18).setScale(2), response.getInflationAdjustedMonthlyIncome());
        assertEquals(BigDecimal.valueOf(3998754.50).setScale(2), response.getTargetAmount());
        assertEquals(BigDecimal.valueOf(3242.26).setScale(2), response.getRequiredMonthlyContribution());
        assertEquals(BigDecimal.valueOf(4.00).setScale(2), response.getUsedAnnualInflationRate());
        assertEquals(BigDecimal.valueOf(4.00).setScale(2), response.getUsedSafeWithdrawalRate());
        assertEquals(300, response.getMonthsToRetirement());
    }

    @Test
    void shouldThrowExceptionWhenRequestIsNull() {
        assertThrows(NullPointerException.class, () -> service.simulate(null));
    }

    @Test
    void shouldThrowExceptionWhenInterestRateIsNull() {

        RetirementSimulatorRequest request = new RetirementSimulatorRequest(
                BigDecimal.valueOf(1000),
                null,
                10,
                PeriodType.ANNUAL,
                RateType.YEARLY,
                null,
                null,
                null);

        assertThrows(NullPointerException.class, () -> service.simulate(request));
    }

    @Test
    void shouldValidateRequestAndReturnViolationsForInvalidValues() {

        RetirementSimulatorRequest request = new RetirementSimulatorRequest(
                BigDecimal.valueOf(-1),
                BigDecimal.ZERO,
                0,
                null,
                null,
                null,
                BigDecimal.valueOf(-1),
                BigDecimal.ZERO);

        Set<ConstraintViolation<RetirementSimulatorRequest>> violations = validator.validate(request);

        assertEquals(7, violations.size());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Renda mensal desejada deve ser maior que zero")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Taxa de juros deve ser maior que zero")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Prazo deve ser no mínimo 1")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Tipo de período é obrigatório")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Tipo de taxa é obrigatório")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Inflação anual deve ser >= 0")));
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Taxa de retirada segura deve ser maior que zero")));
    }

    @Test
    void shouldSupportExplicitPercentageInputForMonthlyRate() {

        RetirementSimulatorRequest request = new RetirementSimulatorRequest(
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(0.95),
                25,
                PeriodType.ANNUAL,
                RateType.MONTHLY,
                RateInputType.PERCENTAGE,
                BigDecimal.valueOf(0.04),
                BigDecimal.valueOf(0.1));

        RetirementSimulatorResponse response = service.simulate(request);

        assertEquals(BigDecimal.valueOf(13329.18).setScale(2), response.getInflationAdjustedMonthlyIncome());
        assertEquals(BigDecimal.valueOf(1599501.80).setScale(2), response.getTargetAmount());
        assertEquals(BigDecimal.valueOf(946.35).setScale(2), response.getRequiredMonthlyContribution());
        assertEquals(300, response.getMonthsToRetirement());
    }

    @Test
    void shouldTreatLegacyMonthlyRateBelowOneAsPercentageWhenInputTypeIsNotInformed() {

        RetirementSimulatorRequest request = new RetirementSimulatorRequest(
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(0.95),
                25,
                PeriodType.ANNUAL,
                RateType.MONTHLY,
                null,
                BigDecimal.valueOf(0.04),
                BigDecimal.valueOf(0.1));

        RetirementSimulatorResponse response = service.simulate(request);

        assertEquals(BigDecimal.valueOf(946.35).setScale(2), response.getRequiredMonthlyContribution());
    }

    @Test
    void shouldSupportExplicitDecimalInputForMonthlyRate() {

        RetirementSimulatorRequest request = new RetirementSimulatorRequest(
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(0.95),
                25,
                PeriodType.ANNUAL,
                RateType.MONTHLY,
                RateInputType.DECIMAL,
                BigDecimal.valueOf(0.04),
                BigDecimal.valueOf(0.1));

        RetirementSimulatorResponse response = service.simulate(request);

        assertEquals(BigDecimal.valueOf(0.00).setScale(2), response.getRequiredMonthlyContribution());
    }
}
