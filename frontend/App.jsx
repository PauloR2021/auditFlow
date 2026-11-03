import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import RotaProtegida from "./src/components/RotaProtegida.jsx";

import Login from "./src/pages/login/Login.jsx";
import PrimeiroAcesso from "./src/pages/login/PrimeiroAcesso.jsx";
import HomeAdmin from "./src/pages/home/HomeAdmin.jsx";
import CriarAdmin from "./src/pages/home/CriarAdmin.jsx";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route
          path="/primeiro-acesso"
          element={
            <RotaProtegida>
              <PrimeiroAcesso />
            </RotaProtegida>
          }
        />

        <Route
          path="/home-admin"
          element={
            <RotaProtegida acesso="ADMIN">
              <HomeAdmin />
            </RotaProtegida>
          }
        />

        <Route
          path="/home-criar-admin"
          element={
            <RotaProtegida acesso="ADMIN">
              <CriarAdmin />
            </RotaProtegida>
          }
        />
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
