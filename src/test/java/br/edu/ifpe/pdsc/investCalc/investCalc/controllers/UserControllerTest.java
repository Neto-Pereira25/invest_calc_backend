package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

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

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.FinancialSummaryDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.UserResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.GlobalExceptionHandler;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtAuthenticationFilter;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.FinancialSummaryService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.UserService;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

        @MockBean
        private FinancialSummaryService financialSummaryService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private User user;
    private UserResponse userResponse;
        private FinancialSummaryDTO financialSummaryDTO;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@email.com");
        user.setName("Test User");

        userResponse = UserResponse.from(user);
        financialSummaryDTO = new FinancialSummaryDTO(
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(3500),
                BigDecimal.valueOf(70),
                false,
                false);
    }

    // ==========
    // GET PROFILE
    // ==========

    @Test
    @DisplayName("GET /api/v1/users/profile - should return authenticated user profile successfully")
    void shouldGetAuthenticatedUserProfileSuccessfully() throws Exception {

        when(userService.getAuthenticatedUser(any()))
                .thenReturn(user);

        mockMvc.perform(get("/api/v1/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(userResponse.email()))
                .andExpect(jsonPath("$.data.name").value(userResponse.name()))
                .andExpect(jsonPath("$.message").value("Dados do usuário retornados com sucesso"));
    }

    @Test
    @DisplayName("GET /api/v1/users/profile - should return user data in response")
    void shouldReturnCorrectUserDataStructure() throws Exception {

        when(userService.getAuthenticatedUser(any()))
                .thenReturn(user);

        mockMvc.perform(get("/api/v1/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.id").value(userResponse.id()))
                .andExpect(jsonPath("$.data.email").value(userResponse.email()));
    }

    @Test
    @DisplayName("PATCH /api/v1/users/profile - should update authenticated user name successfully")
    void shouldUpdateAuthenticatedUserNameSuccessfully() throws Exception {

        user.setName("Updated Name");
        when(userService.updateAuthenticatedUserName(any(), any()))
                .thenReturn(user);

        mockMvc.perform(patch("/api/v1/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Name\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Name"))
                .andExpect(jsonPath("$.message").value("Nome do usuário atualizado com sucesso"));
    }

    // ======================
    // GET FINANCIAL SUMMARY
    // ======================

    @Test
    @DisplayName("GET /api/v1/users/financial-summary - should return financial summary successfully")
    void shouldGetFinancialSummarySuccessfully() throws Exception {

        when(userService.getAuthenticatedUser(any()))
                .thenReturn(user);
        when(financialSummaryService.getFinancialSummary(user))
                .thenReturn(financialSummaryDTO);

        mockMvc.perform(get("/api/v1/users/financial-summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.monthlyLimit").value(5000))
                .andExpect(jsonPath("$.data.monthlyExpenseTotal").value(3500))
                .andExpect(jsonPath("$.data.percentageUsed").value(70))
                .andExpect(jsonPath("$.data.isNearLimit").value(false))
                .andExpect(jsonPath("$.data.isExceeded").value(false))
                .andExpect(jsonPath("$.message").value("Resumo financeiro carregado com sucesso"));
    }

    @Test
    @DisplayName("GET /api/v1/users/financial-summary - should return summary data structure")
    void shouldReturnFinancialSummaryDataStructure() throws Exception {

        when(userService.getAuthenticatedUser(any()))
                .thenReturn(user);
        when(financialSummaryService.getFinancialSummary(user))
                .thenReturn(financialSummaryDTO);

        mockMvc.perform(get("/api/v1/users/financial-summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.monthlyLimit").value(5000))
                .andExpect(jsonPath("$.data.monthlyExpenseTotal").value(3500));
    }
}
