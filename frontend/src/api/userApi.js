import axiosInstance from "./axiosInstance";

export const getUserById = async (id) => {
  const response = await axiosInstance.get(`/api/v1/users/${id}`);

  return response.data;
};
