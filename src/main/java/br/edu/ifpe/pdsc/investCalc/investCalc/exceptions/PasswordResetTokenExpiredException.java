package br.edu.ifpe.pdsc.investCalc.investCalc.exceptions;

public class PasswordResetTokenExpiredException extends RuntimeException {
    public PasswordResetTokenExpiredException() {
        super("Token de recuperacao expirado");
    }
}