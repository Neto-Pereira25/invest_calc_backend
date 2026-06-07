package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
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

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.goals.GoalResponseDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.GoalStatus;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.GlobalExceptionHandler;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.UserNotFoundException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.goals.GoalNotFoundException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.goals.UnauthorizedGoalAccessException;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtAuthenticationFilter;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.GoalService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.UserService;

@WebMvcTest(controllers = GoalController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class GoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoalService goalService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private User user;
    private GoalResponseDTO response;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@email.com");

        response = new GoalResponseDTO();
        response.setId(1L);
        response.setName("Reserva de emergência");
        response.setTargetAmount(BigDecimal.valueOf(10000));
        response.setCurrentAmount(BigDecimal.valueOf(3500));
        response.setProgressPercentage(BigDecimal.valueOf(35.00));
        response.setDeadline(LocalDate.now().plusMonths(12));
        response.setStatus(GoalStatus.ACTIVE);
        response.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("POST /api/v1/goals - should create goal successfully")
    void shouldCreateGoalSuccessfully() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        when(goalService.createGoal(any(), any())).thenReturn(response);

        String requestBody = """
                {
                    "name": "Reserva de emergência",
                    "targetAmount": 10000,
                    "deadline": "2026-12-31"
                }
                """;

        mockMvc.perform(post("/api/v1/goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Reserva de emergência"))
                .andExpect(jsonPath("$.message").value("Goal created successfully."));
    }

    @Test
    @DisplayName("POST /api/v1/goals - should return validation error")
    void shouldReturnValidationErrorWhenCreatingInvalidGoal() throws Exception {
        String requestBody = """
                {
                    "name": "",
                    "targetAmount": 0,
                    "deadline": null
                }
                """;

        mockMvc.perform(post("/api/v1/goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.message").value("Erro de validacao"));
    }

    @Test
    @DisplayName("GET /api/v1/goals - should list goals")
    void shouldListGoals() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        when(goalService.getUserGoals(any())).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/goals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Reserva de emergência"))
                .andExpect(jsonPath("$.message").value("Goals retrieved successfully."));
    }

    @Test
    @DisplayName("GET /api/v1/goals/{id} - should return 404")
    void shouldReturn404WhenGoalNotFound() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        when(goalService.getGoalById(any(), any())).thenThrow(new GoalNotFoundException());

        mockMvc.perform(get("/api/v1/goals/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Meta financeira não encontrada"));
    }

    @Test
    @DisplayName("PUT /api/v1/goals/{id} - should update goal successfully")
    void shouldUpdateGoalSuccessfully() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        when(goalService.updateGoal(any(), any(), any())).thenReturn(response);

        String requestBody = """
                {
                    "name": "Novo nome da meta",
                    "targetAmount": 12000,
                    "deadline": "2027-02-01"
                }
                """;

        mockMvc.perform(put("/api/v1/goals/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Reserva de emergência"))
                .andExpect(jsonPath("$.message").value("Goal updated successfully."));
    }

    @Test
    @DisplayName("PATCH /api/v1/goals/{id}/progress - should update progress successfully")
    void shouldUpdateGoalProgressSuccessfully() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        when(goalService.updateGoalProgress(any(), any(), any())).thenReturn(response);

        String requestBody = """
                {
                    "currentAmount": 5000
                }
                """;

        mockMvc.perform(patch("/api/v1/goals/1/progress")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentAmount").value(3500))
                .andExpect(jsonPath("$.message").value("Goal progress updated successfully."));
    }

    @Test
    @DisplayName("DELETE /api/v1/goals/{id} - should delete goal successfully")
    void shouldDeleteGoalSuccessfully() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        doNothing().when(goalService).deleteGoal(any(), any());

        mockMvc.perform(delete("/api/v1/goals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Goal deleted successfully."));
    }

    @Test
    @DisplayName("DELETE /api/v1/goals/{id} - should return 403 when access denied")
    void shouldReturn403WhenDeletingGoalFromAnotherUser() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        doThrow(new UnauthorizedGoalAccessException()).when(goalService).deleteGoal(any(), any());

        mockMvc.perform(delete("/api/v1/goals/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Voce nao possui permissao para acessar esta meta financeira"));
    }

    @Test
    @DisplayName("GET /api/v1/goals - should return 404 when authenticated user is not found")
    void shouldReturn404WhenAuthenticatedUserIsNotFound() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/api/v1/goals"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuario nao encontrado"));
    }
}
