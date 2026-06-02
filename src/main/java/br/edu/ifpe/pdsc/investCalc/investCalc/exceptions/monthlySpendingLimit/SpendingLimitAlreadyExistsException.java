package br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.monthlySpendingLimit;

public class SpendingLimitAlreadyExistsException extends RuntimeException {

    public SpendingLimitAlreadyExistsException() {
        super("Limite mensal ja configurado para este usuario.");
    }
}
