package com.paulo.backend.controller;

import com.paulo.backend.dtos.Api.ApiResponse;
import com.paulo.backend.dtos.Apontamento.ApontamentoRequest;
import com.paulo.backend.dtos.Apontamento.ApontamentoResponse;
import com.paulo.backend.service.ApontamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apontamento")
@Tag(name = "Apontamentos", description = "Gerenciamento de apontamentos")
public class ApontamentoController {
    private final ApontamentoService apontamentoService;

    public ApontamentoController(ApontamentoService apontamentoService){
        this.apontamentoService = apontamentoService;
    }

    @PostMapping("/create")
    @Operation(
            summary = "Cadastrar apontamento",
            description = "Cadastar novos apontantamentos no sistema"
    )
    public ResponseEntity<ApiResponse<ApontamentoResponse>> createApontamento(
            @Valid @RequestBody ApontamentoRequest request
    ){
        ApontamentoResponse apontamento = apontamentoService.create(request);

        ApiResponse<ApontamentoResponse> resposta = ApiResponse.sucesso(
                "Apontamento dadastrado com sucesso",
                apontamento
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resposta);
    }

    @GetMapping("/all-apontamentos")
    @Operation(
            summary = "Listar todos apontamentos",
            description = "Listar todos os apontamentos cadastrados no sistema"
    )
    public ResponseEntity<ApiResponse<List<ApontamentoResponse>>> listarTodosOsApontamentos()
    {
        List<ApontamentoResponse> apontamentos = apontamentoService.listarTodosApontamentos();
        ApiResponse<List<ApontamentoResponse>> resposta = ApiResponse.sucesso(
                "Lista de todos os apontamentos",
                apontamentos
        );

        return ResponseEntity
                .status((HttpStatus.OK))
                .body(resposta);

    }

    @GetMapping("all-apontamentos/id-responsavel/{id}")
    @Operation(
            summary = "Listar todos os apontamentos",
            description = "Listar todos os apontamentos dos usuários responsaveis"
    )
    public ResponseEntity<ApiResponse<List<ApontamentoResponse>>> listarApontamentosIdResponsavel(
            @PathVariable("id") Long id
    )
    {
        List<ApontamentoResponse> apontamento = apontamentoService.listarApontamentosIdResponsavel(id);

        ApiResponse<List<ApontamentoResponse>> resposta = ApiResponse.sucesso(
                "Lista de todos os apontametnos",
                apontamento
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resposta);

    }

    @PutMapping("/{id}/concluido")
    @Operation(
            summary = "Concluir apontamento",
            description = "Concluir o apontamento"
    )
    public ResponseEntity<ApiResponse<ApontamentoResponse>> concluirApontamento(
            @PathVariable("id") Long id
    )
    {
        ApontamentoResponse apontamento = apontamentoService.mudarSituacaoParaConculuido(id);

        ApiResponse<ApontamentoResponse> resposta = ApiResponse.sucesso(
                "Apontamento mudou para concluido com sucesso",
                apontamento
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resposta);
    }

    @PutMapping("/{id}/pendente")
    @Operation(
            summary = "Pendente apontamento",
            description = "Deixar como pendente o apontamento"
    )
    public ResponseEntity<ApiResponse<ApontamentoResponse>> pendenteApontamento(
            @PathVariable("id") Long id
    )
    {
        ApontamentoResponse apontamento = apontamentoService.mudarSituacaoParaPendente(id);

        ApiResponse<ApontamentoResponse> resposta = ApiResponse.sucesso(
                "Apontamento mudou para pendente com sucesso",
                apontamento
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resposta);
    }

    @PutMapping("/{id}/finalizado")
    @Operation(
            summary = "Finalizar apontamento",
            description = "Deixar como finalizado o apontamento"
    )
    public ResponseEntity<ApiResponse<ApontamentoResponse>> finalizadoApontamento(
            @PathVariable("id") Long id
    )
    {
        ApontamentoResponse apontamento = apontamentoService.mudarSituacaoParaFinalizado(id);

        ApiResponse<ApontamentoResponse> resposta = ApiResponse.sucesso(
                "Apontamento mudou para finalizado com sucesso",
                apontamento
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resposta);
    }

    @PutMapping("/{id}/deletar-responsavel")
    @Operation(
            summary = "Deletar responsável pelo apontamento",
            description = "Deixar o responsável pelo apontamento"
    )
    public ResponseEntity<ApiResponse<ApontamentoResponse>> deletarResponsavelDoApontamento(
            @PathVariable("id") Long id
    )
    {
        ApontamentoResponse apontamento = apontamentoService.removerResponsavelDoApontamento(id);

        ApiResponse<ApontamentoResponse> resposta = ApiResponse.sucesso(
                "Responsável pelo apontamento removido com sucesso",
                apontamento
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resposta);
    }



    @DeleteMapping("/deletar/{id}")
    @Operation(
            summary = "Deletar apontamento",
            description = "Deletar o apontamento"
    )
    public ResponseEntity<ApiResponse<ApontamentoResponse>> deletarApontamentoCompleto(
            @PathVariable("id") Long id
    )
    {
        ApontamentoResponse apontamento = apontamentoService.deletarApontamento(id);

        ApiResponse<ApontamentoResponse> resposta = ApiResponse.sucesso(
                "Apontamento deletado com sucesso",
                apontamento
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resposta);
    }
}
