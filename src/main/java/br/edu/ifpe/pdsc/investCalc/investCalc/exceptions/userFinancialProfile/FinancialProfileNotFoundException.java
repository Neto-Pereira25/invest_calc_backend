package br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.userFinancialProfile;

public class FinancialProfileNotFoundException extends RuntimeException {

    public FinancialProfileNotFoundException() {
        super("Perfil financeiro nao encontrado");
    }
}