package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.AuthResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.GlobalExceptionHandler;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.InvalidPasswordException;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.CustomUserDetailsService;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtAuthenticationFilter;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtService;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.RefreshTokenService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.AuthService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(GlobalExceptionHandler.class)
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
                mockMvc.perform(post("/api/v1/auth/register")
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
                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isUnprocessableEntity())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.message").value("Erro de validacao"))
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
                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.data.token").value("access-token"))
                                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
                                .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("POST /auth/login - should return 400 with standard response when credentials are invalid")
        void shouldReturnBadRequestWhenLoginFails() throws Exception {

                // ARRANGE
                when(authService.login(any()))
                                .thenThrow(new InvalidPasswordException());

                String requestBody = """
                                {
                                    "email": "maria@email.com",
                                    "password": "senhaErrada"
                                }
                                """;

                // ACT & ASSERT
                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.data").isEmpty())
                                .andExpect(jsonPath("$.message").value("Senha invalida"));
        }

        @Test
        @DisplayName("Should always return {data, message} on success.")
        void shouldReturnStandardResponseOnSuccess() throws Exception {

                // ARRANGE
                when(authService.login(any()))
                                .thenReturn(new AuthResponse("token", "refresh"));

                String requestBody = """
                                {
                                    "email": "teste@email.com",
                                    "password": "12345678"
                                }
                                """;

                // ACT & ASSERT
                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data").exists())
                                .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("Should always return {data, message} on a business error.")
        void shouldReturnStandardResponseOnBusinessError() throws Exception {

                // ARRANGE
                when(authService.login(any()))
                                .thenThrow(new InvalidPasswordException());

                String requestBody = """
                                {
                                    "email": "teste@email.com",
                                    "password": "errada"
                                }
                                """;

                // ACT & ASSERT
                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                                .andExpect(jsonPath("$.message").value("Senha invalida"));
        }

        @Test
        @DisplayName("Should return a list of errors in the data field when validation fails")
        void shouldReturnValidationErrorsInDataField() throws Exception {

                // ARRANGE
                String requestBody = """
                                {
                                    "email": "email-invalido",
                                    "password": ""
                                }
                                """;

                // ACT & ASSERT
                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isUnprocessableEntity())
                                .andExpect(jsonPath("$.data").isArray())
                                .andExpect(jsonPath("$.message").value("Erro de validacao"));
        }
}
