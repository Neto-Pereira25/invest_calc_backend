package br.edu.ifpe.pdsc.investCalc.investCalc.dtos;

import java.math.BigDecimal;

import br.edu.ifpe.pdsc.investCalc.investCalc.enums.PeriodType;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.RateInputType;
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
public class RetirementSimulatorRequest {

    @NotNull(message = "Renda mensal desejada é obrigatória")
    @DecimalMin(value = "0.0", inclusive = false, message = "Renda mensal desejada deve ser maior que zero")
    private BigDecimal desiredMonthlyIncome;

    @NotNull(message = "Taxa de juros é obrigatória")
    @DecimalMin(value = "0.0", inclusive = false, message = "Taxa de juros deve ser maior que zero")
    private BigDecimal interestRate;

    @NotNull(message = "Prazo é obrigatório")
    @Min(value = 1, message = "Prazo deve ser no mínimo 1")
    private Integer period;

    @NotNull(message = "Tipo de período é obrigatório")
    private PeriodType periodType;

    @NotNull(message = "Tipo de taxa é obrigatório")
    private RateType rateType;

    private RateInputType interestRateInputType;

    @DecimalMin(value = "0.0", inclusive = true, message = "Inflação anual deve ser >= 0")
    private BigDecimal annualInflationRate;

    @DecimalMin(value = "0.0", inclusive = false, message = "Taxa de retirada segura deve ser maior que zero")
    private BigDecimal safeWithdrawalRate;
}
