# AuditFlow — Back-end

API para o projeto AuditFlow, voltado à gestão de auditorias, apontamentos e planos de ação. 
Esta etapa documenta o cadastro de usuários e a autenticação por sessão, com controle de acesso por perfil, lista os usuários cadastrados respeitando o acesso 'ADMIN''.

> Este README descreve o fluxo e a configuração desenvolvidos até aqui. Os exemplos usam a API em `http://localhost:8085` e os perfis `ADMIN` e `USER`.

## Tecnologias

- Java e Spring Boot.
- Spring Web, Spring Security e Spring Data JPA.
- PostgreSQL.
- Bean Validation para validar os dados de entrada.
- BCrypt para armazenar hashes de senha.
- Lombok.
- springdoc-openapi e Swagger UI para documentação.
- Maven e Bruno para execução e testes manuais.

## Organização

Pacote principal: `com.paulo.backend`.

| Pacote | Responsabilidade |
|---|---|
| `controller` | Receber requisições e montar respostas HTTP |
| `dtos` | Definir dados de entrada, saída e o retorno `ApiResponse` |
| `entity` | Mapear as entidades e os perfis de acesso |
| `repository` | Consultar e persistir dados |
| `service` | Executar regras de negócio e carregar o usuário para autenticação |
| `security` | Configurar sessão, autorização, CSRF e PasswordEncoder |
| `exception` | Definir exceções e centralizar o tratamento de erros dos controllers |

O `UsuarioService` implementa `UserDetailsService`. O método `loadUserByUsername()` busca o usuário pelo login e entrega ao Spring Security o hash da senha e o perfil de acesso.

Mantenha apenas uma implementação de `UserDetailsService` para esse fluxo e um único bean `PasswordEncoder`.

## Configuração e execução

Tenha um JDK compatível com a versão definida no `pom.xml`, Maven e uma instância PostgreSQL acessível. O banco `auditflow` precisa existir antes de iniciar a aplicação.

Crie `.env` na pasta da API, ao lado de `pom.xml`:

```dotenv
APP_PORT=8085
DB_URL=jdbc:postgresql://localhost:5432/auditflow
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
```

Os valores acima são exemplos. Ajuste as credenciais e a porta do banco. Nesta abordagem, o `.env` é interpretado como arquivo Java Properties: não use `export` nem aspas envolvendo os valores.

Configuração em `src/main/resources/application.properties`:

```properties
# Aplicação
spring.application.name=auditflow
spring.config.import=file:./.env[.properties]
server.port=${APP_PORT:8080}

# Banco de dados
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA — configuração de desenvolvimento
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Nesta etapa, Hibernate cria/atualiza tabelas pelas entidades
spring.flyway.enabled=false
spring.sql.init.mode=never

# Sessão
server.servlet.session.timeout=30m
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.same-site=lax
server.servlet.session.tracking-modes=cookie
```

O caminho `file:./.env` considera a pasta de execução. Execute a API a partir da pasta que contém o `.env` e o `pom.xml`:

```bash
mvn spring-boot:run
```

Se usar o Maven Wrapper do projeto:

```bash
./mvnw spring-boot:run
```

O Hibernate cria/atualiza tabelas nesta fase de desenvolvimento. Isso não cria o banco PostgreSQL nem substitui migrações versionadas. Quando o Flyway passar a controlar a estrutura, usar migrações e trocar `ddl-auto` para `validate`.

Não envie `.env` ao Git. Um `.env.example` pode ser versionado com valores fictícios.

Em produção HTTPS, configurar também `server.servlet.session.cookie.secure=true` no perfil apropriado. Essa opção não deve impedir o uso do cookie no desenvolvimento local por HTTP.

## Como funciona a autenticação

A API usa sessão no servidor, sem JWT. Após autenticar, o cliente mantém o cookie `JSESSIONID` e o envia nas próximas chamadas.

| Elemento | Função |
|---|---|
| `JSESSIONID` | Identifica a sessão mantida pelo servidor |
| `ADMIN` ou `USER` | Define as permissões carregadas durante o login |
| Token CSRF | Protege operações contra requisições forjadas que tentam aproveitar os cookies do navegador |

O CSRF não faz login e não concede permissões. A API continua exigindo CSRF mesmo quando uma rota possui `permitAll()`.

Na configuração atual:

- A sessão é criada quando necessária (`IF_REQUIRED`).
- O identificador de sessão é alterado durante o login.
- HTTP Basic está desativado.
- O login e o logout são processados pelos filtros do Spring Security.
- A sessão expira após o período configurado de inatividade.
- Sem armazenamento externo de sessões, reiniciar a aplicação normalmente encerra as sessões existentes.

O fluxo normal é:

1. Obter CSRF e cookies.
2. Enviar usuário e senha para o login, junto com o CSRF e os cookies.
3. Preservar o cookie atualizado recebido no login.
4. Obter um novo CSRF após autenticar.
5. Consultar a identidade em `/auth/me` e executar operações permitidas.
6. Enviar um POST de logout para encerrar a sessão.

## Endpoints

| Método | Caminho     | Finalidade                          | Acesso esperado |
|---|-------------|-------------------------------------|---|
| GET | `/auth/csrf` | Obter proteção CSRF                 | Público |
| POST | `/auth/login` | Autenticar usuário e iniciar sessão | Público, com CSRF válido |
| GET | `/auth/me`  | Consultar o usuário conectado       | Sessão autenticada |
| POST | `/usuario/admin` | Cadastrar administrador             | Sessão ADMIN e CSRF válido |
| POST | `/usuario/user` | Cadastrar usuário comum             | Sessão ADMIN e CSRF válido |
| POST | `/auth/logout` | Invalidar a sessão                  | Cookie da sessão e CSRF válido |
| GET | `/usuario/all-user` | Consultar todos os usuários         | Sessão ADMIN e CSRF válido |
| GET | `/find-user` | Consultar usuário expecifico        | Sessão ADMIN e CSRF válido |

### Primeiro administrador

O fluxo de cadastro protegido pressupõe que um primeiro ADMIN já exista no banco, com senha BCrypt. A criação inicial deve acontecer por uma inicialização controlada. Este README não inclui uma implementação automática dessa inicialização.

A liberação temporária de cadastros usada durante o desenvolvimento deve ser removida quando a autorização por ADMIN estiver ativa.

## Testando no Bruno

Crie uma coleção com as requisições abaixo. Use sempre a mesma origem, `http://localhost:8085`, sem alternar com `127.0.0.1`.

Mantenha o armazenamento e envio de cookies habilitados. Não configure Basic Auth ou Bearer Token: o fluxo usa o cookie da sessão.

### 1. Obter CSRF

```http
GET http://localhost:8085/auth/csrf
```

Sem corpo. Resposta de exemplo:

```json
{
  "headerName": "X-CSRF-TOKEN",
  "token": "valor-retornado-pela-api"
}
```

Copie o campo `token`. O Bruno deve preservar os cookies recebidos. Conforme a implementação do endpoint, a resposta também pode conter `parameterName`.

### 2. Fazer login

```http
POST http://localhost:8085/auth/login
```

Em **Headers**:

| Nome | Valor |
|---|---|
| `X-CSRF-TOKEN` | Token obtido na etapa 1 |

Em **Body**, escolha **Form URL Encoded**:

| Campo | Valor de exemplo |
|---|---|
| `usuario` | `admin_usuario_1` |
| `senha` | Senha original do usuário cadastrado |

O conteúdo é enviado como `application/x-www-form-urlencoded`, não JSON. O cliente envia a senha original; o servidor compara com o hash BCrypt armazenado.

Com os handlers atuais, o sucesso retorna **200 OK sem corpo**. O Bruno deve guardar o cookie atualizado após o login.

O endereço `/auth/login` é definido por `loginProcessingUrl("/auth/login")` na `SecurityConfig`. Não é necessário criar outro `@PostMapping` para processar esse login.

### 3. Atualizar o CSRF após login

Execute novamente:

```http
GET http://localhost:8085/auth/csrf
```

Copie o novo token e use-o nas próximas operações que alteram dados. O token anterior é descartado durante a autenticação.

### 4. Consultar o usuário conectado

```http
GET http://localhost:8085/auth/me
```

Sem corpo e sem necessidade de cabeçalho CSRF. O cookie da sessão deve ser enviado.

Exemplo de retorno, considerando o DTO `UsuarioResponse` com os cinco campos definidos no projeto:

```json
{
  "sucesso": true,
  "mensagem": "Usuário autenticado",
  "dados": {
    "id": 1,
    "nome": "Administrador de exemplo",
    "usuario": "admin_usuario_1",
    "dtCriacao": "2026-11-01T10:30:00",
    "acesso": "ADMIN"
  },
  "timestamp": "2026-11-01T13:30:00Z"
}
```

O `dtCriacao` é um `LocalDateTime`, sem informação de fuso. O `timestamp` do envelope é um `Instant`, representando um instante em UTC. Os valores acima são ilustrativos.

### 5. Cadastrar usuário comum

```http
POST http://localhost:8085/usuario/user
```

Em **Headers**:

| Nome | Valor |
|---|---|
| `Content-Type` | `application/json` |
| `X-CSRF-TOKEN` | Token atualizado da etapa 3 |

Em **Body → JSON**:

```json
{
  "nome": "Usuário de teste",
  "usuario": "usuario_teste",
  "senha": "senhaTeste123"
}
```

A sessão precisa pertencer a um ADMIN. O service atribui `USER` ao novo cadastro, gera o hash da senha e salva a entidade. O cliente não escolhe o perfil nesse corpo de requisição.

Para criar outro administrador, envie o mesmo formato para:

```http
POST http://localhost:8085/usuario/admin
```

Nesse caso, o service atribui `ADMIN`. O retorno esperado é **201 Created**, com os dados do usuário sem `senhaHash`.

Enquanto a sessão e o CSRF permanecerem válidos, não é necessário repetir o login para cada cadastro.

### 6. Fazer logout

```http
POST http://localhost:8085/auth/logout
```

Envie o cookie de sessão e o cabeçalho `X-CSRF-TOKEN` atual. Se precisar, obtenha o CSRF novamente antes de sair.

Deixe o corpo vazio. O retorno configurado é **204 No Content**. A sessão é invalidada e o cookie `JSESSIONID` é removido.

Para confirmar, chame `/auth/me` novamente: sem autenticação, o resultado esperado é **401 Unauthorized**.

Antes de um novo login, comece novamente por `/auth/csrf`.

## Cadastro e validação

O `UsuarioRequest` recebe `nome`, `usuario` e `senha`. Conforme as validações implementadas:

- Nome obrigatório, com até 100 caracteres.
- Usuário obrigatório, de 3 a 20 caracteres, usando letras ASCII, números, ponto, hífen ou sublinhado.
- Senha obrigatória, de 8 a 64 caracteres.

O controller usa `@Valid`. O service verifica login duplicado com `existsByUsuario()` e lança `UsuarioJaCadastradoException`.

A coluna `usuario` deve ter restrição de unicidade no banco. A consulta prévia melhora a mensagem, mas a restrição é quem impede duplicidades em inserções simultâneas.

A senha original não deve ser salva ou devolvida. O hash é gerado por `PasswordEncoder.encode()`. Na autenticação, a comparação é realizada pelo Spring Security usando o encoder configurado.

## Respostas e erros

Os controllers usam `ApiResponse<T>` para padronizar mensagem, dados e timestamp. O `GlobalExceptionHandler` trata as exceções dos controllers. Os handlers de segurança atuais retornam apenas status, sem corpo.

| Status | Significado neste fluxo |
|---|---|
| `200` | Login bem-sucedido ou consulta realizada |
| `201` | Usuário cadastrado |
| `204` | Logout concluído |
| `400` | Erro de validação do request |
| `401` | Sem autenticação, sessão expirada ou credenciais de login inválidas |
| `403` | CSRF ausente/inválido ou perfil sem permissão |
| `409` | Usuário duplicado, tratado pela exceção específica |

O tratamento específico de conflitos de unicidade provenientes diretamente do banco precisa ser implementado separadamente, caso ainda não exista.

## Swagger

Com a dependência springdoc UI instalada e as rotas liberadas, acesse:

```text
http://localhost:8085/swagger-ui/index.html
```

O Swagger documenta automaticamente os métodos dos controllers. Login e logout são processados por filtros e podem não aparecer na interface sem uma documentação adicional.

A ausência de `/auth/login` no Swagger não significa que a rota não exista. É possível utilizá-la diretamente pelo Bruno, sem criar outro controller ou adicionar documentação personalizada.

A indicação `Undocumented` significa que a documentação não descreve aquela resposta HTTP; não é a causa de um erro de segurança.

