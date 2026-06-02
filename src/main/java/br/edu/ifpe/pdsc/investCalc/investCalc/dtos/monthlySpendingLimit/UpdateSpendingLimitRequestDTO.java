package br.edu.ifpe.pdsc.investCalc.investCalc.dtos.monthlySpendingLimit;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record UpdateSpendingLimitRequestDTO(

        @NotNull(message = "O limite é obrigatório") @DecimalMin(value = "0.01", message = "O limite deve ser maior que zero") BigDecimal amount) {
}
