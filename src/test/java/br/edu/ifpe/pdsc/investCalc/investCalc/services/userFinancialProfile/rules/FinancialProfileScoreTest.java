package br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.edu.ifpe.pdsc.investCalc.investCalc.enums.userFinancialProfile.FinancialProfile;

class FinancialProfileScoreTest {

    @Test
    @DisplayName("Should return INVESTIDOR as default profile on tie")
    void shouldReturnInvestidorAsDefaultProfileOnTie() {
        FinancialProfileScore score = new FinancialProfileScore();

        FinancialProfile mainProfile = score.getMainProfile();

        assertEquals(FinancialProfile.INVESTIDOR, mainProfile);
    }

    @Test
    @DisplayName("Should return profile with highest score")
    void shouldReturnProfileWithHighestScore() {
        FinancialProfileScore score = new FinancialProfileScore();
        score.add(FinancialProfile.POUPADOR, 8);
        score.add(FinancialProfile.INVESTIDOR, 7);

        FinancialProfile mainProfile = score.getMainProfile();

        assertEquals(FinancialProfile.POUPADOR, mainProfile);
    }

    @Test
    @DisplayName("Should calculate rounded percentage")
    void shouldCalculateRoundedPercentage() {
        FinancialProfileScore score = new FinancialProfileScore();
        score.add(FinancialProfile.INVESTIDOR, 10);

        Double percentage = score.getPercentage(FinancialProfile.INVESTIDOR);

        assertEquals(43.48, percentage);
    }
}
