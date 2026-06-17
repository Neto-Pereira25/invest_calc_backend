package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileHistoryDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileResponseDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.userFinancialProfile.FinancialProfile;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.GlobalExceptionHandler;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.UserNotFoundException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.userFinancialProfile.FinancialProfileNotFoundException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.userFinancialProfile.InvalidFinancialProfileAssessmentException;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtAuthenticationFilter;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.UserService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile.FinancialProfileService;

@WebMvcTest(controllers = FinancialProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class FinancialProfileControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private FinancialProfileService financialProfileService;

        @MockBean
        private UserService userService;

        @MockBean
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        private User authenticatedUser;
        private FinancialProfileResponseDTO currentProfileResponse;

        @BeforeEach
        void setup() {
                authenticatedUser = User.builder()
                                .id(1L)
                                .email("user@email.com")
                                .build();

                currentProfileResponse = FinancialProfileResponseDTO.builder()
                                .profile(FinancialProfile.INVESTIDOR)
                                .description("Perfil com foco em crescimento de patrimonio")
                                .strengths(List.of("Visao de longo prazo", "Disciplina financeira"))
                                .limitations(List.of("Possivel excesso de confianca"))
                                .recommendations(List.of("Diversificar patrimonio", "Revisar metas"))
                                .suggestedGoals(List.of("Aumentar renda passiva", "Independencia financeira"))
                                .devedorScore(1)
                                .gastadorScore(2)
                                .desligadoScore(3)
                                .poupadorScore(6)
                                .investidorScore(8)
                                .devedorPercentage(5.88)
                                .gastadorPercentage(13.33)
                                .desligadoPercentage(17.65)
                                .poupadorPercentage(25.0)
                                .investidorPercentage(34.78)
                                .assessedAt(LocalDateTime.of(2026, 1, 10, 10, 0))
                                .build();
        }

        @Test
        @DisplayName("POST /api/v1/financial-profile - should return 201 when assessment is valid")
        void shouldSubmitAssessmentSuccessfully() throws Exception {
                when(userService.getAuthenticatedUser(any())).thenReturn(authenticatedUser);
                when(financialProfileService.submitAssessment(any(), any())).thenReturn(currentProfileResponse);

                String requestBody = """
                                {
                                  "answers": [
                                    {"questionNumber": 1, "selectedOption": "A"},
                                    {"questionNumber": 2, "selectedOption": "B"},
                                    {"questionNumber": 3, "selectedOption": "C"},
                                    {"questionNumber": 4, "selectedOption": "D"},
                                    {"questionNumber": 5, "selectedOption": "E"},
                                    {"questionNumber": 6, "selectedOption": "A"},
                                    {"questionNumber": 7, "selectedOption": "B"},
                                    {"questionNumber": 8, "selectedOption": "C"},
                                    {"questionNumber": 9, "selectedOption": "D"},
                                    {"questionNumber": 10, "selectedOption": "E"}
                                  ]
                                }
                                """;

                mockMvc.perform(post("/api/v1/financial-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.data.profile").value("INVESTIDOR"))
                                .andExpect(jsonPath("$.data.description")
                                                .value("Perfil com foco em crescimento de patrimonio"))
                                .andExpect(jsonPath("$.data.strengths[0]").value("Visao de longo prazo"))
                                .andExpect(jsonPath("$.data.limitations[0]").value("Possivel excesso de confianca"))
                                .andExpect(jsonPath("$.data.recommendations[0]").value("Diversificar patrimonio"))
                                .andExpect(jsonPath("$.data.suggestedGoals[0]").value("Aumentar renda passiva"))
                                .andExpect(jsonPath("$.message").value("Financial profile successfully calculated."));
        }

        @Test
        @DisplayName("POST /api/v1/financial-profile - should return 422 when request is invalid")
        void shouldReturn422WhenSubmitAssessmentRequestIsInvalid() throws Exception {
                String requestBody = """
                                {
                                  "answers": []
                                }
                                """;

                mockMvc.perform(post("/api/v1/financial-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isUnprocessableEntity())
                                .andExpect(jsonPath("$.data").isArray())
                                .andExpect(jsonPath("$.message").value("Erro de validacao"));
        }

        @Test
        @DisplayName("GET /api/v1/financial-profile - should return current profile")
        void shouldReturnCurrentProfile() throws Exception {
                when(userService.getAuthenticatedUser(any())).thenReturn(authenticatedUser);
                when(financialProfileService.getCurrentProfile(any())).thenReturn(currentProfileResponse);

                mockMvc.perform(get("/api/v1/financial-profile"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.profile").value("INVESTIDOR"))
                                .andExpect(jsonPath("$.data.description")
                                                .value("Perfil com foco em crescimento de patrimonio"))
                                .andExpect(jsonPath("$.data.strengths[1]").value("Disciplina financeira"))
                                .andExpect(jsonPath("$.message").value("Financial profile successfully retrieved."));
        }

        @Test
        @DisplayName("GET /api/v1/financial-profile/history - should return history list")
        void shouldReturnHistoryList() throws Exception {
                when(userService.getAuthenticatedUser(any())).thenReturn(authenticatedUser);

                FinancialProfileHistoryDTO first = FinancialProfileHistoryDTO.builder()
                                .id(2L)
                                .profile(FinancialProfile.INVESTIDOR)
                                .assessedAt(LocalDateTime.of(2026, 2, 1, 10, 0))
                                .build();

                FinancialProfileHistoryDTO second = FinancialProfileHistoryDTO.builder()
                                .id(1L)
                                .profile(FinancialProfile.POUPADOR)
                                .assessedAt(LocalDateTime.of(2026, 1, 15, 10, 0))
                                .build();

                when(financialProfileService.getHistory(any())).thenReturn(List.of(first, second));

                mockMvc.perform(get("/api/v1/financial-profile/history"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data[0].id").value(2))
                                .andExpect(jsonPath("$.data[0].profile").value("INVESTIDOR"))
                                .andExpect(jsonPath("$.data[1].id").value(1))
                                .andExpect(jsonPath("$.message")
                                                .value("Financial profile history successfully retrieved."));
        }

        @Test
        @DisplayName("GET /api/v1/financial-profile - should return 404 when profile does not exist")
        void shouldReturn404WhenCurrentProfileDoesNotExist() throws Exception {
                when(userService.getAuthenticatedUser(any())).thenReturn(authenticatedUser);
                when(financialProfileService.getCurrentProfile(any()))
                                .thenThrow(new FinancialProfileNotFoundException());

                mockMvc.perform(get("/api/v1/financial-profile"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Perfil financeiro nao encontrado"));
        }

        @Test
        @DisplayName("POST /api/v1/financial-profile - should return 422 when assessment is invalid")
        void shouldReturn422WhenAssessmentIsInvalid() throws Exception {
                when(userService.getAuthenticatedUser(any())).thenReturn(authenticatedUser);
                when(financialProfileService.submitAssessment(any(), any()))
                                .thenThrow(new InvalidFinancialProfileAssessmentException(
                                                "O questionario deve conter exatamente 10 respostas."));

                String requestBody = """
                                {
                                  "answers": [
                                    {"questionNumber": 1, "selectedOption": "A"},
                                    {"questionNumber": 2, "selectedOption": "B"}
                                  ]
                                }
                                """;

                mockMvc.perform(post("/api/v1/financial-profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isUnprocessableEntity())
                                .andExpect(jsonPath("$.message")
                                                .value("O questionario deve conter exatamente 10 respostas."));
        }

        @Test
        @DisplayName("GET /api/v1/financial-profile/history - should return 500 on unexpected error")
        void shouldReturn500WhenGetHistoryThrowsUnexpectedError() throws Exception {
                when(userService.getAuthenticatedUser(any())).thenReturn(authenticatedUser);
                when(financialProfileService.getHistory(any())).thenThrow(new RuntimeException("erro inesperado"));

                mockMvc.perform(get("/api/v1/financial-profile/history"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.message").value("Erro interno do servidor"));
        }

        @Test
        @DisplayName("GET /api/v1/financial-profile/history - should return 404 when authenticated user is not found")
        void shouldReturn404WhenAuthenticatedUserIsNotFoundOnHistory() throws Exception {
                when(userService.getAuthenticatedUser(any())).thenThrow(new UserNotFoundException());

                mockMvc.perform(get("/api/v1/financial-profile/history"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Usuario nao encontrado"));
        }
}
