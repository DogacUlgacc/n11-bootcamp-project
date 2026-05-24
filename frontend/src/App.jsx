import axios from "axios";
import { useEffect } from "react";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import keycloak from "./keycloack";
import AccountPage from "./pages/AccountPage";
import CartPage from "./pages/CartPage";
import LoginPage from "./pages/LoginPage";
import PaymentPage from "./pages/PaymentPage";
import ProductDetailPage from "./pages/ProductDetailPage";
import ProductListPage from "./pages/ProductListPage";
import RegisterPage from "./pages/RegisterPage";
function ProtectedRoute({ children }) {
  const location = useLocation();

  // Route korumasi burada yapilir: kullanici login degilse gitmek istedigi
  // sayfa bilgisiyle birlikte LoginPage'e yonlendirilir.
  if (!keycloak.authenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  return children;
}

function App() {
  useEffect(() => {
    // App ilk acildiginda Keycloak zaten main.jsx tarafinda initialize edildi.
    // Burada token ve backend userId bilgisi browser tarafinda saklanarak
    // sepet/hesap gibi sayfalarin ayni oturum bilgisini okuyabilmesi saglanir.
    if (keycloak.authenticated) {
      localStorage.setItem("token", keycloak.token);

      if (keycloak.refreshToken) {
        localStorage.setItem("refreshToken", keycloak.refreshToken);
      }
    }

    const loadCurrentUser = async () => {
      try {
        const externalId = keycloak.tokenParsed?.sub;
        const token = keycloak.token;

        if (!externalId || !token) {
          return;
        }

        const response = await axios.get(
          `http://localhost:8888/api/v1/users/by-external-id/${externalId}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          },
        );

        console.log("Current user:", response.data);

        localStorage.setItem("userId", response.data.id);
      } catch (error) {
        console.error("Current user fetch error:", error);
      }
    };

    if (keycloak.authenticated) {
      loadCurrentUser();
    }
  }, []);

  return (
    <Routes>
      <Route path="/" element={<ProductListPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/products/:id" element={<ProductDetailPage />} />
      <Route
        path="/cart"
        element={
          <ProtectedRoute>
            <CartPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/account"
        element={
          <ProtectedRoute>
            <AccountPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/payment/:paymentId"
        element={
          <ProtectedRoute>
            <PaymentPage />
          </ProtectedRoute>
        }
      />
      <Route path="/register" element={<RegisterPage />} />
    </Routes>
  );
}

export default App;
