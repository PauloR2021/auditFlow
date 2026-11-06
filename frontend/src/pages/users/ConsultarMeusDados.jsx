import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  atualizarCsrf,
  fazerLogout,
  consultarMeusDados,
} from "../../service/api.js";
import "./styles.css";

export default function ConsultarMeusDados() {
  const navigate = useNavigate();

  const [meusDados, setMeusDados] = useState([]);

  const [saindo, setSaindo] = useState(false);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);
  const [telaHome, setTelaHome] = useState(false);

  useEffect(() => {
    let ativo = true;

    async function carregarMeusDados() {
      try {
        const resposta = await consultarMeusDados();
        if (ativo) {
          setMeusDados(resposta.dados ?? []);
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
    carregarMeusDados();

    return () => {
      ativo = false;
    };
  }, []);

  async function sair() {
    if (saindo) return;

    setSaindo(true);
    setErro("");

    try {
      await fazerLogout();
      navigate("/login", { replace: true });
    } catch (error) {
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
      setErro(
        error instanceof TypeError
          ? "Não foi possível conectar à API. Verifique sua conexão."
          : error.message || "Ocorreu um erro. Tente novamente.",
      );
    } finally {
      setTelaHome(false);
    }
  }

  return (
    <main className="admin-page">
      <div>
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
          <p className="admin-eyebrow">MEUS DADOS</p>
          <p>Valide seus dados</p>
        </section>
        {erro && <p role="alert">{erro}</p>}
        {carregando ? (
          <p role="status">Carregando seus dados...</p>
        ) : meusDados ? (
          <div className="container-card">
            <div className="card">
              <h2>Informações da conta</h2>
              <p>
                Nome: <span>{meusDados.nome}</span>
              </p>
              <p>
                Usuário: <span>{meusDados.usuario}</span>
              </p>
              <p>
                Data de Criação:  
                <span>
                   {meusDados.dtCriacao
                    ? new Date(meusDados.dtCriacao).toLocaleString("pt-BR")
                    : "Não informada"}
                </span>
              </p>
              <p>
                Acesso: <span>{meusDados.acesso}</span>
              </p>
            </div>
          </div>
        ) : (
          !erro && <p>Nenhum dado encontrado.</p>
        )}

        <footer className="admin-footer">
          Gestão de auditorias com clareza e controle.
        </footer>
      </div>
    </main>
  );
}
