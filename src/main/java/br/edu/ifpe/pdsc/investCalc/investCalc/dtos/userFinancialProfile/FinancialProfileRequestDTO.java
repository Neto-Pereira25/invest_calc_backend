package br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinancialProfileRequestDTO {

    @Valid
    @NotEmpty
    private List<FinancialProfileAnswerRequestDTO> answers;

}
