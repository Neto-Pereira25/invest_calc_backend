package br.edu.ifpe.pdsc.investCalc.investCalc.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.CompoundInterestSimulatorRequest;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.CompoundInterestSimulatorResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.CompoundInterestSimulatorTableResponse;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.PeriodType;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.RateType;

@Service
public class CompoundInterestSimulatorService {

    public CompoundInterestSimulatorResponse simulate(CompoundInterestSimulatorRequest request) {

        BigDecimal initialValue = request.getInitialValue();
        BigDecimal monthlyContribution = request.getMonthlyContribution();
        BigDecimal rate = convertRate(request);

        int months = convertToMonths(request);

        BigDecimal accumulated = initialValue;
        BigDecimal totalInvested = initialValue;
        BigDecimal totalInterest = BigDecimal.ZERO;
        List<CompoundInterestSimulatorTableResponse> monthlyBreakdown = new ArrayList<>();

        monthlyBreakdown.add(toTableRow(0, totalInvested, BigDecimal.ZERO, totalInterest, accumulated));

        for (int i = 1; i <= months; i++) {

            BigDecimal interest = accumulated.multiply(rate);
            accumulated = accumulated.add(interest).add(monthlyContribution);

            totalInterest = totalInterest.add(interest);
            totalInvested = totalInvested.add(monthlyContribution);

            monthlyBreakdown.add(toTableRow(i, totalInvested, interest, totalInterest, accumulated));
        }

        return new CompoundInterestSimulatorResponse(
                totalInvested.setScale(2, RoundingMode.HALF_UP),
                totalInterest.setScale(2, RoundingMode.HALF_UP),
                accumulated.setScale(2, RoundingMode.HALF_UP),
                monthlyBreakdown);
    }

    private CompoundInterestSimulatorTableResponse toTableRow(int month, BigDecimal invested, BigDecimal interest,
            BigDecimal totalInterest, BigDecimal accumulated) {

        return new CompoundInterestSimulatorTableResponse(
                month,
                invested.setScale(2, RoundingMode.HALF_UP),
                interest.setScale(2, RoundingMode.HALF_UP),
                totalInterest.setScale(2, RoundingMode.HALF_UP),
                accumulated.setScale(2, RoundingMode.HALF_UP));
    }

    private BigDecimal convertRate(CompoundInterestSimulatorRequest request) {

        BigDecimal rate = request.getInterestRate()
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        if (RateType.YEARLY.equals(request.getRateType())) {
            return BigDecimal.valueOf(
                    Math.pow(1 + rate.doubleValue(), 1.0 / 12) - 1);
        }

        return rate;
    }

    private int convertToMonths(CompoundInterestSimulatorRequest request) {

        if (PeriodType.ANNUAL.equals(request.getPeriodType())) {
            return request.getPeriod() * 12;
        }

        return request.getPeriod();
    }
}
