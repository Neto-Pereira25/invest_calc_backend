package br.edu.ifpe.pdsc.investCalc.investCalc.dtos;

import java.math.BigDecimal;

import br.edu.ifpe.pdsc.investCalc.investCalc.enums.PeriodType;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.RateType;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.ReverseSimulationMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReverseSimulationRequest {

    @NotNull(message = "Valor objetivo é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Valor objetivo deve ser maior que zero")
    private BigDecimal targetAmount;

    @NotNull(message = "Taxa de juros é obrigatória")
    @DecimalMin(value = "0.0", inclusive = false, message = "Taxa de juros deve ser maior que zero")
    private BigDecimal interestRate;

    @NotNull(message = "Tipo de taxa é obrigatório")
    private RateType rateType;

    @NotNull(message = "Modo da simulação reversa é obrigatório")
    private ReverseSimulationMode mode;

    @Min(value = 1, message = "Período deve ser no mínimo 1")
    private Integer period;

    private PeriodType periodType;

    @DecimalMin(value = "0.0", inclusive = false, message = "Aporte mensal deve ser maior que zero")
    private BigDecimal monthlyContribution;
}
