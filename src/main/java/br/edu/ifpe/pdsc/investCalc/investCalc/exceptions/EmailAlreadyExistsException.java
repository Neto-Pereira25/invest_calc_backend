package br.edu.ifpe.pdsc.investCalc.investCalc.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("Email ja cadastrado");
    }
}
