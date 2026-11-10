package com.paulo.backend.exception;

public class ApontamentoFinalizado extends RuntimeException {
    public ApontamentoFinalizado(String titulo) {

        super("O apontamento '" + titulo+ "' já está finalizado, não é possível fazer mudanças");
    }
}
