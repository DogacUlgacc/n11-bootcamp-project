import keycloak from "../keycloack";

const API_BASE_URL = "http://localhost:8888";

const redirectToLogin = () => {
  const from = `${window.location.pathname}${window.location.search}`;
  window.location.assign(`/login?from=${encodeURIComponent(from)}`);
};

export async function apiFetch(path, options = {}, requireAuth = false) {
  if (requireAuth && !keycloak.authenticated) {
    redirectToLogin();
    return;
  }

  if (requireAuth) {
    await keycloak.updateToken(30);
  }

  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {}),
  };

  if (requireAuth && keycloak.token) {
    headers.Authorization = `Bearer ${keycloak.token}`;
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    throw new Error(`API error: ${response.status}`);
  }

  return response.json();
}
