import { api } from "./api.js";

export async function listarUsuarios() {
  return api("/usuario/all-user");
}

export function buscarUsuario(usuario) {
  const parametros = new URLSearchParams({
    usuario: usuario.trim(),
  });

  return api(`/usuario/find-user?${parametros}`);
}

export function cadastrarUsuarioAdmin(dados) {
  return api("/usuario/create-admin", {
    method: "POST",
    body: dados,
  });

}

export function cadastrarUsuarioUser(dados) {
  return api("/usuario/create-user", {
    method: "POST",
    body: dados,
  });

}

