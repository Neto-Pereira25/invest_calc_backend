package br.edu.ifpe.pdsc.investCalc.investCalc.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompoundInterestSimulatorTableResponse {

    private Integer month;
    private BigDecimal invested;
    private BigDecimal interest;
    private BigDecimal accumulated;
}
