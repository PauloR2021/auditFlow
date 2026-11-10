package com.paulo.backend.exception;

public class ApontamentoFinalizado extends RuntimeException {
    public ApontamentoFinalizado(String message) {
        super(message);
    }
}
