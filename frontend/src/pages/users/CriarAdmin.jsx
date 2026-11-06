import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { atualizarCsrf } from "../../service/api.js";
import { fazerLogout } from "../../service/api.js";
import { cadastrarUsuarioAdmin } from "../../service/usuarioService.js";
import "./styles.css";

export default function CriarAdmin() {
  const navigate = useNavigate();

  const [nome, setNome] = useState("");
  const [usuario, setUsuario] = useState("");

  const [carregando, setCarregando] = useState(false);
  const [saindo, setSaindo] = useState(false);
  const [erro, setErro] = useState("");
  const [sucesso, setSucesso] = useState("");

  const [telaHome, setTelaHome] = useState(false);

  async function cadastrar(evento) {
    evento.preventDefault();

    if (carregando) return;

    setErro("");
    setSucesso("");

    if (!nome.trim() || !usuario.trim()) {
      setErro("Preencha todos os campos.");
      return;
    }

    setCarregando(true);

    try {
      await atualizarCsrf();

      const resposta = await cadastrarUsuarioAdmin({
        nome: nome.trim(),
        usuario: usuario.trim(),
      });

      limparFormulario();

      setSucesso(resposta.mensagem || "Administrador cadastrado com sucesso!");
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

  async function sair() {
    if (saindo) return;

    setSaindo(true);
    setErro("");

    try {
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
      setSaindo(false);
    }
  }

  async function voltarHome() {
    if (telaHome) return;

    setTelaHome(true);
    setErro("");

    try {
      navigate("/home-admin", { replace: true });
    } catch (error) {
      setSucesso("");
      setErro(
        error instanceof TypeError
          ? "Não foi possível conectar à API. Verifique sua conexão."
          : error.message || "Ocorreu um erro. Tente novamente.",
      );
    } finally {
      setTelaHome(false);
    }
  }

  function limparFormulario() {
    setNome("");
    setUsuario("");
    setErro("");
    setSucesso("");
  }

  return (
    <main className="admin-page">
      <div className="admin-container">
        <header className="admin-header">
          <div>
            <p className="admin-marca">
              Audit<span>Flow</span>
            </p>
            <span className="admin-identificacao">Área administrativa</span>
          </div>

          <button
            className="admin-botao-secundario"
            type="button"
            disabled={telaHome}
            onClick={voltarHome}
          >
            {telaHome ? "Voltando..." : "Home"}
          </button>

          <button
            className="admin-botao-secundario"
            type="button"
            disabled={saindo}
            onClick={sair}
          >
            {saindo ? "Saindo..." : "Sair da conta"}
          </button>
        </header>

        <section className="admin-introducao">
          <p className="admin-eyebrow">PAINEL DE CADASTRO DE ADMINISTRADORES</p>
          <p>Cadastre novos administradores para gerenciar o sistema.</p>
        </section>

        <form
          className="admin-formulario"
          onSubmit={cadastrar}
          aria-busy={carregando}
        >
          <div className="admin-formulario-campo">
            <label htmlFor="nome">Nome</label>
            <input
              type="text"
              id="nome"
              name="nome"
              placeholder="Digite o nome do administrador"
              autoComplete="name"
              autoCapitalize="none"
              spellCheck={false}
              maxLength={100}
              required
              value={nome}
              onChange={(evento) => setNome(evento.target.value)}
            />
          </div>
          <div className="admin-formulario-campo">
            <label htmlFor="usuario">Usuário</label>
            <input
              type="text"
              id="usuario"
              name="usuario"
              placeholder="Digite o nome de usuário"
              autoComplete="username"
              autoCapitalize="none"
              spellCheck={false}
              maxLength={100}
              required
              value={usuario}
              onChange={(evento) => setUsuario(evento.target.value)}
            />
          </div>

          {erro && (
            <p className="admin-aviso admin-erro" role="alert">
              {erro}
            </p>
          )}

          {sucesso && (
            <p className="admin-aviso admin-sucesso" role="status">
              {sucesso}
            </p>
          )}

          <button
            className="admin-cadastrar"
            type="submit"
            disabled={carregando}
          >
            {carregando ? (
              <>
                <span className="admin-spinner" aria-hidden="true" />
                Cadastrando...
              </>
            ) : (
              <>
                Cadastrar
                <span aria-hidden="true">→</span>
              </>
            )}
          </button>
        </form>
        <footer className="admin-footer">
          Gestão de auditorias com clareza e controle.
        </footer>
      </div>
    </main>
  );
}
