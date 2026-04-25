package br.edu.ifpe.pdsc.investCalc.investCalc.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Usuário não encontrado");
    }
}
