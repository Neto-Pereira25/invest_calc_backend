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

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ReverseSimulationResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.PeriodType;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.ReverseSimulationMode;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.GlobalExceptionHandler;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.InvalidReverseSimulationRequestException;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.CustomUserDetailsService;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtAuthenticationFilter;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.AuthService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.ReverseSimulationService;

@WebMvcTest(controllers = ReverseSimulationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class ReverseSimulationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReverseSimulationService reverseSimulationService;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("POST /api/v1/reverse-simulation - should simulate contribution successfully")
    void shouldSimulateContributionSuccessfully() throws Exception {

        ReverseSimulationResponse mockResponse = new ReverseSimulationResponse(
                ReverseSimulationMode.CALCULATE_CONTRIBUTION,
                BigDecimal.valueOf(100000.00),
                BigDecimal.valueOf(0.9489),
                null,
                10,
                PeriodType.ANNUAL,
                BigDecimal.valueOf(450.59),
                null,
                null);

        when(reverseSimulationService.simulate(any())).thenReturn(mockResponse);

        String requestBody = """
                {
                    "targetAmount": 100000,
                    "interestRate": 12,
                    "rateType": "YEARLY",
                    "mode": "CALCULATE_CONTRIBUTION",
                    "period": 10,
                    "periodType": "ANNUAL"
                }
                """;

        mockMvc.perform(post("/api/v1/reverse-simulation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Simulação reversa realizada com sucesso"))
                .andExpect(jsonPath("$.data.requiredMonthlyContribution").value(450.59));
    }

    @Test
    @DisplayName("POST /api/v1/reverse-simulation - should return validation error")
    void shouldReturn422WhenRequestIsInvalid() throws Exception {

        String requestBody = """
                {
                    "targetAmount": 0,
                    "interestRate": 0,
                    "rateType": "MONTHLY",
                    "mode": "CALCULATE_PERIOD",
                    "monthlyContribution": 0
                }
                """;

        mockMvc.perform(post("/api/v1/reverse-simulation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Erro de validacao"))
                .andExpect(jsonPath("$.data").isArray());

        verifyNoInteractions(reverseSimulationService);
    }

    @Test
    @DisplayName("POST /api/v1/reverse-simulation - should return 400 for invalid enum")
    void shouldReturn400WhenEnumValueIsInvalid() throws Exception {

        String requestBody = """
                {
                    "targetAmount": 100000,
                    "interestRate": 1,
                    "rateType": "MONTHLY",
                    "mode": "INVALID",
                    "monthlyContribution": 500
                }
                """;

        mockMvc.perform(post("/api/v1/reverse-simulation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Valor inválido para 'mode'. Valores permitidos: [CALCULATE_CONTRIBUTION, CALCULATE_PERIOD]"));

        verifyNoInteractions(reverseSimulationService);
    }

    @Test
    @DisplayName("POST /api/v1/reverse-simulation - should return 422 for conditional validation")
    void shouldReturn422WhenServiceThrowsConditionalValidationException() throws Exception {

        when(reverseSimulationService.simulate(any()))
                .thenThrow(new InvalidReverseSimulationRequestException(
                        "Para calcular aporte, informe um período válido"));

        String requestBody = """
                {
                    "targetAmount": 100000,
                    "interestRate": 12,
                    "rateType": "YEARLY",
                    "mode": "CALCULATE_CONTRIBUTION"
                }
                """;

        mockMvc.perform(post("/api/v1/reverse-simulation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Para calcular aporte, informe um período válido"));
    }

    @Test
    @DisplayName("POST /api/v1/reverse-simulation - should return 500 for unexpected errors")
    void shouldReturn500WhenServiceThrowsUnexpectedException() throws Exception {

        when(reverseSimulationService.simulate(any())).thenThrow(new RuntimeException("erro inesperado"));

        String requestBody = """
                {
                    "targetAmount": 100000,
                    "interestRate": 1,
                    "rateType": "MONTHLY",
                    "mode": "CALCULATE_PERIOD",
                    "monthlyContribution": 500
                }
                """;

        mockMvc.perform(post("/api/v1/reverse-simulation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro interno do servidor"));
    }
}
