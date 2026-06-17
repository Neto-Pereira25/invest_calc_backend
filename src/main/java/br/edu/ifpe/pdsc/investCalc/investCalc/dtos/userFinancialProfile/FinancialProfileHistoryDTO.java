package br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile;

import java.time.LocalDateTime;

import br.edu.ifpe.pdsc.investCalc.investCalc.enums.userFinancialProfile.FinancialProfile;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FinancialProfileHistoryDTO {

    private Long id;

    private FinancialProfile profile;

    private LocalDateTime assessedAt;

}
