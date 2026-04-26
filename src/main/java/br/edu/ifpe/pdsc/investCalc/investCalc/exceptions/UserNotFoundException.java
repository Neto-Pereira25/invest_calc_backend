package br.edu.ifpe.pdsc.investCalc.investCalc.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Usuario nao encontrado");
    }
}
