const API_BASE = "/api";

let csrf = null;

export async function api(
  caminho,
  { method = "GET", body, headers = {} } = {}
) {

  const metodo = method.toUpperCase();
  const cabecalhos = new Headers(headers);

  const alteraDados = !["GET", "HEAD", "OPTIONS"].includes(metodo);

  if (alteraDados && csrf) {
    cabecalhos.set(csrf.headerName, csrf.token);
  }

  const formulario = body instanceof URLSearchParams;

  if (body !== undefined) {
    cabecalhos.set(
      "Content-Type",
      formulario
        ? "application/x-www-form-urlencoded"
        : "application/json"
    );
  }

  const resposta = await fetch(`${API_BASE}${caminho}`, {
    method: metodo,
    credentials: "include",
    headers: cabecalhos,
    body:
      body === undefined
        ? undefined
        : formulario
          ? body.toString()
          : JSON.stringify(body),
  });

  // O login e o logout podem retornar corpo vazio.
  const texto = await resposta.text();
  let dados = null;

  if (texto) {
    const tipo = resposta.headers.get("content-type") || "";

    if (tipo.includes("application/json")) {
      dados = JSON.parse(texto);
    } else if (resposta.ok) {
      throw new Error(
        "A API retornou um formato inesperado. Confira o proxy."
      );
    }
  }

  if (!resposta.ok) {
    const erro = new Error(
      dados?.mensagem || `Erro na requisição: ${resposta.status}`
    );

    erro.status = resposta.status;

    throw erro;
  }

  return dados;
}

export async function atualizarCsrf() {
  csrf = await api("/auth/csrf");
  return csrf;
}

export async function fazerLogin(usuario, senha) {
  await atualizarCsrf();

  await api("/auth/login", {
    method: "POST",
    body: new URLSearchParams({ usuario, senha }),
  });

  await atualizarCsrf();

  const resposta = await api("/auth/me");
  return resposta.dados;
}

export async function fazerLogout() {
  await atualizarCsrf();

  await api("/auth/logout", {
    method: "POST",
  });

  csrf = null;
}