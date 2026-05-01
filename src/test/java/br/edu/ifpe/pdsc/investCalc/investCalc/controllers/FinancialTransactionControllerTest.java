package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
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

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.FinancialTransactionResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.GlobalExceptionHandler;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.transaction.TransactionNotFoundException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.transaction.UnauthorizedTransactionAccessException;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtAuthenticationFilter;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.FinancialTransactionService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.UserService;

@WebMvcTest(controllers = FinancialTransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class FinancialTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FinancialTransactionService transactionService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private User user;
    private FinancialTransactionResponse response;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@email.com");

        response = new FinancialTransactionResponse(
                1L,
                "Almoço",
                BigDecimal.valueOf(50),
                null,
                "Alimentação",
                "Restaurante",
                LocalDate.now());
    }

    // ==========
    // CREATE
    // ==========

    @Test
    @DisplayName("POST /api/v1/financial-transactions - should create transaction successfully")
    void shouldCreateTransactionSuccessfully() throws Exception {

        when(userService.getAuthenticatedUser(any())).thenReturn(user);

        when(transactionService.createFinancialTransaction(any(), any()))
                .thenReturn(response);

        String requestBody = """
                    {
                        "amount": 100,
                        "description": "Almoço",
                        "date": "2026-05-01",
                        "subcategoryId": 1
                    }
                """;

        mockMvc.perform(post("/api/v1/financial-transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.description").value("Almoço"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /api/v1/financial-transactions - should return validation error")
    void shouldReturnValidationErrorWhenCreatingInvalidTransaction() throws Exception {

        String requestBody = """
                {
                    "amount": 0,
                    "description": "",
                    "date": null,
                    "subcategoryId": null
                }
                """;

        mockMvc.perform(post("/api/v1/financial-transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.message").value("Erro de validacao"));
    }

    // =========================
    // LIST
    // =========================

    @Test
    @DisplayName("GET /api/v1/financial-transactions - should list transactions")
    void shouldListTransactions() throws Exception {

        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        when(transactionService.listByUser(user))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/financial-transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].description").value("Almoço"));
    }

    @Test
    @DisplayName("GET /api/v1/financial-transactions - should return empty list")
    void shouldReturnEmptyList() throws Exception {

        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        when(transactionService.listByUser(user))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/financial-transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // =========================
    // UPDATE
    // =========================

    @Test
    @DisplayName("PUT /api/v1/financial-transactions/{id} - should update successfully")
    void shouldUpdateTransactionSuccessfully() throws Exception {

        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        when(transactionService.updateFinancialTransaction(any(), any(), any()))
                .thenReturn(response);

        String requestBody = """
                {
                    "amount": 150,
                    "description": "Jantar",
                    "date": "2026-05-01",
                    "subcategoryId": 1
                }
                """;

        mockMvc.perform(put("/api/v1/financial-transactions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.description").value("Almoço"));
    }

    @Test
    @DisplayName("PUT /api/v1/financial-transactions/{id} - should return 404")
    void shouldReturn404WhenTransactionNotFound() throws Exception {

        when(userService.getAuthenticatedUser(any())).thenReturn(user);

        when(transactionService.updateFinancialTransaction(any(), any(), any()))
                .thenThrow(new TransactionNotFoundException());

        String requestBody = """
                {
                    "amount": 150,
                    "description": "Jantar",
                    "date": "2026-05-01",
                    "subcategoryId": 1
                }
                """;

        mockMvc.perform(put("/api/v1/financial-transactions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Transacao nao encontrada"));
    }

    // =========================
    // DELETE
    // =========================

    @Test
    @DisplayName("DELETE /api/v1/financial-transactions/{id} - should delete successfully")
    void shouldDeleteTransactionSuccessfully() throws Exception {

        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        doNothing().when(transactionService).deleteFinancialTransaction(any(), any());

        mockMvc.perform(delete("/api/v1/financial-transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transação financeira deletada com sucesso."));
    }

    @Test
    @DisplayName("DELETE /api/v1/financial-transactions/{id} - should return 403 when access denied")
    void shouldReturn403WhenDeletingFromAnotherUser() throws Exception {

        when(userService.getAuthenticatedUser(any())).thenReturn(user);

        doThrow(new UnauthorizedTransactionAccessException())
                .when(transactionService).deleteFinancialTransaction(any(), any());

        mockMvc.perform(delete("/api/v1/financial-transactions/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Usuario nao autorizado a realizar esta operacao"));
    }
}
