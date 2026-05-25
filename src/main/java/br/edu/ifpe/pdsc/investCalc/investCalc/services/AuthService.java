package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.AuthResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ForgotPasswordRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.LoginRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.RegisterRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ResetPasswordRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.PasswordResetToken;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.RefreshToken;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.User;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.Role;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.EmailAlreadyExistsException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.InvalidPasswordException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.InvalidPasswordResetTokenException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.PasswordResetTokenAlreadyUsedException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.PasswordResetTokenExpiredException;
import br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.UserNotFoundException;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.PasswordResetTokenRepository;
import br.edu.ifpe.pdsc.investCalc.investCalc.repositories.UserRepository;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtService;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.RefreshTokenService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        String encryptedPassword = passwordEncoder.encode(request.password());

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(encryptedPassword);
        user.setRole(Role.ROLE_USER);

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        String token = jwtService.generateToken(user.getName(), user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return new AuthResponse(token, refreshToken.getToken());
    }

    public void forgotPassword(ForgotPasswordRequest request) {

        Optional<User> userOptional = userRepository.findByEmail(request.email());

        if (userOptional.isEmpty()) {
            return;
        }

        User user = userOptional.get();

        passwordResetTokenRepository.deleteByUser(user);

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUser(user);
        passwordResetToken.setToken(UUID.randomUUID().toString());
        passwordResetToken.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15));
        passwordResetToken.setUsed(false);

        PasswordResetToken savedToken = passwordResetTokenRepository.save(passwordResetToken);

        System.out.println("Token de recuperacao para " + user.getEmail() + ": " + savedToken.getToken());
    }

    public void resetPassword(ResetPasswordRequest request) {

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(request.token())
                .orElseThrow(InvalidPasswordResetTokenException::new);

        if (passwordResetToken.getExpiration().before(new Date())) {
            throw new PasswordResetTokenExpiredException();
        }

        if (passwordResetToken.isUsed()) {
            throw new PasswordResetTokenAlreadyUsedException();
        }

        User user = passwordResetToken.getUser();

        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);
    }
}
