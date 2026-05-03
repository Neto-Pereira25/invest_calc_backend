package br.edu.ifpe.pdsc.investCalc.investCalc.dtos;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompoundInterestSimulatorResponse {

    private BigDecimal totalInvested;
    private BigDecimal totalInterest;
    private BigDecimal finalAmount;
    private List<CompoundInterestSimulatorTableResponse> monthlyBreakdown;

    public CompoundInterestSimulatorResponse(BigDecimal totalInvested, BigDecimal totalInterest,
            BigDecimal finalAmount) {
        this.totalInvested = totalInvested;
        this.totalInterest = totalInterest;
        this.finalAmount = finalAmount;
    }

    public CompoundInterestSimulatorResponse(BigDecimal totalInvested, BigDecimal totalInterest, BigDecimal finalAmount,
            List<CompoundInterestSimulatorTableResponse> monthlyBreakdown) {
        this.totalInvested = totalInvested;
        this.totalInterest = totalInterest;
        this.finalAmount = finalAmount;
        this.monthlyBreakdown = monthlyBreakdown;
    }
}
