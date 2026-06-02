package br.edu.ifpe.pdsc.investCalc.investCalc.dtos.monthlySpendingLimit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SpendingLimitResponseDTO(
        Long id,
        BigDecimal amount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
