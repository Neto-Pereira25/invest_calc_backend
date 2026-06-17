package br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileHistoryDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.dtos.userFinancialProfile.FinancialProfileResponseDTO;
import br.edu.ifpe.pdsc.investCalc.investCalc.entities.userFinancialProfile.FinancialProfileResult;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.userFinancialProfile.FinancialProfile;

class FinancialProfileMapperTest {

    private final FinancialProfileMapper mapper = new FinancialProfileMapper();

    @Test
    @DisplayName("Should map result to response dto with percentages")
    void shouldMapResultToResponseDtoWithPercentages() {
        LocalDateTime assessedAt = LocalDateTime.of(2026, 1, 10, 12, 0);
        FinancialProfileResult result = FinancialProfileResult.builder()
                .id(1L)
                .profile(FinancialProfile.POUPADOR)
                .devedorScore(5)
                .gastadorScore(4)
                .desligadoScore(3)
                .poupadorScore(12)
                .investidorScore(10)
                .assessedAt(assessedAt)
                .build();

        FinancialProfileResponseDTO response = mapper.toResponseDTO(result);

        assertNotNull(response);
        assertEquals(FinancialProfile.POUPADOR, response.getProfile());
        assertNotNull(response.getDescription());
        assertFalse(response.getDescription().isBlank());
        assertNotNull(response.getStrengths());
        assertFalse(response.getStrengths().isEmpty());
        assertNotNull(response.getLimitations());
        assertFalse(response.getLimitations().isEmpty());
        assertNotNull(response.getRecommendations());
        assertFalse(response.getRecommendations().isEmpty());
        assertNotNull(response.getSuggestedGoals());
        assertFalse(response.getSuggestedGoals().isEmpty());
        assertEquals(5, response.getDevedorScore());
        assertEquals(29.41, response.getDevedorPercentage());
        assertEquals(26.67, response.getGastadorPercentage());
        assertEquals(17.65, response.getDesligadoPercentage());
        assertEquals(50.0, response.getPoupadorPercentage());
        assertEquals(43.48, response.getInvestidorPercentage());
        assertEquals(assessedAt, response.getAssessedAt());
    }

    @Test
    @DisplayName("Should map result to history dto")
    void shouldMapResultToHistoryDto() {
        LocalDateTime assessedAt = LocalDateTime.of(2026, 2, 1, 8, 30);
        FinancialProfileResult result = FinancialProfileResult.builder()
                .id(99L)
                .profile(FinancialProfile.INVESTIDOR)
                .assessedAt(assessedAt)
                .build();

        FinancialProfileHistoryDTO history = mapper.toHistoryDTO(result);

        assertNotNull(history);
        assertEquals(99L, history.getId());
        assertEquals(FinancialProfile.INVESTIDOR, history.getProfile());
        assertEquals(assessedAt, history.getAssessedAt());
    }
}
