package br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile.mapper;

import org.springframework.stereotype.Component;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileHistoryDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileResponseDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.userFinancialProfile.FinancialProfileResult;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.userFinancialProfile.FinancialProfile;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile.metadata.FinancialProfileDetails;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile.metadata.FinancialProfileMetadata;
import br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile.rules.FinancialProfileScore;

@Component
public class FinancialProfileMapper {

        public FinancialProfileResponseDTO toResponseDTO(
                        FinancialProfileResult result) {

                FinancialProfileScore score = FinancialProfileScore.fromResult(result);

                FinancialProfileDetails details = FinancialProfileMetadata.get(
                                result.getProfile());

                return FinancialProfileResponseDTO.builder()
                                .profile(result.getProfile())

                                .description(
                                                details.description())

                                .strengths(
                                                details.strengths())

                                .limitations(
                                                details.limitations())

                                .recommendations(
                                                details.recommendations())

                                .suggestedGoals(
                                                details.suggestedGoals())

                                .devedorScore(
                                                result.getDevedorScore())

                                .gastadorScore(
                                                result.getGastadorScore())

                                .desligadoScore(
                                                result.getDesligadoScore())

                                .poupadorScore(
                                                result.getPoupadorScore())

                                .investidorScore(
                                                result.getInvestidorScore())

                                .devedorPercentage(
                                                score.getPercentage(
                                                                FinancialProfile.DEVEDOR))

                                .gastadorPercentage(
                                                score.getPercentage(
                                                                FinancialProfile.GASTADOR))

                                .desligadoPercentage(
                                                score.getPercentage(
                                                                FinancialProfile.DESLIGADO))

                                .poupadorPercentage(
                                                score.getPercentage(
                                                                FinancialProfile.POUPADOR))

                                .investidorPercentage(
                                                score.getPercentage(
                                                                FinancialProfile.INVESTIDOR))

                                .assessedAt(
                                                result.getAssessedAt())

                                .build();
        }

        public FinancialProfileHistoryDTO toHistoryDTO(
                        FinancialProfileResult result) {

                return FinancialProfileHistoryDTO.builder()
                                .id(result.getId())
                                .profile(result.getProfile())
                                .assessedAt(result.getAssessedAt())
                                .build();
        }
}
