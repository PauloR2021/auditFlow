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