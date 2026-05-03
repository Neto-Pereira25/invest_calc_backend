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

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.CompoundInterestSimulatorResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.GlobalExceptionHandler;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.CustomUserDetailsService;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtAuthenticationFilter;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.AuthService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.CompoundInterestSimulatorService;

@WebMvcTest(controllers = CompoundInterestSimulatorController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class CompoundInterestSimulatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompoundInterestSimulatorService simulationService;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("POST /api/v1/compound-interest-simulator - should simulate successfully")
    void shouldSimulateSuccessfully() throws Exception {

        CompoundInterestSimulatorResponse mockResponse = new CompoundInterestSimulatorResponse(
                BigDecimal.valueOf(4600.00),
                BigDecimal.valueOf(775.21),
                BigDecimal.valueOf(5375.21));

        when(simulationService.simulate(any())).thenReturn(mockResponse);

        String requestBody = """
                {
                    "initialValue": 1000,
                    "monthlyContribution": 300,
                    "interestRate": 1.1,
                    "period": 12,
                    "periodType": "MONTHLY",
                    "rateType": "MONTHLY"
                }
                """;

        mockMvc.perform(post("/api/v1/compound-interest-simulator")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Simulação realizada com sucesso"))
                .andExpect(jsonPath("$.data.totalInvested").value(4600.00))
                .andExpect(jsonPath("$.data.totalInterest").value(775.21))
                .andExpect(jsonPath("$.data.finalAmount").value(5375.21));
    }

    @Test
    @DisplayName("POST /api/v1/compound-interest-simulator - should return validation error")
    void shouldReturn422WhenRequestIsInvalid() throws Exception {

        String requestBody = """
                {
                    "initialValue": -1,
                    "monthlyContribution": 100,
                    "interestRate": 0,
                    "period": 0,
                    "periodType": "MONTHLY",
                    "rateType": "MONTHLY"
                }
                """;

        mockMvc.perform(post("/api/v1/compound-interest-simulator")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Erro de validacao"))
                .andExpect(jsonPath("$.data").isArray());

        verifyNoInteractions(simulationService);
    }

    @Test
    @DisplayName("POST /api/v1/compound-interest-simulator - should return 400 for invalid enum")
    void shouldReturn400WhenEnumValueIsInvalid() throws Exception {

        String requestBody = """
                {
                    "initialValue": 1000,
                    "monthlyContribution": 300,
                    "interestRate": 1.2,
                    "period": 12,
                    "periodType": "INVALID",
                    "rateType": "MONTHLY"
                }
                """;

        mockMvc.perform(post("/api/v1/compound-interest-simulator")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Valor inválido para 'periodType'. Valores permitidos: [ANNUAL, MONTHLY]"));

        verifyNoInteractions(simulationService);
    }

    @Test
    @DisplayName("POST /api/v1/compound-interest-simulator - should return 500 for unexpected errors")
    void shouldReturn500WhenServiceThrowsUnexpectedException() throws Exception {

        when(simulationService.simulate(any())).thenThrow(new RuntimeException("erro inesperado"));

        String requestBody = """
                {
                    "initialValue": 1000,
                    "monthlyContribution": 100,
                    "interestRate": 1.2,
                    "period": 12,
                    "periodType": "MONTHLY",
                    "rateType": "MONTHLY"
                }
                """;

        mockMvc.perform(post("/api/v1/compound-interest-simulator")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro interno do servidor"));
    }
}
