import { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  atualizarCsrf,
  primeiroAcesso,
  fazerLogout,
} from "../../service/api.js";
import "./Login.css";

export default function PrimeiroAcesso() {
  const navigate = useNavigate();

  const [senha, setSenha] = useState("");
  const [confirmarSenha, setConfirmarSenha] = useState("");
  const [mostrarSenha, setMostrarSenha] = useState(false);
  const [mostrarSenhaConfirmacao, setMostrarSenhaConfirmacao] = useState(false);

  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState("");
  const [sucesso, setSucesso] = useState("");

  const [telaLogin, setTelaLogin] = useState(false);

  async function trocarSenha(evento) {
    evento.preventDefault();

    if (carregando) return;

    setErro("");
    setSucesso("");

    if (!senha || !confirmarSenha) {
      setErro("Preencha todos os campos.");
      return;
    }

    if (senha !== confirmarSenha) {
      setErro("As senhas não coincidem.");
      return;
    }

    setCarregando(true);

    try {
      await atualizarCsrf();

      await primeiroAcesso({
        senha,
        confirmarSenha,
      });

      setSenha("");
      setConfirmarSenha("");

      setSucesso("Senha alterada com sucesso!");

      // Encerra a sessão antes de voltar ao login.
      await fazerLogout();

      navigate("/login", { replace: true });

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

          <h1 id="login-titulo">Bem-vindo</h1>

          <p className="login-descricao">
            O primeiro acesso requer a criação de uma senha para sua conta. Por
            favor, escolha uma senha segura e confirme-a abaixo.
          </p>
        </header>

        <form className="login-form" onSubmit={trocarSenha} aria-busy={carregando}>
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

          <div className="login-campo">
            <label htmlFor="senha">Confirmar Senha</label>

            <div className="login-senha">
              <input
                id="confirmarSenha"
                name="confirmarSenha"
                type={mostrarSenhaConfirmacao ? "text" : "password"}
                placeholder="Digite sua senha"
                autoComplete="current-password"
                required
                disabled={carregando}
                value={confirmarSenha}
                onChange={(evento) => setConfirmarSenha(evento.target.value)}
              />

              <button
                className="login-mostrar-senha"
                type="button"
                disabled={carregando}
                aria-controls="senha"
                aria-label={
                  mostrarSenhaConfirmacao ? "Ocultar senha" : "Mostrar senha"
                }
                aria-pressed={mostrarSenhaConfirmacao}
                onClick={() =>
                  setMostrarSenhaConfirmacao(!mostrarSenhaConfirmacao)
                }
              >
                {mostrarSenhaConfirmacao ? "Ocultar" : "Mostrar"}
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
                Trocando senha...
              </>
            ) : (
              <>
                Trocar Senha
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
