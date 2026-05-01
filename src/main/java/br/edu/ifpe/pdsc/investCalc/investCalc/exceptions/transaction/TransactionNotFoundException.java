package br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.transaction;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException() {
        super("Transacao nao encontrada");
    }
}
