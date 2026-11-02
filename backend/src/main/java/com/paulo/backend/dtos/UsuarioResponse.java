package com.paulo.backend.dtos;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String  nome,
        String usuario,
        LocalDateTime dtCriacao,
        String acesso
) {
}
