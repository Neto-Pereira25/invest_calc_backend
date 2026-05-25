package br.edu.ifpe.pdsc.investCalc.investCalc.exceptions;

public class InvalidPasswordResetTokenException extends RuntimeException {
    public InvalidPasswordResetTokenException() {
        super("Token de recuperacao invalido");
    }
}