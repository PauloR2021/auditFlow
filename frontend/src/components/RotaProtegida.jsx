import { useEffect, useState } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { api } from "../service/api.js";

export default function RotaProtegida({ children, acesso }) {
  const location = useLocation();

  const [estado, setEstado] = useState({
    situacao: "carregando",
    usuario: null,
  });

  const [tentativa, setTentativa] = useState(0);

  useEffect(() => {
    let ativo = true;

    setEstado({
      situacao: "carregando",
      usuario: null,
    });

    async function verificarSessao() {
      try {
        const resposta = await api("/auth/me");
        const usuario = resposta?.dados;

        if (!usuario?.acesso) {
          throw new Error("Resposta de sessão inválida.");
        }

        if (ativo) {
          setEstado({
            situacao: "autenticado",
            usuario,
          });
        }
      } catch (erro) {
        if (!ativo) return;

        setEstado({
          situacao:
            erro.status === 401
              ? "sem-login"
              : erro.status === 403
                ? "sem-permissao"
                : "indisponivel",
          usuario: null,
        });
      }
    }

    verificarSessao();

    return () => {
      ativo = false;
    };
  }, [tentativa, location.pathname]);

  if (estado.situacao === "carregando") {
    return (
      <main className="login-page">
        <p role="status">Verificando sessão...</p>
      </main>
    );
  }

  if (estado.situacao === "sem-login") {
    return <Navigate to="/login" replace />;
  }

  if (estado.situacao === "indisponivel") {
    return (
      <main className="login-page">
        <section className="login-card">
          <h1>Não foi possível verificar sua sessão</h1>

          <p>A API pode estar indisponível. Tente novamente.</p>

          <button
            className="login-entrar"
            type="button"
            onClick={() => setTentativa((valor) => valor + 1)}
          >
            Tentar novamente
          </button>
        </section>
      </main>
    );
  }

  if (
    estado.situacao === "sem-permissao" ||
    (acesso && estado.usuario?.acesso !== acesso)
  ) {
    return (
      <main className="login-page">
        <section className="login-card">
          <h1>Acesso negado</h1>
          <p>Seu perfil não tem permissão para acessar esta página.</p>
        </section>
      </main>
    );
  }

  return children;
}