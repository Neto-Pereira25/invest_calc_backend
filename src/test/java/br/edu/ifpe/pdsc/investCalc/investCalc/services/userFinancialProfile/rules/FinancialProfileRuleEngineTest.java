package br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileAnswerRequestDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.userFinancialProfile.FinancialProfile;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.userFinancialProfile.FinancialProfileOption;

class FinancialProfileRuleEngineTest {

    private final FinancialProfileRuleEngine ruleEngine = new FinancialProfileRuleEngine();

    @Test
    @DisplayName("Should calculate score based on mapped rules")
    void shouldCalculateScoreBasedOnMappedRules() {
        List<FinancialProfileAnswerRequestDTO> answers = List.of(
                answer(1, FinancialProfileOption.A),
                answer(4, FinancialProfileOption.E),
                answer(7, FinancialProfileOption.A));

        FinancialProfileScore score = ruleEngine.calculate(answers);

        assertEquals(2, score.get(FinancialProfile.DEVEDOR));
        assertEquals(2, score.get(FinancialProfile.GASTADOR));
        assertEquals(4, score.get(FinancialProfile.DESLIGADO));
        assertEquals(0, score.get(FinancialProfile.POUPADOR));
        assertEquals(4, score.get(FinancialProfile.INVESTIDOR));
    }

    @Test
    @DisplayName("Should throw exception when question is not mapped")
    void shouldThrowExceptionWhenQuestionIsNotMapped() {
        List<FinancialProfileAnswerRequestDTO> answers = List.of(answer(99, FinancialProfileOption.A));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ruleEngine.calculate(answers));

        assertEquals("Question not mapped: 99", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when option is not mapped")
    void shouldThrowExceptionWhenOptionIsNotMapped() {
        List<FinancialProfileAnswerRequestDTO> answers = List.of(answer(1, null));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ruleEngine.calculate(answers));

        assertEquals("Option not mapped: null", exception.getMessage());
    }

    private FinancialProfileAnswerRequestDTO answer(Integer questionNumber, FinancialProfileOption option) {
        FinancialProfileAnswerRequestDTO answer = new FinancialProfileAnswerRequestDTO();
        answer.setQuestionNumber(questionNumber);
        answer.setSelectedOption(option);
        return answer;
    }
}
