package br.edu.ifpe.pdsc.investCalc.investCalc.dtos;

import java.math.BigDecimal;

import br.edu.ifpe.pdsc.investCalc.investCalc.enums.PeriodType;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.ReverseSimulationMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReverseSimulationResponse {

    private ReverseSimulationMode mode;
    private BigDecimal targetAmount;
    private BigDecimal usedMonthlyRatePercent;

    private BigDecimal informedMonthlyContribution;
    private Integer informedPeriod;
    private PeriodType informedPeriodType;

    private BigDecimal requiredMonthlyContribution;
    private Integer requiredPeriodMonths;
    private BigDecimal requiredPeriodYears;
}
