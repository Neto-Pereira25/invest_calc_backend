package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.RetirementSimulatorResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.GlobalExceptionHandler;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.CustomUserDetailsService;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtAuthenticationFilter;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.AuthService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.RetirementSimulatorService;

@WebMvcTest(controllers = RetirementSimulatorController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class RetirementSimulatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RetirementSimulatorService retirementSimulatorService;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("POST /api/v1/retirement-simulator - should simulate successfully")
    void shouldSimulateSuccessfully() throws Exception {

        RetirementSimulatorResponse mockResponse = new RetirementSimulatorResponse(
                BigDecimal.valueOf(1000.00),
                BigDecimal.valueOf(1200.00),
                BigDecimal.valueOf(360000.00),
                BigDecimal.valueOf(1585.52),
                BigDecimal.valueOf(3.50),
                BigDecimal.valueOf(4.00),
                120);

        when(retirementSimulatorService.simulate(any())).thenReturn(mockResponse);

        String requestBody = """
                {
                    "desiredMonthlyIncome": 1000,
                    "interestRate": 12,
                    "period": 10,
                    "periodType": "ANNUAL",
                    "rateType": "YEARLY",
                    "annualInflationRate": 3.5,
                    "safeWithdrawalRate": 4
                }
                """;

        mockMvc.perform(post("/api/v1/retirement-simulator")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Simulação realizada com sucesso"))
                .andExpect(jsonPath("$.data.targetAmount").value(360000.00))
                .andExpect(jsonPath("$.data.requiredMonthlyContribution").value(1585.52))
                .andExpect(jsonPath("$.data.usedAnnualInflationRate").value(3.50))
                .andExpect(jsonPath("$.data.usedSafeWithdrawalRate").value(4.00));
    }

    @Test
    @DisplayName("POST /api/v1/retirement-simulator - should return validation error")
    void shouldReturn422WhenRequestIsInvalid() throws Exception {

        String requestBody = """
                {
                    "desiredMonthlyIncome": 0,
                    "interestRate": 0,
                    "period": 0,
                    "periodType": "ANNUAL",
                    "rateType": "YEARLY",
                    "annualInflationRate": -1,
                    "safeWithdrawalRate": 0
                }
                """;

        mockMvc.perform(post("/api/v1/retirement-simulator")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Erro de validacao"))
                .andExpect(jsonPath("$.data").isArray());

        verifyNoInteractions(retirementSimulatorService);
    }

    @Test
    @DisplayName("POST /api/v1/retirement-simulator - should return 400 for invalid enum")
    void shouldReturn400WhenEnumValueIsInvalid() throws Exception {

        String requestBody = """
                {
                    "desiredMonthlyIncome": 1000,
                    "interestRate": 12,
                    "period": 10,
                    "periodType": "INVALID",
                    "rateType": "YEARLY"
                }
                """;

        mockMvc.perform(post("/api/v1/retirement-simulator")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Valor inválido para 'periodType'. Valores permitidos: [ANNUAL, MONTHLY]"));

        verifyNoInteractions(retirementSimulatorService);
    }

    @Test
    @DisplayName("POST /api/v1/retirement-simulator - should return 500 for unexpected errors")
    void shouldReturn500WhenServiceThrowsUnexpectedException() throws Exception {

        when(retirementSimulatorService.simulate(any())).thenThrow(new RuntimeException("erro inesperado"));

        String requestBody = """
                {
                    "desiredMonthlyIncome": 1000,
                    "interestRate": 12,
                    "period": 10,
                    "periodType": "ANNUAL",
                    "rateType": "YEARLY"
                }
                """;

        mockMvc.perform(post("/api/v1/retirement-simulator")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro interno do servidor"));
    }
}
