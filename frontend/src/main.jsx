import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import App from "./App.jsx";
import "./index.css";
import keycloak from "./keycloack";

keycloak
  .init({
    onLoad: "check-sso",
    pkceMethod: "S256",
  })
  .then((authenticated) => {
    console.log("Keycloak initialized:", authenticated);
    console.log("Keycloak sub:", keycloak.tokenParsed?.sub);

    ReactDOM.createRoot(document.getElementById("root")).render(
      <React.StrictMode>
        <BrowserRouter>
          <App />
        </BrowserRouter>
      </React.StrictMode>,
    );
  })
  .catch((err) => {
    console.error("Keycloak init error:", err);
  });
