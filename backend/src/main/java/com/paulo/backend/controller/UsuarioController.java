package com.paulo.backend.controller;

import com.paulo.backend.dtos.Api.ApiResponse;
import com.paulo.backend.dtos.Usuario.UsuarioRequest;
import com.paulo.backend.dtos.Usuario.UsuarioResponse;
import com.paulo.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@Tag(name = "Usuários", description = "Gerenciamento de usuários")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }



    @PostMapping("/create-admin")
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


    @PostMapping("/create-user")
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

    @GetMapping("/all-user")
    @Operation(
            summary = "Listar todos os usuários",
            description = "Listar todos os usuários somente acesso ADMIN"
    )
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> todosUsuarios(){
        List<UsuarioResponse> usuarios = usuarioService.listarTodosOsUsuarios();

        ApiResponse<List<UsuarioResponse>> resposta = ApiResponse.sucesso(
                "Lista de usuários ADMIN e USER",
                usuarios
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resposta);
    }

    @GetMapping("/find-user")
    @Operation(
            summary = "Listar todos os usuários",
            description = "Listar todos os usuários somente acesso ADMIN"
    )
    public ResponseEntity<ApiResponse<UsuarioResponse>> listarUsuarioEspecifico(
            @RequestParam("usuario") String usuario
            )
    {
        UsuarioResponse dados = usuarioService.listarUsuarioEspecifico(usuario);

        ApiResponse<UsuarioResponse> resposta = ApiResponse.sucesso(
                "Usuário encontrado",
                dados
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resposta);
    }


}
