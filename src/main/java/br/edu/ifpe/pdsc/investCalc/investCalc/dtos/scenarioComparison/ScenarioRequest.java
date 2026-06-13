package br.edu.ifpe.pdsc.investCalc.investCalc.dtos.scenarioComparison;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScenarioRequest(
        @NotBlank(message = "Nome do cenário é obrigatório") String name,

        @NotNull(message = "Capital inicial é obrigatório") @DecimalMin(value = "0.0", inclusive = true, message = "Capital inicial deve ser >= 0") BigDecimal initialCapital,

        @NotNull(message = "Aporte mensal é obrigatório") @DecimalMin(value = "0.0", inclusive = true, message = "Aporte mensal deve ser >= 0") BigDecimal monthlyContribution,

        @NotNull(message = "Taxa de juros é obrigatória") @DecimalMin(value = "0.0", inclusive = false, message = "Taxa de juros deve ser maior que zero") BigDecimal interestRate,

        @NotNull(message = "Quantidade de meses é obrigatória") @Min(value = 1, message = "Quantidade de meses deve ser no mínimo 1") Integer months) {
}
