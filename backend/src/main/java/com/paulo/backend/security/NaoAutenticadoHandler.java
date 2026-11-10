package com.paulo.backend.security;

import com.paulo.backend.dtos.Api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

@Component
public class NaoAutenticadoHandler implements AuthenticationEntryPoint {

    private final JsonMapper jsonMapper;

    public NaoAutenticadoHandler(JsonMapper jsonMapper){
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {

        ApiResponse<Void> resposta = ApiResponse.erro(
                "Autenticação necessária. Faça login para acessar este recurso.",
                null
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        jsonMapper.writeValue(response.getWriter(), resposta);
    }


}
