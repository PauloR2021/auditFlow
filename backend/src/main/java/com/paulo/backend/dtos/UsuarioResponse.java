package com.paulo.backend.dtos;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String  nome,
        String username,
        LocalDateTime dtCriacao,
        String acesso
) {
}
