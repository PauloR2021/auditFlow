package com.paulo.backend.dtos;

import java.time.Instant;

public record ApiResponse<T>(
        boolean sucesso,
        String mensagem,
        T dados,
        Instant timestamp
) {
    public static <T> ApiResponse<T> sucesso(
            String mensagem,
            T dados
    ) {
        return new ApiResponse<>(
                true,
                mensagem,
                dados,
                Instant.now()
        );
    }

    public static <T> ApiResponse<T> erro(
            String mensagem,
            T detalhes
    ) {
        return new ApiResponse<>(
                false,
                mensagem,
                detalhes,
                Instant.now()
        );
    }
}
