package br.edu.ifpe.pdsc.investCalc.investCalc.dtos.goals;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateGoalRequestDTO {

    @NotBlank(message = "Goal name is required.")
    @Size(max = 100, message = "Goal name must have at most 100 characters.")
    private String name;

    @NotNull(message = "Target amount is required.")
    @DecimalMin(value = "0.01", message = "Target amount must be greater than zero.")
    private BigDecimal targetAmount;

    @NotNull(message = "Deadline is required.")
    @Future(message = "Deadline must be a future date.")
    private LocalDate deadline;
}
