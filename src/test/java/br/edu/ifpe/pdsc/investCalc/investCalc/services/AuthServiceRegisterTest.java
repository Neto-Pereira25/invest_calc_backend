package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.RegisterRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.Role;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.EmailAlreadyExistsException;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.UserRepository;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtService;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.RefreshTokenService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceRegisterTest {

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
    void shouldRegisterWithNotExistingEmail() {
        // ARRANGE
        RegisterRequest request = new RegisterRequest("Maria", "maria@email.com", "12345678");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("SENHA_CRIPTOGRAFADA");

        // ACT
        authService.register(request);

        // ASSERT
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();

        assertEquals("Maria", savedUser.getName());
        assertEquals("maria@email.com", savedUser.getEmail());
        assertEquals("SENHA_CRIPTOGRAFADA", savedUser.getPassword());
        assertEquals(Role.ROLE_USER, savedUser.getRole());

        verify(passwordEncoder).encode("12345678");
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // ARRANGE
        RegisterRequest request = new RegisterRequest("João", "joao@email.com", "12345678");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(new User()));

        // ACT & ASSERT

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(request));

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }
}
