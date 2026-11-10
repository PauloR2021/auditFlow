package com.paulo.backend.dtos.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String  nome,
        String usuario,
        LocalDateTime dtCriacao,
        String acesso,
        Boolean primeiroAcesso,
        Boolean trocouSenha
) {
}
