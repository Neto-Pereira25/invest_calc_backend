package br.edu.ifpe.pdsc.investCalc.investCalc.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.ApiResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.AuthResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.LoginRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.RegisterRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
}
