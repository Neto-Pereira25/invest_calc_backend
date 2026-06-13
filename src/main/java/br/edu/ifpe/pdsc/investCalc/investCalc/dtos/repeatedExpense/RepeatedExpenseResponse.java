package br.edu.ifpe.pdsc.investCalc.investCalc.dtos.repeatedExpense;

import java.math.BigDecimal;

public record RepeatedExpenseResponse(
        String description,

        String category,

        String subcategory,

        BigDecimal averageAmount,

        Integer frequency

) {
}
