package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.CompoundInterestSimulatorRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.CompoundInterestSimulatorResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.PeriodType;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.RateType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class CompoundInterestSimulatorServiceTest {

    private CompoundInterestSimulatorService service;
    private Validator validator;

    @BeforeEach
    void setup() {
        service = new CompoundInterestSimulatorService();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldSimulateMonthlyRateAndPeriodSuccessfully() {

        CompoundInterestSimulatorRequest request = new CompoundInterestSimulatorRequest(
                BigDecimal.valueOf(1000),
                BigDecimal.ZERO,
                BigDecimal.valueOf(10),
                1,
                PeriodType.MONTHLY,
                RateType.MONTHLY);

        CompoundInterestSimulatorResponse response = service.simulate(request);

        assertEquals(BigDecimal.valueOf(1000.00).setScale(2), response.getTotalInvested());
        assertEquals(BigDecimal.valueOf(100.00).setScale(2), response.getTotalInterest());
        assertEquals(BigDecimal.valueOf(1100.00).setScale(2), response.getFinalAmount());
    }

    @Test
    void shouldConvertAnnualPeriodToMonthsSuccessfully() {

        CompoundInterestSimulatorRequest request = new CompoundInterestSimulatorRequest(
                BigDecimal.valueOf(1000),
                BigDecimal.ZERO,
                BigDecimal.valueOf(1),
                1,
                PeriodType.ANNUAL,
                RateType.MONTHLY);

        CompoundInterestSimulatorResponse response = service.simulate(request);

        assertEquals(BigDecimal.valueOf(1000.00).setScale(2), response.getTotalInvested());
        assertEquals(BigDecimal.valueOf(126.83).setScale(2), response.getTotalInterest());
        assertEquals(BigDecimal.valueOf(1126.83).setScale(2), response.getFinalAmount());
    }

    @Test
    void shouldConvertAnnualRateToEquivalentMonthlySuccessfully() {

        CompoundInterestSimulatorRequest request = new CompoundInterestSimulatorRequest(
                BigDecimal.valueOf(1000),
                BigDecimal.ZERO,
                BigDecimal.valueOf(12),
                12,
                PeriodType.MONTHLY,
                RateType.YEARLY);

        CompoundInterestSimulatorResponse response = service.simulate(request);

        assertEquals(BigDecimal.valueOf(1000.00).setScale(2), response.getTotalInvested());
        assertEquals(BigDecimal.valueOf(120.00).setScale(2), response.getTotalInterest());
        assertEquals(BigDecimal.valueOf(1120.00).setScale(2), response.getFinalAmount());
    }

    @Test
    void shouldHandleZeroInitialValueWithMonthlyContributions() {

        CompoundInterestSimulatorRequest request = new CompoundInterestSimulatorRequest(
                BigDecimal.ZERO,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(1),
                2,
                PeriodType.MONTHLY,
                RateType.MONTHLY);

        CompoundInterestSimulatorResponse response = service.simulate(request);

        assertEquals(BigDecimal.valueOf(200.00).setScale(2), response.getTotalInvested());
        assertEquals(BigDecimal.valueOf(1.00).setScale(2), response.getTotalInterest());
        assertEquals(BigDecimal.valueOf(201.00).setScale(2), response.getFinalAmount());
    }

    @Test
    void shouldThrowExceptionWhenRequestIsNull() {
        assertThrows(NullPointerException.class, () -> service.simulate(null));
    }

    @Test
    void shouldThrowExceptionWhenInterestRateIsNull() {

        CompoundInterestSimulatorRequest request = new CompoundInterestSimulatorRequest(
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(100),
                null,
                12,
                PeriodType.MONTHLY,
                RateType.MONTHLY);

        assertThrows(NullPointerException.class, () -> service.simulate(request));
    }

    @Test
    void shouldValidateRequestAndReturnViolationsForInvalidValues() {

        CompoundInterestSimulatorRequest request = new CompoundInterestSimulatorRequest(
                BigDecimal.valueOf(-1),
                BigDecimal.valueOf(-10),
                BigDecimal.ZERO,
                0,
                null,
                null);

        Set<ConstraintViolation<CompoundInterestSimulatorRequest>> violations = validator.validate(request);

        assertEquals(6, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Valor inicial deve ser >= 0")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Aporte deve ser >= 0")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Taxa deve ser maior que zero")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Período deve ser no mínimo 1")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Tipo de período é obrigatório")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Tipo de taxa é obrigatório")));
    }

    @Test
    void shouldValidateRequestWithNoViolationsForValidValues() {

        CompoundInterestSimulatorRequest request = new CompoundInterestSimulatorRequest(
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(1),
                12,
                PeriodType.MONTHLY,
                RateType.MONTHLY);

        Set<ConstraintViolation<CompoundInterestSimulatorRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }
}
