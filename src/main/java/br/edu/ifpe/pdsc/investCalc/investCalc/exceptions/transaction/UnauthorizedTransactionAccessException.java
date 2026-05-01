package br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.transaction;

public class UnauthorizedTransactionAccessException extends RuntimeException {
    public UnauthorizedTransactionAccessException() {
        super("Usuario nao autorizado a realizar esta operacao");
    }
}
