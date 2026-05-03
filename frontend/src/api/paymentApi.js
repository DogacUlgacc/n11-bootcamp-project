import { apiFetch } from "./ApiClient";

export const createPayment = async ({ orderId, userId, amount, currency }) => {
  return apiFetch(
    "/api/v1/payments",
    {
      method: "POST",
      body: JSON.stringify({
        orderId,
        userId,
        amount,
        currency,
      }),
    },
    true,
  );
};

export const getPayments = async () => {
  return apiFetch("/api/v1/payments", {}, true);
};

export const getPaymentById = async (paymentId) => {
  return apiFetch(`/api/v1/payments/${paymentId}`, {}, true);
};

export const waitForPaymentByOrderId = async (
  orderId,
  { retries = 10, delayMs = 500 } = {},
) => {
  for (let attempt = 0; attempt < retries; attempt += 1) {
    const payments = await getPayments();
    const payment = payments.find((item) => item.orderId === orderId);

    if (payment) {
      return payment;
    }

    await new Promise((resolve) => {
      setTimeout(resolve, delayMs);
    });
  }

  throw new Error("Ödeme kaydı henüz oluşturulmadı. Lütfen tekrar deneyin.");
};

export const completePayment = async (paymentId) => {
  return apiFetch(
    `/api/v1/payments/${paymentId}/complete`,
    {
      method: "POST",
    },
    true,
  );
};

export const payWithCard = async (paymentId, card) => {
  return apiFetch(
    `/api/v1/payments/${paymentId}/pay`,
    {
      method: "POST",
      body: JSON.stringify(card),
    },
    true,
  );
};
