package br.edu.ifpe.pdsc.investCalc.investCalc.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(

        @NotBlank(message = "Token e obrigatorio") String token,

        @Size(min = 8, message = "Senha deve ter no minimo 8 caracteres") String password) {
}