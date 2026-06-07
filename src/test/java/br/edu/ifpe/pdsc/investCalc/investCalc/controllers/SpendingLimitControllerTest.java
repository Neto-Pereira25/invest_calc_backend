package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.monthlySpendingLimit.SpendingLimitResponseDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.GlobalExceptionHandler;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.UserNotFoundException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.monthlySpendingLimit.SpendingLimitAlreadyExistsException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.monthlySpendingLimit.SpendingLimitNotConfiguredException;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtAuthenticationFilter;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.SpendingLimitService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.UserService;

@WebMvcTest(controllers = SpendingLimitController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class SpendingLimitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpendingLimitService spendingLimitService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private User user;
    private SpendingLimitResponseDTO response;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@email.com");

        response = new SpendingLimitResponseDTO(
                1L,
                BigDecimal.valueOf(1500),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now());
    }

    @Test
    @DisplayName("POST /api/v1/spending-limit - should create spending limit successfully")
    void shouldCreateSpendingLimitSuccessfully() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        when(spendingLimitService.createLimit(any(), any())).thenReturn(response);

        String requestBody = """
                {
                    "amount": 1500
                }
                """;

        mockMvc.perform(post("/api/v1/spending-limit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.amount").value(1500))
                .andExpect(jsonPath("$.message").value("Limite mensal criado com sucesso"));
    }

    @Test
    @DisplayName("POST /api/v1/spending-limit - should return validation error")
    void shouldReturnValidationErrorWhenCreatingInvalidLimit() throws Exception {
        String requestBody = """
                {
                    "amount": 0
                }
                """;

        mockMvc.perform(post("/api/v1/spending-limit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.message").value("Erro de validacao"));
    }

    @Test
    @DisplayName("POST /api/v1/spending-limit - should return 400 when limit already exists")
    void shouldReturn400WhenLimitAlreadyExists() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        when(spendingLimitService.createLimit(any(), any())).thenThrow(new SpendingLimitAlreadyExistsException());

        String requestBody = """
                {
                    "amount": 1800
                }
                """;

        mockMvc.perform(post("/api/v1/spending-limit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Limite mensal ja configurado para este usuario."));
    }

    @Test
    @DisplayName("GET /api/v1/spending-limit - should return configured limit")
    void shouldGetConfiguredLimit() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        when(spendingLimitService.getLimit(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/spending-limit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.amount").value(1500))
                .andExpect(jsonPath("$.message").value("Limite encontrado com sucesso"));
    }

    @Test
    @DisplayName("GET /api/v1/spending-limit - should return message when no limit is configured")
    void shouldReturnMessageWhenNoLimitIsConfigured() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        when(spendingLimitService.getLimit(any())).thenReturn(null);

        mockMvc.perform(get("/api/v1/spending-limit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Nenhum limite configurado"));
    }

    @Test
    @DisplayName("PUT /api/v1/spending-limit - should update spending limit successfully")
    void shouldUpdateSpendingLimitSuccessfully() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        when(spendingLimitService.updateLimit(any(), any())).thenReturn(response);

        String requestBody = """
                {
                    "amount": 2000
                }
                """;

        mockMvc.perform(put("/api/v1/spending-limit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.amount").value(1500))
                .andExpect(jsonPath("$.message").value("Limite atualizado com sucesso"));
    }

    @Test
    @DisplayName("PUT /api/v1/spending-limit - should return 404 when limit is not configured")
    void shouldReturn404WhenUpdatingWithoutConfiguredLimit() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        when(spendingLimitService.updateLimit(any(), any())).thenThrow(new SpendingLimitNotConfiguredException());

        String requestBody = """
                {
                    "amount": 2000
                }
                """;

        mockMvc.perform(put("/api/v1/spending-limit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nenhum limite mensal configurado."));
    }

    @Test
    @DisplayName("DELETE /api/v1/spending-limit - should delete spending limit successfully")
    void shouldDeleteSpendingLimitSuccessfully() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        doNothing().when(spendingLimitService).deleteLimit(any());

        mockMvc.perform(delete("/api/v1/spending-limit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Limite removido com sucesso"));
    }

    @Test
    @DisplayName("DELETE /api/v1/spending-limit - should return 404 when limit is not configured")
    void shouldReturn404WhenDeletingWithoutConfiguredLimit() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        doThrow(new SpendingLimitNotConfiguredException()).when(spendingLimitService).deleteLimit(any());

        mockMvc.perform(delete("/api/v1/spending-limit"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nenhum limite mensal configurado."));
    }

    @Test
    @DisplayName("GET /api/v1/spending-limit - should return 500 on unexpected error")
    void shouldReturn500WhenUnexpectedErrorOccurs() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        when(spendingLimitService.getLimit(any())).thenThrow(new RuntimeException("erro inesperado"));

        mockMvc.perform(get("/api/v1/spending-limit"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro interno do servidor"));
    }

    @Test
    @DisplayName("GET /api/v1/spending-limit - should return 404 when authenticated user is not found")
    void shouldReturn404WhenAuthenticatedUserIsNotFound() throws Exception {
        when(userService.getAuthenticatedUser(any())).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/api/v1/spending-limit"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuario nao encontrado"));
    }
}