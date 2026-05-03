package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.InvalidPasswordException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.UserNotFoundException;
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
                user.setName("Teste User");
                user.setEmail(email);
                user.setPassword("senhaCriptografada");

                when(userRepository.findByEmail(email))
                                .thenReturn(Optional.of(user));

                when(passwordEncoder.matches(password, user.getPassword()))
                                .thenReturn(true);

                when(jwtService.generateToken(user.getName(), email))
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

        @Test
        void shouldThrowExceptionWhenPasswordIsInvalid() {

                // ARRANGE
                String email = "teste@email.com";
                String rawPassword = "123456";
                String encodedPassword = "senhaCriptografada";

                User user = new User();
                user.setEmail(email);
                user.setPassword(encodedPassword);

                when(userRepository.findByEmail(email))
                                .thenReturn(Optional.of(user));

                when(passwordEncoder.matches(rawPassword, encodedPassword))
                                .thenReturn(false);

                // ACT & ASSERT
                InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> {
                        authService.login(new LoginRequest(email, rawPassword));
                });

                assertEquals("Senha invalida", exception.getMessage());
        }

        @Test
        void shouldThrowExceptionWhenUserNotFound() {

                // ARRANGE
                String email = "naoexiste@email.com";
                String password = "123456";

                // simula usuário NÃO encontrado
                when(userRepository.findByEmail(email))
                                .thenReturn(Optional.empty());

                // ACT & ASSERT
                UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
                        authService.login(new LoginRequest(email, password));
                });

                assertEquals("Usuario nao encontrado", exception.getMessage());
        }
}
