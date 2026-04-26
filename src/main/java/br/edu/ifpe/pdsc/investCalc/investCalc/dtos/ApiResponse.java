package br.edu.ifpe.pdsc.investCalc.investCalc.dtos;

public class ApiResponse<T> {

    private T data;
    private String message;

    public ApiResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
