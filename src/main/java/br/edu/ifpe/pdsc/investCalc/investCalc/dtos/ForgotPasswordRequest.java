package br.edu.ifpe.pdsc.investCalc.investCalc.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(

        @Email(message = "Email invalido") @NotBlank(message = "Email e obrigatorio") String email) {
}