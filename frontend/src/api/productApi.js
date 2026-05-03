import axiosInstance from "./axiosInstance";
import { apiFetch } from "./ApiClient";

export const getProducts = async ({
  page = 0,
  size = 5,
  sortBy = "id",
  direction = "asc",
} = {}) => {
  const response = await axiosInstance.get("/api/v1/products", {
    params: {
      page,
      size,
      sortBy,
      direction,
    },
  });

  return response.data;
};

export const getAllProducts = async () => {
  const response = await axiosInstance.get("/api/v1/products/all");

  return response.data;
};

export const getProductById = async (id) => {
  const response = await axiosInstance.get(`/api/v1/products/${id}`);

  return response.data;
};

export const createProduct = async (product) => {
  return apiFetch(
    "/api/v1/products",
    {
      method: "POST",
      body: JSON.stringify(product),
    },
    true,
  );
};

export const updateProduct = async (id, product) => {
  const response = await axiosInstance.put(`/api/v1/products/${id}`, product);

  return response.data;
};

export const deleteProduct = async (id) => {
  await axiosInstance.delete(`/api/v1/products/${id}`);
};

export const getApiErrorMessage = (error) => {
  const message = error?.response?.data?.message;

  if (message) {
    return message;
  }

  if (error?.response?.status === 404) {
    return "Ürün bulunamadı.";
  }

  if (error?.response?.status === 409) {
    return "Bu ürün zaten kayıtlı.";
  }

  if (error?.response?.status === 400) {
    return "Gönderilen ürün bilgileri geçersiz.";
  }

  return "Beklenmeyen bir hata oluştu. Lütfen tekrar deneyin.";
};
