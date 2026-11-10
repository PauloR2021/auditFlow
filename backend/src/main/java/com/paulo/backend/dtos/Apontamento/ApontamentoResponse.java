package com.paulo.backend.dtos.Apontamento;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ApontamentoResponse(
        Long id,
        String titulo,
        String descricao,
        Long idResponsavel,
        String responsavel,
        LocalDate prazo,
        String situacao,
        Long idCriador,
        String usuarioCriador,
        LocalDateTime dtCriacao

) {
}
