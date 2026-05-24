import keycloak from "../keycloack";
import axiosInstance from "./axiosInstance";

const CART_ID_STORAGE_KEY = "cartId";

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

export const getCartByUserId = async (userId) => {
  const authConfig = await getAuthConfig();

  if (!authConfig) {
    return null;
  }

  const response = await axiosInstance.get(
    `/api/v1/carts/user/${userId}`,
    authConfig,
  );

  return response.data;
};

export const storeCartId = (cartId) => {
  if (cartId) {
    localStorage.setItem(CART_ID_STORAGE_KEY, cartId);
  }
};

export const clearStoredCartId = () => {
  localStorage.removeItem(CART_ID_STORAGE_KEY);
};

export const ensureCart = async (userId) => {
  const cart = await getCartByUserId(userId);
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

export const getCurrentCart = async (userId) => {
  return getCartByUserId(userId);
};

export const addProductToCart = async (userId, productId, quantity = 1) => {
  const authConfig = await getAuthConfig();

  if (!authConfig) {
    return null;
  }

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
