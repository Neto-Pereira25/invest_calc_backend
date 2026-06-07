package br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile;

import br.edu.ifpe.pdsc.investCalc.investCalc.enums.userFinancialProfile.FinancialProfileOption;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinancialProfileAnswerRequestDTO {

    @NotNull
    @Min(1)
    @Max(10)
    private Integer questionNumber;

    @NotNull
    private FinancialProfileOption selectedOption;

}
