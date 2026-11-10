package com.paulo.backend.exception;

public class UsuarioNaoTemAcessoAdmin extends RuntimeException {
    public UsuarioNaoTemAcessoAdmin(String usuario) {
        super("O usuário '" + usuario + "' não é um usuário com acesso ADMIN");
    }
}
