import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import keycloak from "./keycloack";
import AccountPage from "./pages/AccountPage";
import CartPage from "./pages/CartPage";
import LoginPage from "./pages/LoginPage";
import PaymentPage from "./pages/PaymentPage";
import ProductDetailPage from "./pages/ProductDetailPage";
import ProductListPage from "./pages/ProductListPage";

function ProtectedRoute({ children }) {
  const location = useLocation();

  if (!keycloak.authenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  return children;
}

function App() {
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
    </Routes>
  );
}

export default App;
