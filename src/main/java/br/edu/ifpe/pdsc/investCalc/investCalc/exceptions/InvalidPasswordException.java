package br.edu.ifpe.pdsc.investCalc.investCalc.exceptions;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Senha invalida");
    }
}
