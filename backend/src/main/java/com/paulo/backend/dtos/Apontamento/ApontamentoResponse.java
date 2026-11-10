package com.paulo.backend.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ApontamentoResponse(
        Long id,
        String titulo,
        Long idResponsavel,
        String responsavel,
        LocalDate prazo,
        String situacao,
        Long idCriador,
        LocalDateTime dtCriacao

) {
}
