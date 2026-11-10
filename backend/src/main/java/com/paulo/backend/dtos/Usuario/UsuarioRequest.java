package com.paulo.backend.dtos.Usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve ter até 100 caracteres")
        String nome,

        @NotBlank(message = "O usuário é obrigatório")
        @Size(
                min = 3,
                max = 20,
                message = "O usuário deve ter entre 3 e 20 caracteres"
        )
        @Pattern(
                regexp = "^[a-zA-Z0-9._-]+$",
                message = "O usuário deve conter apenas letras, números, ponto, hífen ou sublinhado"
        )
        String usuario

) {
}
