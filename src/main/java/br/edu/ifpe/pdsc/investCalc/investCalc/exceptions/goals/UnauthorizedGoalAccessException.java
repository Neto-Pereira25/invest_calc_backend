package br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.goals;

public class UnauthorizedGoalAccessException extends RuntimeException {
    public UnauthorizedGoalAccessException() {
        super("Voce nao possui permissao para acessar esta meta financeira");
    }

}
