package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.AuthResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.CustomUserDetailsService;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtAuthenticationFilter;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtService;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.RefreshTokenService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.AuthService;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("POST /auth/register - should return 200 OK and ApiResponse when request is valid")
    void shouldReturnOkWhenRegisterRequestIsValid() throws Exception {

        // ARRANGE
        doNothing().when(authService).register(any());

        String requestBody = """
                {
                    "name": "Maria",
                    "email": "maria@email.com",
                    "password": "12345678"
                }
                """;

        // ACT & ASSERT
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /auth/register - should return 422 Unprocessable Entity with validation errors when request is invalid")
    void shouldReturnUnprocessableEntityWhenRegisterRequestIsInvalid() throws Exception {

        // ARRANGE
        String requestBody = """
                {
                    "name": "Maria",
                    "email": "invalid-email",
                    "password": "123"
                }
                """;

        // ACT & ASSERT
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Erro de validação"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("POST /auth/login - should return 200 and tokens in ApiResponse when request is valid")
    void shouldReturnOkWithTokensWhenLoginRequestIsValid() throws Exception {
        // ARRANGE
        when(authService.login(any())).thenReturn(new AuthResponse("access-token", "refresh-token"));

        String requestBody = """
                {
                    "email": "maria@email.com",
                    "password": "12345678"
                }
                """;

        // ACT & ASSERT
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.token").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.message").exists());
    }
}
