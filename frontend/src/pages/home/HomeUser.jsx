import { useEffect, useState } from "react";
import { replace, useNavigate } from "react-router-dom";
import "./styles.css";

export default function HomeUser() {
  return (
    <main className="page">
      <div className="container">
        <header className="header">
          <div>
            <p className="marca">
              Audit<span>Flow</span>
            </p>

            <span className="identificador">Centro de Auditoria</span>
          </div>

          <button
            className="botao-secundario"
            type="button"
            disabled = {""}
            onClick={""}
          >
            {"" ? "Acessando..." : "Cadastrar Apontamentos"}
          </button>

          <button
            className="botao-secundario"
            type="button"
            disabled={""}
            onClick={""}
          >
            {"" ? "Acessando..." : "Consultar Todos Apontamentos"}

          </button>

          <button
            className="botao-secundario"
            type="button"
            disabled={""}
            onClick={""}
          >
            {"" ? "Acessando..." : "Relatórios"}

          </button>
          <button
            className="botao-secundario"
            type="button"
            disabled={""}
            onClick={""}
          >
            {"" ? "Acessando..." : "Meus dados"}

          </button>
        </header>

        <footer className="footer">
          Gestão de auditorias com clareza e controle.
        </footer>
      </div>
    </main>
  );
}
