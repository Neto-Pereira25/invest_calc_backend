package br.edu.ifpe.pdsc.investCalc.investCalc.dtos;

import java.math.BigDecimal;

import br.edu.ifpe.pdsc.investCalc.investCalc.enums.PeriodType;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.RateType;
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
public class CompoundInterestSimulatorRequest {

    @NotNull(message = "Valor inicial é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "Valor inicial deve ser >= 0")
    private BigDecimal initialValue; // P

    @NotNull(message = "Aporte mensal é obrigatório")
    @DecimalMin(value = "0.0", inclusive = true, message = "Aporte deve ser >= 0")
    private BigDecimal monthlyContribution; // PMT

    @NotNull(message = "Taxa de juros é obrigatória")
    @DecimalMin(value = "0.0", inclusive = false, message = "Taxa deve ser maior que zero")
    private BigDecimal interestRate; // % (ex: 14.5)

    @NotNull(message = "Período é obrigatório")
    @Min(value = 1, message = "Período deve ser no mínimo 1")
    private Integer period; // quantidade

    @NotNull(message = "Tipo de período é obrigatório")
    private PeriodType periodType; // MONTHLY | YEARLY

    @NotNull(message = "Tipo de taxa é obrigatório")
    private RateType rateType; // MONTHLY | YEARLY
}
