package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.scenarioComparison.ScenarioComparisonResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.GlobalExceptionHandler;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.CustomUserDetailsService;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtAuthenticationFilter;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.AuthService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.ScenarioComparisonService;

@WebMvcTest(controllers = ScenarioComparisonController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ScenarioComparisonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScenarioComparisonService scenarioComparisonService;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("POST /api/v1/simulations/compare - should compare scenarios successfully")
    void shouldCompareScenariosSuccessfully() throws Exception {

        List<ScenarioComparisonResponse> mockResponse = List.of(
                new ScenarioComparisonResponse(
                        "Cenário A",
                        BigDecimal.valueOf(25000),
                        BigDecimal.valueOf(18000),
                        BigDecimal.valueOf(43000)),
                new ScenarioComparisonResponse(
                        "Cenário B",
                        BigDecimal.valueOf(49000),
                        BigDecimal.valueOf(35000),
                        BigDecimal.valueOf(84000)));

        when(scenarioComparisonService.compareScenarios(any())).thenReturn(mockResponse);

        String requestBody = """
                {
                    "scenarios": [
                        {
                            "name": "Cenário A",
                            "initialCapital": 1000,
                            "monthlyContribution": 200,
                            "interestRate": 1,
                            "months": 120
                        },
                        {
                            "name": "Cenário B",
                            "initialCapital": 1000,
                            "monthlyContribution": 400,
                            "interestRate": 1,
                            "months": 120
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/v1/simulations/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Comparação de cenários realizada com sucesso"))
                .andExpect(jsonPath("$.data[0].scenarioName").value("Cenário A"))
                .andExpect(jsonPath("$.data[0].finalAmount").value(43000))
                .andExpect(jsonPath("$.data[1].scenarioName").value("Cenário B"))
                .andExpect(jsonPath("$.data[1].finalAmount").value(84000));
    }

    @Test
    @DisplayName("POST /api/v1/simulations/compare - should return 422 when only one scenario is informed")
    void shouldReturn422WhenOnlyOneScenarioIsInformed() throws Exception {

        String requestBody = """
                {
                    "scenarios": [
                        {
                            "name": "Cenário A",
                            "initialCapital": 1000,
                            "monthlyContribution": 200,
                            "interestRate": 1,
                            "months": 120
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/v1/simulations/compare")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Erro de validacao"));

        verifyNoInteractions(scenarioComparisonService);
    }
}
