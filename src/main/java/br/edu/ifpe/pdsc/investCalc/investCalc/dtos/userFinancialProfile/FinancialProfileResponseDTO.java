package br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile;

import java.time.LocalDateTime;
import java.util.List;

import br.edu.ifpe.pdsc.investCalc.investCalc.enums.userFinancialProfile.FinancialProfile;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FinancialProfileResponseDTO {

    private FinancialProfile profile;

    private String description;

    private List<String> strengths;

    private List<String> limitations;

    private List<String> recommendations;

    private List<String> suggestedGoals;

    private Integer devedorScore;
    private Integer gastadorScore;
    private Integer desligadoScore;
    private Integer poupadorScore;
    private Integer investidorScore;

    private Double devedorPercentage;
    private Double gastadorPercentage;
    private Double desligadoPercentage;
    private Double poupadorPercentage;
    private Double investidorPercentage;

    private LocalDateTime assessedAt;

}
