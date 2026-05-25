package br.edu.ifpe.pdsc.investCalc.investCalc.exceptions;

public class PasswordResetTokenAlreadyUsedException extends RuntimeException {
    public PasswordResetTokenAlreadyUsedException() {
        super("Token de recuperacao ja utilizado");
    }
}