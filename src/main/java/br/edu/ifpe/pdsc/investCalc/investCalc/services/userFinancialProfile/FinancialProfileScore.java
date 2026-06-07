package br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import br.edu.ifpe.pdsc.investCalc.investCalc.enums.userFinancialProfile.FinancialProfile;

public class FinancialProfileScore {

    private static final List<FinancialProfile> PRIORITY = List.of(
            FinancialProfile.INVESTIDOR,
            FinancialProfile.POUPADOR,
            FinancialProfile.GASTADOR,
            FinancialProfile.DESLIGADO,
            FinancialProfile.DEVEDOR);

    private final Map<FinancialProfile, Integer> scores = new EnumMap<>(FinancialProfile.class);

    private static final Map<FinancialProfile, Integer> MAX_SCORES = Map.of(
            FinancialProfile.DEVEDOR, 17,
            FinancialProfile.GASTADOR, 15,
            FinancialProfile.DESLIGADO, 17,
            FinancialProfile.POUPADOR, 24,
            FinancialProfile.INVESTIDOR, 23);

    public FinancialProfileScore() {
        for (FinancialProfile profile : FinancialProfile.values()) {
            scores.put(profile, 0);
        }
    }

    public void add(FinancialProfile profile, int points) {
        scores.merge(profile, points, Integer::sum);
    }

    public Integer get(FinancialProfile profile) {
        return scores.get(profile);
    }

    public Map<FinancialProfile, Integer> getScores() {
        return scores;
    }

    public FinancialProfile getMainProfile() {

        return PRIORITY.stream()
                .max(Comparator.comparing(scores::get))
                .orElse(FinancialProfile.DESLIGADO);
    }

    public Double getPercentage(FinancialProfile profile) {

        Integer score = scores.get(profile);

        Integer max = MAX_SCORES.get(profile);

        return Math.round(((score * 100.0) / max) * 100.0) / 100.0;
    }
}
