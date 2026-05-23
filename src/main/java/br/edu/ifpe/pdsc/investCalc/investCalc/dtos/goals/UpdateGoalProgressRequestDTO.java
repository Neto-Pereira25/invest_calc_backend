package br.edu.ifpe.pdsc.investCalc.investCalc.dtos.goals;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class UpdateGoalProgressRequestDTO {

    @NotNull(message = "Current amount is required.")
    @DecimalMin(value = "0.00", message = "Current amount cannot be negative.")
    private BigDecimal currentAmount;

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }
}
