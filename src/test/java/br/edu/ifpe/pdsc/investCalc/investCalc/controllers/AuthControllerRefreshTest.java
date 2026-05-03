package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import br.edu.ifpe.pdsc.investCalc.investCalc.entities.RefreshToken;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.CustomUserDetailsService;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtAuthenticationFilter;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtService;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.RefreshTokenService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.AuthService;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerRefreshTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AuthService authService;

        @MockBean
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @MockBean
        private CustomUserDetailsService customUserDetailsService;

        @MockBean
        private RefreshTokenService refreshTokenService;

        @MockBean
        private JwtService jwtService;

        @Test
        @DisplayName("POST /auth/refresh - should return new access token and same refresh token when refresh token is valid")
        void shouldReturnNewAccessTokenWhenRefreshTokenIsValid() throws Exception {

                // ARRANGE
                String refreshTokenValue = "REFRESH_TOKEN";
                String name = "User Name";
                String email = "user@email.com";

                User user = new User();
                user.setName(name);
                user.setEmail(email);

                RefreshToken refreshToken = new RefreshToken();
                refreshToken.setToken(refreshTokenValue);
                refreshToken.setUser(user);
                refreshToken.setExpiration(new java.util.Date(System.currentTimeMillis() + 60 * 60 * 1000)); // Expira
                                                                                                             // em 1
                                                                                                             // hora

                when(refreshTokenService.validate(refreshTokenValue))
                                .thenReturn(refreshToken);
                when(jwtService.generateToken(name, email))
                                .thenReturn("NEW_ACCESS_TOKEN");

                String requestBody = """
                                {
                                    "refreshToken": "REFRESH_TOKEN"
                                }
                                """;

                // ACT & ASSERT
                mockMvc.perform(post("/api/v1/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.token").value("NEW_ACCESS_TOKEN"))
                                .andExpect(jsonPath("$.data.refreshToken").value(refreshTokenValue))
                                .andExpect(jsonPath("$.message").exists());
        }
}
