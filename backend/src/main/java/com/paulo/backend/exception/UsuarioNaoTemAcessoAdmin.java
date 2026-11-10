package com.paulo.backend.exception;

public class UsuarioNaoTemAcessoAdmin extends RuntimeException {
    public UsuarioNaoTemAcessoAdmin(String message) {
        super(message);
    }
}
