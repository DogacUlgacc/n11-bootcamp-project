import { useEffect } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import keycloak from "../keycloack";

const getRedirectPath = (location) => {
  const params = new URLSearchParams(location.search);
  const queryFrom = params.get("from");
  const stateFrom = location.state?.from;

  if (queryFrom?.startsWith("/")) {
    return queryFrom;
  }

  if (typeof stateFrom === "string" && stateFrom.startsWith("/")) {
    return stateFrom;
  }

  if (stateFrom?.pathname) {
    return `${stateFrom.pathname}${stateFrom.search || ""}`;
  }

  return "/";
};

function LoginPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const redirectPath = getRedirectPath(location);

  useEffect(() => {
    if (keycloak.authenticated) {
      navigate("/", { replace: true });
    }
  }, [navigate]);

  const handleLogin = async () => {
    await keycloak.login({
      redirectUri: `${window.location.origin}${redirectPath}`,
    });
  };

  return (
    <div className="login-page">
      <header className="login-header">
        <Link className="logo login-logo" to="/" aria-label="n11 ana sayfa">
          n11
        </Link>
        <span>Clone</span>
      </header>

      <main className="login-shell">
        <section className="login-panel" aria-labelledby="login-title">
          <span className="section-kicker">Güvenli alışveriş</span>
          <h1 id="login-title">Giriş Yap</h1>
          <p>
            Sepetinize devam etmek ve siparişlerinizi görüntülemek için giriş
            yapın.
          </p>

          <button className="login-primary-button" type="button" onClick={handleLogin}>
            Keycloak ile Giriş Yap
          </button>

          <small>Güvenli giriş altyapısı Keycloak ile sağlanır.</small>
        </section>
      </main>
    </div>
  );
}

export default LoginPage;
