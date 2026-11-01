package com.paulo.backend.exception;

public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException(String usuario) {

        super("O usuário '" + usuario + "' não está cadastrado");
    }
}
