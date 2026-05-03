import { apiFetch } from "./ApiClient";

export const checkoutOrder = async ({ cartId }) => {
  return apiFetch(
    "/api/v1/orders/checkout",
    {
      method: "POST",
      body: JSON.stringify({
        cartId,
      }),
    },
    true,
  );
};
