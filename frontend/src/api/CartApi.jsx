import axiosInstance from "./axiosInstance";
import keycloak from "../keycloack";

const CART_ID_STORAGE_KEY = "3d5cefed-2835-471e-81b0-2acb6aed28ce";

const redirectToLogin = () => {
  const from = `${window.location.pathname}${window.location.search}`;
  window.location.assign(`/login?from=${encodeURIComponent(from)}`);
};

const getAuthConfig = async () => {
  if (!keycloak.authenticated) {
    redirectToLogin();
    return null;
  }

  await keycloak.updateToken(30);

  return {
    headers: {
      Authorization: `Bearer ${keycloak.token}`,
    },
  };
};

export const getStoredCartId = () => {
  return localStorage.getItem(CART_ID_STORAGE_KEY);
};

export const storeCartId = (cartId) => {
  if (cartId) {
    localStorage.setItem(CART_ID_STORAGE_KEY, cartId);
  }
};

export const clearStoredCartId = () => {
  localStorage.removeItem(CART_ID_STORAGE_KEY);
};

export const createCart = async (userId, currency = "TRY") => {
  const authConfig = await getAuthConfig();

  if (!authConfig) {
    return null;
  }

  const response = await axiosInstance.post(
    "/api/v1/carts",
    {
      userId,
      currency,
    },
    authConfig,
  );

  storeCartId(response.data?.cartId);

  return response.data;
};

export const ensureCart = async (userId, currency = "TRY") => {
  const storedCartId = getStoredCartId();

  if (storedCartId) {
    return storedCartId;
  }

  const cart = await createCart(userId, currency);

  return cart?.cartId;
};

export const getCartById = async (cartId) => {
  const authConfig = await getAuthConfig();

  if (!authConfig) {
    return null;
  }

  const response = await axiosInstance.get(
    `/api/v1/carts/${cartId}`,
    authConfig,
  );

  return response.data;
};

export const getCurrentCart = async (userId, currency = "TRY") => {
  const cartId = await ensureCart(userId, currency);

  return getCartById(cartId);
};

export const addProductToCart = async (
  userId,
  productId,
  quantity = 1,
  currency = "TRY",
) => {
  const authConfig = await getAuthConfig();

  if (!authConfig) {
    return null;
  }

  await ensureCart(userId, currency);

  const response = await axiosInstance.post(
    "/api/v1/carts/items",
    {
      userId,
      productId,
      quantity,
    },
    authConfig,
  );

  return response.data;
};

export const addToCart = async (productId, quantity = 1) => {
  const authConfig = await getAuthConfig();

  if (!authConfig) {
    return null;
  }

  const response = await axiosInstance.post(
    "/api/v1/carts/items",
    {
      productId,
      quantity,
    },
    authConfig,
  );

  return response.data;
};

export const updateCartItemQuantity = async (cartId, productId, quantity) => {
  const authConfig = await getAuthConfig();

  if (!authConfig) {
    return null;
  }

  const response = await axiosInstance.put(
    `/api/v1/carts/${cartId}/items/${productId}`,
    { quantity },
    authConfig,
  );

  return response.data;
};

export const removeCartItem = async (cartId, productId) => {
  const authConfig = await getAuthConfig();

  if (!authConfig) {
    return;
  }

  await axiosInstance.delete(
    `/api/v1/carts/${cartId}/items/${productId}`,
    authConfig,
  );
};
