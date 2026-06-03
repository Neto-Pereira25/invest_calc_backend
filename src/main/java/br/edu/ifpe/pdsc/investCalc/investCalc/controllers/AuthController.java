package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ApiResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.AuthResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ForgotPasswordRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.LoginRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.PasswordResetTokenResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.RefreshRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.RegisterRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ResetPasswordRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.RefreshToken;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.JwtService;
import br.edu.ifpe.pdsc.investCalc.investCalc.security.RefreshTokenService;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return new ApiResponse<>(null, "Usuário cadastrado com sucesso");
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        AuthResponse response = authService.login(request);
        return new ApiResponse<>(response, "Login realizado com sucesso");
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@RequestBody RefreshRequest request) {

        RefreshToken refreshToken = refreshTokenService.validate(request.refreshToken());

        String newAccessToken = jwtService.generateToken(
                refreshToken.getUser().getName(),
                refreshToken.getUser().getEmail());

        return new ApiResponse<>(new AuthResponse(newAccessToken, request.refreshToken()),
                "Token atualizado com sucesso");
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return new ApiResponse<>(null,
                "Se o email estiver cadastrado, voce recebera instrucoes para redefinir a senha");
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request);
        return new ApiResponse<>(null, "Senha redefinida com sucesso");
    }

    @GetMapping("/public")
    public String publicRoute() {
        return "ok";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public String me(@AuthenticationPrincipal UserDetails user) {
        return user.getUsername();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminRoute() {
        return "admin";
    }

    @Profile("e2e")
    @GetMapping("/e2e/password-reset-token")
    public ApiResponse<PasswordResetTokenResponse> getPasswordResetTokenForE2E(
            @RequestParam String email) {
        String token = authService.getPasswordResetTokenForE2E(email);
        return new ApiResponse<>(new PasswordResetTokenResponse(token), "Token recuperado com sucesso");
    }
}
