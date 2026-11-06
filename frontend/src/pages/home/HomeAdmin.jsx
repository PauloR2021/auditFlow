import { useEffect, useState } from "react";
import { replace, useNavigate } from "react-router-dom";
import { fazerLogout, consultarMeusDados } from "../../service/api.js";
import { listarUsuarios, buscarUsuario } from "../../service/usuarioService.js";
import "./styles.css";

export default function HomeAdmin() {
  const navigate = useNavigate();

  const [usuarios, setUsuarios] = useState([]);
  const [busca, setBusca] = useState("");
  const [resultado, setResultado] = useState(null);
  const [carregando, setCarregando] = useState(true);
  const [buscando, setBuscando] = useState(false);
  const [saindo, setSaindo] = useState(false);
  const [erro, setErro] = useState("");

  const [telaUser, setTelaUser] = useState(false);
  const [telaAdmin, setTelaAdmin] = useState(false);
  const [meusDados, setMeusDados] = useState(false);

  useEffect(() => {
    let ativo = true;

    async function carregarUsuarios() {
      try {
        const resposta = await listarUsuarios();

        if (ativo) {
          setUsuarios(resposta.dados ?? []);
        }
      } catch (error) {
        if (ativo) {
          setErro(error.message || "Não foi possível carregar os usuários.");
        }
      } finally {
        if (ativo) {
          setCarregando(false);
        }
      }
    }

    carregarUsuarios();

    return () => {
      ativo = false;
    };
  }, []);

  async function pesquisar(evento) {
    evento.preventDefault();

    const login = busca.trim();

    if (!login || buscando) return;

    setBuscando(true);
    setErro("");
    setResultado(null);

    try {
      const resposta = await buscarUsuario(login);
      setResultado(resposta.dados);
    } catch (error) {
      setErro(error.message || "Não foi possível buscar o usuário.");
    } finally {
      setBuscando(false);
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
      setErro(error.message || "Não foi possível encerrar a sessão.");
    } finally {
      setSaindo(false);
    }
  }

  async function telaCadastroUser() {
    if (telaUser) return;

    setTelaUser(true);
    setErro("");

    try {
      navigate("/users/create-user", { replace: true });
    } catch (error) {
      setResultado("");
      setErro(
        error instanceof TypeError
          ? "Não foi possível conectar à API. Verifique sua conexão."
          : error.message || "Ocorreu um erro. Tente novamente.",
      );
    } finally {
      setTelaUser(false);
    }
  }

  async function telaCadastrarAdmin() {
    if (telaAdmin) return;

    setTelaAdmin(true);
    setErro("");

    try {
      navigate("/users/create-admin", { replace: true });
    } catch (error) {
      setResultado("");
      setErro(
        error instanceof TypeError
          ? "Não foi possível conectar à API. Verifique sua conexão."
          : error.message || "Ocorreu um erro. Tente novamente.",
      );
    } finally {
      setTelaAdmin(false);
    }
  }

  async function telaMeusDados() {
    if (meusDados) return;

    setMeusDados(true);
    setErro("");

    try {
      navigate("/users/me", {replace: true})
    } catch (error) {
      setResultado("");
      setErro(
        error instanceof TypeError
          ? "Não foi possível conectar à API. Verifique sua conexão."
          : error.message || "Ocorreu um erro. Tente novamente.",
      );
    } finally {
      setMeusDados(false);
    }
  }

  function formatarData(valor) {
    if (!valor) return "—";

    const data = new Date(valor);

    return Number.isNaN(data.getTime()) ? "—" : data.toLocaleString("pt-BR");
  }

  const administradores = usuarios.filter(
    (usuario) => usuario.acesso === "ADMIN",
  ).length;

  const usuariosComuns = usuarios.filter(
    (usuario) => usuario.acesso === "USER",
  ).length;

  const listaExibida = resultado ? [resultado] : usuarios;

  return (
    <main className="page">
      <div className="container">
        <header className="header">
          <div>
            <p className="marca">
              Audit<span>Flow</span>
            </p>

            <span className="identificacao">Área administrativa</span>
          </div>

          <button
            className="botao-secundario"
            type="button"
            disabled={telaUser}
            onClick={telaCadastroUser}
          >
            {telaAdmin ? "Acessando..." : "Cadastrar usuário"}
          </button>

          <button
            className="botao-secundario"
            type="button"
            disabled={telaAdmin}
            onClick={telaCadastrarAdmin}
          >
            {telaAdmin ? "Acessando..." : "Cadastrar administrador"}
          </button>

          <button
            className="botao-secundario"
            type="button"
            disabled={meusDados}
            onClick={telaMeusDados}
          >
            {telaAdmin ? "Acessando..." : "Meus dados"}
          </button>

          <button
            className="botao-secundario"
            type="button"
            disabled={saindo}
            onClick={sair}
          >
            {saindo ? "Saindo..." : "Sair da conta"}
          </button>
        </header>

        <section className="introducao">
          <p className="eyebrow">PAINEL DE CONTROLE</p>
          <h1>Visão geral</h1>
          <p>Consulte os usuários e acompanhe os acessos ao sistema.</p>
        </section>

        {erro && (
          <p className="erro" role="alert">
            {erro}
          </p>
        )}

        <section className="indicadores" aria-label="Indicadores">
          <article className="indicador">
            <span>Total de usuários</span>
            <strong>{carregando ? "—" : usuarios.length}</strong>
            <small>Contas cadastradas</small>
          </article>

          <article className="indicador">
            <span>Administradores</span>
            <strong>{carregando ? "—" : administradores}</strong>
            <small>Perfil ADMIN</small>
          </article>

          <article className="indicador">
            <span>Usuários comuns</span>
            <strong>{carregando ? "—" : usuariosComuns}</strong>
            <small>Perfil USER</small>
          </article>
        </section>

        <section className="painel">
          <div className="titulo-painel">
            <div>
              <h2>Usuários cadastrados</h2>
              <p>Busque uma conta pelo login exato.</p>
            </div>
          </div>

          <form className="busca" onSubmit={pesquisar}>
            <label className="label-busca" htmlFor="busca-usuario">
              Login do usuário
            </label>

            <div className="busca-controles">
              <input
                id="busca-usuario"
                type="search"
                placeholder="Ex.: admin_usuario_1"
                value={busca}
                onChange={(evento) => setBusca(evento.target.value)}
                disabled={buscando || carregando}
                maxLength={20}
                required
              />

              <button
                className="botao"
                type="submit"
                disabled={buscando || carregando}
              >
                {buscando ? "Buscando..." : "Buscar"}
              </button>

              {resultado && (
                <button
                  className="botao-secundario"
                  type="button"
                  onClick={() => {
                    setBusca("");
                    setResultado(null);
                    setErro("");
                  }}
                >
                  Mostrar todos
                </button>
              )}
            </div>
          </form>

          {carregando ? (
            <p className="estado" role="status">
              Carregando usuários...
            </p>
          ) : listaExibida.length === 0 ? (
            <p className="estado">Nenhum usuário para exibir.</p>
          ) : (
            <div className="tabela-container">
              <table className="tabela">
                <caption className="somente-leitor">
                  Lista de usuários do sistema
                </caption>

                <thead>
                  <tr>
                    <th scope="col">Nome</th>
                    <th scope="col">Usuário</th>
                    <th scope="col">Perfil</th>
                    <th scope="col">Criado em</th>
                  </tr>
                </thead>

                <tbody>
                  {listaExibida.map((usuario) => (
                    <tr key={usuario.id}>
                      <td>{usuario.nome}</td>
                      <td>{usuario.usuario}</td>
                      <td>
                        <span
                          className={
                            usuario.acesso === "ADMIN"
                              ? "perfil perfil-administrador"
                              : "perfil"
                          }
                        >
                          {usuario.acesso}
                        </span>
                      </td>
                      <td>{formatarData(usuario.dtCriacao)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
        <footer className="footer">
          Gestão de auditorias com clareza e controle.
        </footer>
      </div>
    </main>
  );
}
