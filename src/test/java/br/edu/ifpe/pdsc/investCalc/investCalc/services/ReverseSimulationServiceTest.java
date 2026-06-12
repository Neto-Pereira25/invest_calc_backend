package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ReverseSimulationRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ReverseSimulationResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.PeriodType;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.RateInputType;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.RateType;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.ReverseSimulationMode;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.InvalidReverseSimulationRequestException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ReverseSimulationServiceTest {

    private ReverseSimulationService service;
    private Validator validator;

    @BeforeEach
    void setup() {
        service = new ReverseSimulationService();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCalculateRequiredMonthlyContributionSuccessfully() {

        ReverseSimulationRequest request = new ReverseSimulationRequest(
                BigDecimal.valueOf(100000),
                BigDecimal.valueOf(12),
                RateType.YEARLY,
                null,
                ReverseSimulationMode.CALCULATE_CONTRIBUTION,
                10,
                PeriodType.ANNUAL,
                null);

        ReverseSimulationResponse response = service.simulate(request);

        assertEquals(ReverseSimulationMode.CALCULATE_CONTRIBUTION, response.getMode());
        assertEquals(BigDecimal.valueOf(100000.00).setScale(2), response.getTargetAmount());
        assertEquals(10, response.getInformedPeriod());
        assertEquals(PeriodType.ANNUAL, response.getInformedPeriodType());
        assertEquals(BigDecimal.valueOf(450.59).setScale(2), response.getRequiredMonthlyContribution());
    }

    @Test
    void shouldCalculateRequiredPeriodSuccessfully() {

        ReverseSimulationRequest request = new ReverseSimulationRequest(
                BigDecimal.valueOf(100000),
                BigDecimal.valueOf(0.01),
                RateType.MONTHLY,
                RateInputType.DECIMAL,
                ReverseSimulationMode.CALCULATE_PERIOD,
                null,
                null,
                BigDecimal.valueOf(500));

        ReverseSimulationResponse response = service.simulate(request);

        assertEquals(ReverseSimulationMode.CALCULATE_PERIOD, response.getMode());
        assertEquals(BigDecimal.valueOf(500.00).setScale(2), response.getInformedMonthlyContribution());
        assertEquals(111, response.getRequiredPeriodMonths());
        assertEquals(BigDecimal.valueOf(9.25).setScale(2), response.getRequiredPeriodYears());
    }

    @Test
    void shouldSupportDecimalRateInput() {

        ReverseSimulationRequest request = new ReverseSimulationRequest(
                BigDecimal.valueOf(100000),
                BigDecimal.valueOf(0.12),
                RateType.YEARLY,
                null,
                ReverseSimulationMode.CALCULATE_CONTRIBUTION,
                10,
                PeriodType.ANNUAL,
                null);

        ReverseSimulationResponse response = service.simulate(request);

        assertEquals(BigDecimal.valueOf(450.59).setScale(2), response.getRequiredMonthlyContribution());
    }

    @Test
    void shouldThrowExceptionWhenContributionModeDoesNotHavePeriodData() {

        ReverseSimulationRequest request = new ReverseSimulationRequest(
                BigDecimal.valueOf(100000),
                BigDecimal.valueOf(12),
                RateType.YEARLY,
                null,
                ReverseSimulationMode.CALCULATE_CONTRIBUTION,
                null,
                null,
                null);

        assertThrows(InvalidReverseSimulationRequestException.class, () -> service.simulate(request));
    }

    @Test
    void shouldThrowExceptionWhenPeriodModeDoesNotHaveContribution() {

        ReverseSimulationRequest request = new ReverseSimulationRequest(
                BigDecimal.valueOf(100000),
                BigDecimal.valueOf(1),
                RateType.MONTHLY,
                null,
                ReverseSimulationMode.CALCULATE_PERIOD,
                null,
                null,
                null);

        assertThrows(InvalidReverseSimulationRequestException.class, () -> service.simulate(request));
    }

    @Test
    void shouldValidateRequestAndReturnViolationsForInvalidValues() {

        ReverseSimulationRequest request = new ReverseSimulationRequest(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                null,
                null,
                null,
                0,
                null,
                BigDecimal.ZERO);

        Set<ConstraintViolation<ReverseSimulationRequest>> violations = validator.validate(request);

        assertEquals(6, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Valor objetivo deve ser maior que zero")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Taxa de juros deve ser maior que zero")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Tipo de taxa é obrigatório")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Modo da simulação reversa é obrigatório")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Período deve ser no mínimo 1")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Aporte mensal deve ser maior que zero")));
    }

    @Test
    void shouldTreatLegacyMonthlyRateBelowOneAsPercentageWhenInputTypeIsNotInformed() {

        ReverseSimulationRequest request = new ReverseSimulationRequest(
                BigDecimal.valueOf(100000),
                BigDecimal.valueOf(0.95),
                RateType.MONTHLY,
                null,
                ReverseSimulationMode.CALCULATE_CONTRIBUTION,
                10,
                PeriodType.ANNUAL,
                null);

        ReverseSimulationResponse response = service.simulate(request);

        assertEquals(BigDecimal.valueOf(450.24).setScale(2), response.getRequiredMonthlyContribution());
    }

    @Test
    void shouldSupportExplicitDecimalInputForMonthlyRate() {

        ReverseSimulationRequest request = new ReverseSimulationRequest(
                BigDecimal.valueOf(100000),
                BigDecimal.valueOf(0.95),
                RateType.MONTHLY,
                RateInputType.DECIMAL,
                ReverseSimulationMode.CALCULATE_CONTRIBUTION,
                10,
                PeriodType.ANNUAL,
                null);

        ReverseSimulationResponse response = service.simulate(request);

        assertEquals(BigDecimal.valueOf(0.00).setScale(2), response.getRequiredMonthlyContribution());
    }
}
