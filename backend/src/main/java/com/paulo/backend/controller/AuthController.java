package com.paulo.backend.controller;


import com.paulo.backend.dtos.Api.ApiResponse;
import com.paulo.backend.dtos.Login.TrocarSenhaRequest;
import com.paulo.backend.dtos.Usuario.UsuarioResponse;
import com.paulo.backend.entity.usuario.UsuarioEntity;
import com.paulo.backend.repository.UsuarioRepository;
import com.paulo.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * AUTENTICAÇÃO POR SESSÃO — SEM JWT
 *
 * Esta API utiliza o Spring Security para autenticar usuários
 * e manter o login por meio de uma sessão no servidor.
 *
 * O cliente guarda um cookie chamado JSESSIONID, que identifica
 * essa sessão. Não é necessário gerar nem enviar um JWT.
 *
 * FLUXO DE UTILIZAÇÃO:
 *
 * 1. GET /auth/csrf
 *    - Deve ser chamado antes do login.
 *    - Retorna o token de proteção CSRF.
 *    - O cliente deve guardar os cookies recebidos.
 *
 * 2. POST /auth/login
 *    - É processado pelo Spring Security, conforme a configuração
 *      loginProcessingUrl("/auth/login") na SecurityConfig.
 *    - Não precisa de um método @PostMapping neste controller.
 *    - Recebe "usuario" e "senha" no formato:
 *      application/x-www-form-urlencoded.
 *    - Exige o cookie e o cabeçalho X-CSRF-TOKEN com o valor
 *      obtido em GET /auth/csrf.
 *    - O Spring busca o usuário pelo UserDetailsService e compara
 *      a senha recebida com o hash BCrypt armazenado no banco.
 *    - Se as credenciais forem válidas, mantém a autenticação
 *      na sessão e atualiza o identificador de sessão.
 *    - Na configuração atual, retorna 200 sem corpo em caso de sucesso.
 *
 * 3. GET /auth/me
 *    - Retorna os dados do usuário autenticado.
 *    - O cliente deve enviar o cookie de sessão atualizado.
 *    - Não realiza login: apenas consulta a identidade conectada.
 *    - Sem sessão autenticada, retorna 401.
 *
 * 4. OPERAÇÕES APÓS O LOGIN
 *    - Após autenticar, chamar GET /auth/csrf novamente, pois
 *      o token CSRF anterior é descartado durante o login.
 *    - Enviar o cookie de sessão nas próximas requisições.
 *    - Nas operações POST, PUT, PATCH e DELETE, enviar também
 *      o token atual no cabeçalho X-CSRF-TOKEN.
 *    - O Spring verifica as permissões carregadas no login.
 *      Exemplo: hasAuthority("ADMIN") exige o perfil ADMIN.
 *
 * 5. POST /auth/logout
 *    - É processado pelo Spring Security.
 *    - Exige CSRF válido e o cookie da sessão.
 *    - Encerra a sessão e remove o cookie JSESSIONID.
 *    - Na configuração atual, retorna 204.
 *    - Antes de um novo login, obter outro CSRF em /auth/csrf.
 *
 * DIFERENÇA ENTRE SESSÃO, PERFIL E CSRF:
 *
 * - JSESSIONID: identifica a sessão mantida no servidor.
 * - Perfil de acesso: determina quais operações são permitidas.
 * - Token CSRF: protege contra requisições forjadas que tentam
 *   aproveitar os cookies enviados automaticamente pelo navegador.
 *   Não é um JWT e não autentica o usuário.
 *
 * OBSERVAÇÕES:
 *
 * - permitAll() libera o acesso sem login, mas não desativa o CSRF.
 * - O cliente precisa preservar os cookies entre as requisições.
 * - Em chamadas React com fetch, usar credentials: "include"
 *   quando necessário para enviar e receber cookies entre origens.
 *   Nesse cenário, o back-end também precisa de CORS apropriado.
 * - A sessão pode expirar por inatividade. Se isso ocorrer,
 *   será necessário autenticar novamente.
 * - Nesta configuração, as sessões ficam na memória do servidor
 *   e normalmente são perdidas quando a aplicação é reiniciada.
 */

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    public AuthController(UsuarioRepository usuarioRepository, UsuarioService usuarioService)
    {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/csrf")
    public CsrfResponse csrf(CsrfToken csrfToken) {
        return new CsrfResponse(
                csrfToken.getHeaderName(),
                csrfToken.getToken()
        );
    }

    @PatchMapping("/primeiro-acesso")
    @Operation(
            summary = "Trocar senha no primeiro acesso",
            description = "Altera a senha do usuário da sessão autenticada."
    )
    public ResponseEntity<ApiResponse<Void>> trocarSenhaPrimeiroAcesso(
            Authentication authentication,
            @Valid @RequestBody TrocarSenhaRequest request
    ) {
        usuarioService.trocarSenhaPrimeiroAcesso(
                authentication.getName(),
                request
        );

        ApiResponse<Void> resposta = ApiResponse.sucesso(
                "Senha alterada com sucesso",
                null
        );

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UsuarioResponse>> usuarioLogado(
            Authentication authentication
    ) {
        UsuarioEntity usuario = usuarioRepository
                .findByUsuario(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário da sessão não encontrado"
                ));

        UsuarioResponse dados = new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getUsuario(),
                usuario.getDtCriacao(),
                usuario.getAcesso().toString(),
                usuario.getPrimeiroAcesso(),
                usuario.getTrocouSenha()
        );

        return ResponseEntity.ok(
                ApiResponse.sucesso(
                        "Usuário autenticado",
                        dados
                )
        );
    }

    public record CsrfResponse(
            String headerName,
            String token
    ) {
    }

}
