package com.paulo.backend.controller;

import com.paulo.backend.dtos.ApiResponse;
import com.paulo.backend.dtos.UsuarioRequest;
import com.paulo.backend.dtos.UsuarioResponse;
import com.paulo.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
@Tag(name = "Usuários", description = "Gerenciamento de usuários")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    @PostMapping("/admin")
    @Operation(
            summary = "Cadastrar usuáro admin",
            description = "Cadastra um novo usuário no sistema."
    )
    public ResponseEntity<ApiResponse<UsuarioResponse>> criarAdmin(
            @Valid @RequestBody UsuarioRequest request)
    {
        UsuarioResponse usuario = usuarioService.criarUsuarioAdmin(request);

        ApiResponse<UsuarioResponse> resposta = ApiResponse.sucesso(
                "Administrador cadastrado com sucesso",
                usuario
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resposta);
    }


    @PostMapping("/user")
    @Operation(
            summary = "Cadastrar usuáro user",
            description = "Cadastra um novo usuário no sistema."
    )
    public ResponseEntity<ApiResponse<UsuarioResponse>> criarUser(
            @Valid @RequestBody UsuarioRequest request)
    {
        UsuarioResponse usuario = usuarioService.criarUsuarioUser(request);

        ApiResponse<UsuarioResponse> resposta = ApiResponse.sucesso(
                "Usuario cadastrado com sucesso",
                usuario
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resposta);
    }
}
