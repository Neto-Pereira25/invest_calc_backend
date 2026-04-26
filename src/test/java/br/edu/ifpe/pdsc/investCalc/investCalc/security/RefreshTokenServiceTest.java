package br.edu.ifpe.pdsc.investCalc.investCalc.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ifpe.pdsc.investCalc.investCalc.entities.RefreshToken;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.RefreshTokenRepository;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("Validate should return RefreshToken when token exists and is not expired")
    void shouldReturnRefreshTokenWhenTokenIsValid() {

        // Arrange
        String tokenValue = "VALID_TOKEN";

        RefreshToken token = new RefreshToken();
        token.setToken(tokenValue);
        token.setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000)); // Expira em 1 hora

        when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));

        // Act
        RefreshToken result = refreshTokenService.validate(tokenValue);

        // Assert
        assertNotNull(result);
        assertEquals(tokenValue, result.getToken());
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Validate should delete token and throw exception when token is expired")
    void shouldDeleteAndThrowWhenTokenIsExpired() {

        // Arrange
        String tokenValue = "EXPIRED_TOKEN";

        RefreshToken token = new RefreshToken();
        token.setToken(tokenValue);
        token.setExpiration(new Date(System.currentTimeMillis() - 60 * 60 * 1000)); // Expirado há 1 hora

        when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> refreshTokenService.validate(tokenValue));
        assertTrue(ex.getMessage().toLowerCase().contains("expir"));

        verify(refreshTokenRepository).delete(token);
    }

    @Test
    @DisplayName("Validate should throw exception when token does not exist")
    void shouldThrowWhenTokenDoesNotExist() {

        // Arrange
        String tokenValue = "MISSING_TOKEN";

        when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> refreshTokenService.validate(tokenValue));
        assertTrue(ex.getMessage().toLowerCase().contains("inv"));

        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
    }

    @Test
    void shouldThrowExceptionAndDeleteTokenWhenRefreshTokenIsExpired() {

        // ARRANGE
        String tokenValue = "refresh-token-valido";

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(tokenValue);
        refreshToken.setExpiration(Date.from(Instant.now().minus(1, ChronoUnit.DAYS))); // EXPIRADO

        when(refreshTokenRepository.findByToken(tokenValue))
                .thenReturn(Optional.of(refreshToken));

        // ACT & ASSERT
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            refreshTokenService.validate(tokenValue);
        });

        assertEquals("Refresh token expirado", exception.getMessage());

        verify(refreshTokenRepository).delete(refreshToken);
    }
}
