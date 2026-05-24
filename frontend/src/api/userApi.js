import keycloak from "../keycloack";
import axiosInstance from "./axiosInstance";

export const getUserById = async (userId) => {
  if (!keycloak.authenticated) {
    return null;
  }

  await keycloak.updateToken(30);

  const response = await axiosInstance.get(`/api/v1/users/${userId}`, {
    headers: {
      Authorization: `Bearer ${keycloak.token}`,
    },
  });

  return response.data;
};
