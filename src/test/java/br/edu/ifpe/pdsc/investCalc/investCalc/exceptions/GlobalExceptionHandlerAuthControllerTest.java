package br.edu.ifpe.pdsc.investCalc.investCalc.exceptions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.edu.ifpe.pdsc.investCalc.investCalc.controllers.AuthController;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.CustomUserDetailsService;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtAuthenticationFilter;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtService;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.RefreshTokenService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.AuthService;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
public class GlobalExceptionHandlerAuthControllerTest {

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
    @DisplayName("Should return 400 and ApiResponse when email already exists on register")
    void shouldReturn400WhenEmailAlreadyExists() throws Exception {

        // ARRANGE
        doThrow(new EmailAlreadyExistsException()).when(authService).register(any());

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
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("Email ja cadastrado"));
    }

    @Test
    @DisplayName("Should return 404 and ApiResponse when user not found on login")
    void shouldReturn404WhenUserNotFoundOnLogin() throws Exception {

        // ARRANGE
        when(authService.login(any())).thenThrow(new UserNotFoundException());

        String requestBody = """
                {
                    "email": "missing@email.com",
                    "password": "12345678"
                }
                """;

        // ACT & ASSERT
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("Usuario nao encontrado"));
    }

    @Test
    @DisplayName("Should return 400 and ApiResponse when password is invalid on login")
    void shouldReturn400WhenPasswordIsInvalid() throws Exception {

        // ARRANGE
        when(authService.login(any())).thenThrow(new InvalidPasswordException());

        String requestBody = """
                {
                    "email": "missing@email.com",
                    "password": "wrongPassword"
                }
                """;

        // ACT & ASSERT
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.message").value("Senha invalida"));
    }

    @Test
    @DisplayName("Should return 422 and validation errors when register request is invalid")
    void shouldReturn422WhenRegisterRequestIsInvalid() throws Exception {

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
}
