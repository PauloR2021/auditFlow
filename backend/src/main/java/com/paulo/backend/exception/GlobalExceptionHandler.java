package com.paulo.backend.exception;

import com.paulo.backend.dtos.Api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UsuarioJaCadastradoException.class)
    public ResponseEntity<ApiResponse<Void>> tratarUsuarioDuplicado(UsuarioJaCadastradoException exception){
        ApiResponse<Void> resposta = ApiResponse.erro(
                exception.getMessage(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(resposta);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> tratarValidacao(MethodArgumentNotValidException exception){
        Map<String, List<String>> erros = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        erro -> erro.getField(),
                        Collectors.mapping(
                                erro -> erro.getDefaultMessage(),
                                Collectors.toList()
                        )
                ));

        ApiResponse<Map<String, List<String>>> resposta =
                ApiResponse.erro(
                        "Existem campos inválidos na requisição",
                        erros
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(resposta);
    }

    @ExceptionHandler(UsuarioNaoCadastradoException.class)
    public ResponseEntity<ApiResponse<Void>> tratarUsuarioDuplicado(UsuarioNaoCadastradoException exception){
        ApiResponse<Void> resposta = ApiResponse.erro(
                exception.getMessage(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(resposta);
    }
}
