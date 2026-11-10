package com.paulo.backend.exception;

public class ApontamentoNãoCadastrado extends RuntimeException {
    public ApontamentoNãoCadastrado(Long id) {

        super("O apontamento '" + id + "' não está cadastrado");
    }
}
