package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ForgotPasswordRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ResetPasswordRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.PasswordResetToken;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.InvalidPasswordResetTokenException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.PasswordResetTokenAlreadyUsedException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.PasswordResetTokenExpiredException;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.PasswordResetTokenRepository;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.UserRepository;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtService;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.RefreshTokenService;

@ExtendWith(MockitoExtension.class)
public class AuthServicePasswordRecoveryTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldCreateResetTokenAndInvalidateOldOnForgotPassword() {

        // ARRANGE
        User user = new User();
        user.setEmail("maria@email.com");

        when(userRepository.findByEmail("maria@email.com")).thenReturn(Optional.of(user));
        when(passwordResetTokenRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        authService.forgotPassword(new ForgotPasswordRequest("maria@email.com"));

        // ASSERT
        verify(passwordResetTokenRepository).deleteByUser(user);

        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository).save(captor.capture());

        PasswordResetToken token = captor.getValue();
        assertEquals(user, token.getUser());
        assertFalse(token.isUsed());
        assertFalse(token.getToken().isBlank());
    }

    @Test
    void shouldDoNothingOnForgotPasswordWhenEmailDoesNotExist() {

        // ARRANGE
        when(userRepository.findByEmail("missing@email.com")).thenReturn(Optional.empty());

        // ACT
        authService.forgotPassword(new ForgotPasswordRequest("missing@email.com"));

        // ASSERT
        verify(passwordResetTokenRepository, never()).deleteByUser(any());
        verify(passwordResetTokenRepository, never()).save(any());
    }

    @Test
    void shouldResetPasswordWhenTokenIsValid() {

        // ARRANGE
        User user = new User();
        user.setPassword("old-password");

        PasswordResetToken token = new PasswordResetToken();
        token.setToken("token-value");
        token.setUser(user);
        token.setUsed(false);
        token.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15));

        when(passwordResetTokenRepository.findByToken("token-value")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("12345678")).thenReturn("encoded-password");

        // ACT
        authService.resetPassword(new ResetPasswordRequest("token-value", "12345678"));

        // ASSERT
        assertEquals("encoded-password", user.getPassword());
        assertEquals(true, token.isUsed());
        verify(userRepository).save(user);
        verify(passwordResetTokenRepository).save(token);
    }

    @Test
    void shouldThrowWhenResetTokenDoesNotExist() {

        // ARRANGE
        when(passwordResetTokenRepository.findByToken("invalido")).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(InvalidPasswordResetTokenException.class,
                () -> authService.resetPassword(new ResetPasswordRequest("invalido", "12345678")));
    }

    @Test
    void shouldThrowWhenResetTokenIsExpired() {

        // ARRANGE
        PasswordResetToken token = new PasswordResetToken();
        token.setUsed(false);
        token.setExpiration(new Date(System.currentTimeMillis() - 1000));

        when(passwordResetTokenRepository.findByToken("token-expired")).thenReturn(Optional.of(token));

        // ACT & ASSERT
        assertThrows(PasswordResetTokenExpiredException.class,
                () -> authService.resetPassword(new ResetPasswordRequest("token-expired", "12345678")));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenResetTokenIsAlreadyUsed() {

        // ARRANGE
        PasswordResetToken token = new PasswordResetToken();
        token.setUsed(true);
        token.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60));

        when(passwordResetTokenRepository.findByToken("token-used")).thenReturn(Optional.of(token));

        // ACT & ASSERT
        assertThrows(PasswordResetTokenAlreadyUsedException.class,
                () -> authService.resetPassword(new ResetPasswordRequest("token-used", "12345678")));
        verify(userRepository, never()).save(any());
    }
}