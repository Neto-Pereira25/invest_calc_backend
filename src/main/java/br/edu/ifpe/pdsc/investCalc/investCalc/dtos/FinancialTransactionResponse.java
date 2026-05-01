package br.edu.ifpe.pdsc.investCalc.investCalc.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.edu.ifpe.pdsc.investCalc.investCalc.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FinancialTransactionResponse {
    private Long id;
    private String description;
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private String subcategory;
    private LocalDate date;
}
