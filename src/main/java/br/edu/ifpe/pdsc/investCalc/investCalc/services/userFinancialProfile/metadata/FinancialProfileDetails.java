package br.edu.ifpe.pdsc.investCalc.investCalc.services.userFinancialProfile.metadata;

import java.util.List;

public record FinancialProfileDetails(

                String description,

                List<String> strengths,

                List<String> limitations,

                List<String> recommendations,

                List<String> suggestedGoals) {
}
