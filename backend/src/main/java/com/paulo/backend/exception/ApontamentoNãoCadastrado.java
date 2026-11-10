package com.paulo.backend.exception;

public class ApontamentoNãoCadastrado extends RuntimeException {
    public ApontamentoNãoCadastrado(String message) {
        super(message);
    }
}
