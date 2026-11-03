package com.paulo.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TrocarSenhaRequest(
        @NotBlank(message = "A nova senha é obrigatória")
        @Size(
                min = 8,
                max = 64,
                message = "A senha deve ter entre 8 e 64 caracteres"
        )
        String senha,

        @NotBlank(message = "Confirme a nova senha")
        String confirmarSenha
) {
}
