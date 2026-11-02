import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { fazerLogin } from "../../service/api.js";
import "./Login.css";

export default function Login({ onLogin }) {
  const navigate = useNavigate(); // Hook do React Router para navegação programática
  const [usuario, setUsuario] = useState(""); // Estado para armazenar o valor do campo de usuário
  const [senha, setSenha] = useState(""); // Estado para armazenar o valor do campo de senha
  const [mostrarSenha, setMostrarSenha] = useState(false); // Estado para controlar a visibilidade da senha
  const [carregando, setCarregando] = useState(false); // Estado para indicar se o login está em andamento
  const [erro, setErro] = useState(""); // Estado para armazenar mensagens de erro
  const [sucesso, setSucesso] = useState(""); // Estado para armazenar mensagens de sucesso

  // Função para lidar com o envio do formulário de login

  async function entrar(evento) {
    evento.preventDefault();

    if (carregando) return;

    setErro("");
    setSucesso("");

    if (!usuario.trim() || !senha) {
      setErro("Informe o usuário e a senha.");
      return;
    }

    setCarregando(true);

    try {
      const usuarioLogado = await fazerLogin(usuario.trim(), senha);

      setSenha("");
      setSucesso("Login realizado com sucesso!");

      onLogin?.({ usuario: usuarioLogado });

      if (usuarioLogado?.acesso === "ADMIN") {
        navigate("/home-admin", { replace: true });
      }
    } catch (error) {
      setSucesso("");

      setErro(
        error instanceof TypeError
          ? "Não foi possível conectar à API. Verifique sua conexão."
          : error.message || "Ocorreu um erro. Tente novamente.",
      );
    } finally {
      setCarregando(false);
    }
  }

  return (
    <main className="login-page">
      <section className="login-card" aria-labelledby="login-titulo">
        <header className="login-header">
          <div className="login-logo" aria-hidden="true">
            AF
          </div>

          <p className="login-marca">
            Audit<span>Flow</span>
          </p>

          <h1 id="login-titulo">Bem-vindo de volta</h1>

          <p className="login-descricao">
            Acesse sua conta para acompanhar auditorias e planos de ação.
          </p>
        </header>

        <form className="login-form" onSubmit={entrar} aria-busy={carregando}>
          <div className="login-campo">
            <label htmlFor="usuario">Usuário</label>

            <input
              id="usuario"
              name="usuario"
              type="text"
              placeholder="Digite seu usuário"
              autoComplete="username"
              autoCapitalize="none"
              spellCheck={false}
              maxLength={20}
              required
              disabled={carregando}
              value={usuario}
              onChange={(evento) => setUsuario(evento.target.value)}
            />
          </div>

          <div className="login-campo">
            <label htmlFor="senha">Senha</label>

            <div className="login-senha">
              <input
                id="senha"
                name="senha"
                type={mostrarSenha ? "text" : "password"}
                placeholder="Digite sua senha"
                autoComplete="current-password"
                required
                disabled={carregando}
                value={senha}
                onChange={(evento) => setSenha(evento.target.value)}
              />

              <button
                className="login-mostrar-senha"
                type="button"
                disabled={carregando}
                aria-controls="senha"
                aria-label={mostrarSenha ? "Ocultar senha" : "Mostrar senha"}
                aria-pressed={mostrarSenha}
                onClick={() => setMostrarSenha(!mostrarSenha)}
              >
                {mostrarSenha ? "Ocultar" : "Mostrar"}
              </button>
            </div>
          </div>

          {erro && (
            <p className="login-aviso login-erro" role="alert">
              {erro}
            </p>
          )}

          {sucesso && (
            <p className="login-aviso login-sucesso" role="status">
              {sucesso}
            </p>
          )}

          <button className="login-entrar" type="submit" disabled={carregando}>
            {carregando ? (
              <>
                <span className="login-spinner" aria-hidden="true" />
                Entrando...
              </>
            ) : (
              <>
                Entrar
                <span aria-hidden="true">→</span>
              </>
            )}
          </button>
        </form>

        <footer className="login-footer">
          Gestão de auditorias com clareza e controle.
        </footer>
      </section>
    </main>
  );
}
