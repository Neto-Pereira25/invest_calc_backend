package br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import br.edu.ifpe.pdsc.investCalc.investCalc.enums.userFinancialProfile.FinancialProfile;
import br.edu.ifpe.pdsc.investCalc.investCalc.enums.userFinancialProfile.FinancialProfileOption;

public final class FinancialProfileRules {

    private FinancialProfileRules() {
    }

    public static final Map<Integer, Map<FinancialProfileOption, Map<FinancialProfile, Integer>>> RULES = new HashMap<>();

    static {

        /*
         * PERGUNTA 1
         */

        Map<FinancialProfileOption, Map<FinancialProfile, Integer>> question1 = new EnumMap<>(
                FinancialProfileOption.class);

        question1.put(
                FinancialProfileOption.A,
                score(FinancialProfile.DESLIGADO, 4));

        question1.put(
                FinancialProfileOption.B,
                score(FinancialProfile.DESLIGADO, 3));

        question1.put(
                FinancialProfileOption.C,
                score(
                        FinancialProfile.DESLIGADO,
                        FinancialProfile.POUPADOR,
                        1));

        question1.put(
                FinancialProfileOption.D,
                score(FinancialProfile.POUPADOR, 3));

        question1.put(
                FinancialProfileOption.E,
                score(
                        FinancialProfile.POUPADOR, 3,
                        FinancialProfile.INVESTIDOR, 2));

        RULES.put(1, question1);

        /*
         * PERGUNTA 2
         */

        Map<FinancialProfileOption, Map<FinancialProfile, Integer>> question2 = new EnumMap<>(
                FinancialProfileOption.class);

        question2.put(
                FinancialProfileOption.A,
                score(FinancialProfile.DESLIGADO, 4));

        question2.put(
                FinancialProfileOption.B,
                score(FinancialProfile.DESLIGADO, 2));

        question2.put(
                FinancialProfileOption.C,
                score(FinancialProfile.POUPADOR, 1));

        question2.put(
                FinancialProfileOption.D,
                score(
                        FinancialProfile.POUPADOR,
                        FinancialProfile.INVESTIDOR,
                        2));

        RULES.put(2, question2);

        /*
         * PERGUNTA 3
         */

        Map<FinancialProfileOption, Map<FinancialProfile, Integer>> question3 = new EnumMap<>(
                FinancialProfileOption.class);

        question3.put(
                FinancialProfileOption.A,
                score(FinancialProfile.DEVEDOR, 4));

        question3.put(
                FinancialProfileOption.B,
                score(FinancialProfile.DEVEDOR, 3));

        question3.put(
                FinancialProfileOption.C,
                score(FinancialProfile.DEVEDOR, 1));

        question3.put(
                FinancialProfileOption.D,
                score(
                        FinancialProfile.POUPADOR,
                        FinancialProfile.INVESTIDOR,
                        2));

        RULES.put(3, question3);

        /*
         * PERGUNTA 4
         */

        Map<FinancialProfileOption, Map<FinancialProfile, Integer>> question4 = new EnumMap<>(
                FinancialProfileOption.class);

        question4.put(
                FinancialProfileOption.A,
                score(FinancialProfile.GASTADOR, 4));

        question4.put(
                FinancialProfileOption.B,
                score(FinancialProfile.GASTADOR, 3));

        question4.put(
                FinancialProfileOption.C,
                score(
                        FinancialProfile.GASTADOR,
                        FinancialProfile.POUPADOR,
                        1));

        question4.put(
                FinancialProfileOption.D,
                score(FinancialProfile.POUPADOR, 3));

        question4.put(
                FinancialProfileOption.E,
                score(FinancialProfile.INVESTIDOR, 4));

        RULES.put(4, question4);

        /*
         * PERGUNTA 5
         */

        Map<FinancialProfileOption, Map<FinancialProfile, Integer>> question5 = new EnumMap<>(
                FinancialProfileOption.class);

        question5.put(
                FinancialProfileOption.A,
                score(FinancialProfile.GASTADOR, 4));

        question5.put(
                FinancialProfileOption.B,
                score(FinancialProfile.GASTADOR, 3));

        question5.put(
                FinancialProfileOption.C,
                score(FinancialProfile.GASTADOR, 1));

        question5.put(
                FinancialProfileOption.D,
                score(
                        FinancialProfile.POUPADOR, 2));

        question5.put(
                FinancialProfileOption.E,
                score(
                        FinancialProfile.POUPADOR,
                        FinancialProfile.INVESTIDOR,
                        2));

        RULES.put(5, question5);

        /*
         * PERGUNTA 6
         */

        Map<FinancialProfileOption, Map<FinancialProfile, Integer>> question6 = new EnumMap<>(
                FinancialProfileOption.class);

        question6.put(
                FinancialProfileOption.A,
                score(FinancialProfile.DEVEDOR, 2));

        question6.put(
                FinancialProfileOption.B,
                score(FinancialProfile.POUPADOR, 1));

        question6.put(
                FinancialProfileOption.C,
                score(FinancialProfile.POUPADOR, 2));

        question6.put(
                FinancialProfileOption.D,
                score(
                        FinancialProfile.POUPADOR, 3));

        question6.put(
                FinancialProfileOption.E,
                score(
                        FinancialProfile.POUPADOR,
                        FinancialProfile.INVESTIDOR,
                        3));

        RULES.put(6, question6);

        /*
         * PERGUNTA 7
         */

        Map<FinancialProfileOption, Map<FinancialProfile, Integer>> question7 = new EnumMap<>(
                FinancialProfileOption.class);

        question7.put(
                FinancialProfileOption.A,
                score(
                        FinancialProfile.GASTADOR,
                        FinancialProfile.DEVEDOR,
                        2));

        question7.put(
                FinancialProfileOption.B,
                score(FinancialProfile.GASTADOR, 1));

        question7.put(
                FinancialProfileOption.C,
                score(FinancialProfile.POUPADOR, 2));

        question7.put(
                FinancialProfileOption.D,
                score(
                        FinancialProfile.POUPADOR, 3));

        question7.put(
                FinancialProfileOption.E,
                score(
                        FinancialProfile.INVESTIDOR, 4));

        RULES.put(7, question7);

        /*
         * PERGUNTA 8
         */

        Map<FinancialProfileOption, Map<FinancialProfile, Integer>> question8 = new EnumMap<>(
                FinancialProfileOption.class);

        question8.put(
                FinancialProfileOption.A,
                score(FinancialProfile.DESLIGADO, 2));

        question8.put(
                FinancialProfileOption.B,
                score(FinancialProfile.POUPADOR, 1));

        question8.put(
                FinancialProfileOption.C,
                score(FinancialProfile.INVESTIDOR, 2));

        question8.put(
                FinancialProfileOption.D,
                score(FinancialProfile.INVESTIDOR, 3));

        question8.put(
                FinancialProfileOption.E,
                score(FinancialProfile.INVESTIDOR, 4));

        RULES.put(8, question8);

        /*
         * PERGUNTA 9
         */

        Map<FinancialProfileOption, Map<FinancialProfile, Integer>> question9 = new EnumMap<>(
                FinancialProfileOption.class);

        question9.put(
                FinancialProfileOption.A,
                score(FinancialProfile.DEVEDOR, 4));

        question9.put(
                FinancialProfileOption.B,
                score(
                        FinancialProfile.DEVEDOR, 2,
                        FinancialProfile.GASTADOR, 1));

        question9.put(
                FinancialProfileOption.C,
                score(FinancialProfile.DEVEDOR, 1));

        question9.put(
                FinancialProfileOption.D,
                score(FinancialProfile.POUPADOR, 3));

        question9.put(
                FinancialProfileOption.E,
                score(FinancialProfile.INVESTIDOR, 3));

        RULES.put(9, question9);

        /*
         * PERGUNTA 10
         */

        Map<FinancialProfileOption, Map<FinancialProfile, Integer>> question10 = new EnumMap<>(
                FinancialProfileOption.class);

        question10.put(
                FinancialProfileOption.A,
                score(FinancialProfile.DEVEDOR, 4));

        question10.put(
                FinancialProfileOption.B,
                score(FinancialProfile.DESLIGADO, 4));

        question10.put(
                FinancialProfileOption.C,
                score(FinancialProfile.GASTADOR, 4));

        question10.put(
                FinancialProfileOption.D,
                score(FinancialProfile.POUPADOR, 4));

        question10.put(
                FinancialProfileOption.E,
                score(FinancialProfile.INVESTIDOR, 4));

        RULES.put(10, question10);
    }

    private static Map<FinancialProfile, Integer> score(
            FinancialProfile profile,
            Integer points) {
        return Map.of(profile, points);
    }

    private static Map<FinancialProfile, Integer> score(
            FinancialProfile profile1,
            FinancialProfile profile2,
            Integer points) {
        return Map.of(
                profile1, points,
                profile2, points);
    }

    private static Map<FinancialProfile, Integer> score(
            FinancialProfile profile1,
            Integer points1,
            FinancialProfile profile2,
            Integer points2) {
        return Map.of(
                profile1, points1,
                profile2, points2);
    }

}
