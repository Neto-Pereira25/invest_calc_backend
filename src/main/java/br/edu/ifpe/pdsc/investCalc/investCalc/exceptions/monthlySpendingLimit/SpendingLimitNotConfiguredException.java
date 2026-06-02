package br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.monthlySpendingLimit;

public class SpendingLimitNotConfiguredException extends RuntimeException {

    public SpendingLimitNotConfiguredException() {
        super("Nenhum limite mensal configurado.");
    }
}
