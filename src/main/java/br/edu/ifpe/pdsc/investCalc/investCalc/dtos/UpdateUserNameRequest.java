package br.edu.ifpe.pdsc.investCalc.investCalc.dtos;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserNameRequest(
        @NotBlank(message = "Nome é obrigatório") String name) {
}
