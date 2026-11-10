package com.paulo.backend.dtos.Apontamento;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ApontamentoRequest(
        String titulo,
        String descricao,

        @NotNull(message = "O responsável é obrigatório")
        String responsavel,

        LocalDate prazo,

        @NotNull(message = "O responsável é obrigatório")
        Long idCriador

) {
}
