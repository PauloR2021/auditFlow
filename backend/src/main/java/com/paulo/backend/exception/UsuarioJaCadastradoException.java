package com.paulo.backend.exception;

public class UsuarioJaCadastradoException extends RuntimeException {
    public UsuarioJaCadastradoException(String usuario) {
        super("O usuário '" + usuario + "' já está cadastrado");
    }
}
