package br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.goals;

public class GoalNotFoundException extends RuntimeException {
    public GoalNotFoundException() {
        super("Meta financeira não encontrada");
    }

}
