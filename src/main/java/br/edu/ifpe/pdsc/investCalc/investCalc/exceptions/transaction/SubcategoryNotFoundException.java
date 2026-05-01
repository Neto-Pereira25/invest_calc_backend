package br.edu.ifpe.pdsc.investCalc.investCalc.exceptions.transaction;

public class SubcategoryNotFoundException extends RuntimeException {
    public SubcategoryNotFoundException() {
        super("Subcategoria nao encontrada");
    }
}
