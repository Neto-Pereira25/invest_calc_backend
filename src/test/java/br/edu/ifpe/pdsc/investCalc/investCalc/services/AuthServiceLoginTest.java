package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.AuthResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.LoginRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.RefreshToken;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.UserRepository;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtService;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.RefreshTokenService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceLoginTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private JwtService jwtService;

        @Mock
        private RefreshTokenService refreshTokenService;

        @InjectMocks
        private AuthService authService;

        @Test
        void shouldLoginSuccessfully() {

                // ARRANGE (preparar cenário)

                String email = "teste@email.com";
                String password = "12345678";

                User user = new User();
                user.setEmail(email);
                user.setPassword("senhaCriptografada");

                when(userRepository.findByEmail(email))
                                .thenReturn(Optional.of(user));

                when(passwordEncoder.matches(password, user.getPassword()))
                                .thenReturn(true);

                when(jwtService.generateToken(email))
                                .thenReturn("token-fake");

                RefreshToken refreshToken = new RefreshToken();
                refreshToken.setToken("refresh-token-fake");
                when(refreshTokenService.createRefreshToken(email))
                                .thenReturn(refreshToken);

                // ACT (executar)

                AuthResponse response = authService.login(
                                new LoginRequest(email, password));

                // ASSERT (verificar resultado)

                assertNotNull(response);
                assertEquals("token-fake", response.token());
                assertEquals("refresh-token-fake", response.refreshToken());
        }

        // Fazer os cenários de falha do login
}
