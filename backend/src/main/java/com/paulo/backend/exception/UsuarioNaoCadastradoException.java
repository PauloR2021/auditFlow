package com.paulo.backend.exception;

public class UsuarioNaoCadastradoException extends RuntimeException {
    public UsuarioNaoCadastradoException(String usuario) {

        super("O usuário '" + usuario + "' não está cadastrado");
    }
}
