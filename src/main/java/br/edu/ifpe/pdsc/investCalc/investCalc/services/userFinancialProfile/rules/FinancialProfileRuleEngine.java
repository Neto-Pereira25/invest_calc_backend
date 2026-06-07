package br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile.rules;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileAnswerRequestDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.userFinancialProfile.FinancialProfile;

@Component
public class FinancialProfileRuleEngine {

    public FinancialProfileScore calculate(
            List<FinancialProfileAnswerRequestDTO> answers) {

        FinancialProfileScore score = new FinancialProfileScore();

        for (FinancialProfileAnswerRequestDTO answer : answers) {

            var questionRules = FinancialProfileRules.RULES
                    .get(answer.getQuestionNumber());

            if (questionRules == null) {
                throw new IllegalArgumentException(
                        "Question not mapped: "
                                + answer.getQuestionNumber());
            }

            Map<FinancialProfile, Integer> profilePoints = questionRules.get(answer.getSelectedOption());

            if (profilePoints == null) {
                throw new IllegalArgumentException(
                        "Option not mapped: "
                                + answer.getSelectedOption());
            }

            profilePoints.forEach(score::add);
        }

        return score;
    }
}
