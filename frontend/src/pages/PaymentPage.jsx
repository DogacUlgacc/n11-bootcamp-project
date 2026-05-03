import { useEffect, useState } from "react";
import { Link, useLocation, useParams } from "react-router-dom";
import { clearStoredCartId } from "../api/cartApi";
import { getPaymentById, payWithCard } from "../api/paymentApi";
import { getApiErrorMessage } from "../api/productApi";

const emptyCardForm = {
  cardHolderName: "",
  cardNumber: "",
  expireMonth: "",
  expireYear: "",
  cvc: "",
};

function PaymentPage() {
  const { paymentId } = useParams();
  const location = useLocation();
  const [payment, setPayment] = useState(location.state?.payment || null);
  const [cardForm, setCardForm] = useState(emptyCardForm);
  const [loading, setLoading] = useState(!location.state?.payment);
  const [paying, setPaying] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  useEffect(() => {
    if (payment) {
      return;
    }

    let ignore = false;

    const run = async () => {
      try {
        setLoading(true);
        setError("");

        const data = await getPaymentById(paymentId);

        if (!ignore) {
          setPayment(data);
        }
      } catch (err) {
        if (!ignore) {
          setError(getApiErrorMessage(err));
        }
      } finally {
        if (!ignore) {
          setLoading(false);
        }
      }
    };

    run();

    return () => {
      ignore = true;
    };
  }, [payment, paymentId]);

  const formatMoney = (amount) => {
    return new Intl.NumberFormat("tr-TR", {
      style: "currency",
      currency: payment?.currency || "TRY",
    }).format(amount || 0);
  };

  const handleCardFieldChange = (event) => {
    const { name, value } = event.target;

    setCardForm((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const validateCardForm = () => {
    if (!cardForm.cardHolderName.trim()) {
      return "Kart üzerindeki isim zorunludur.";
    }

    if (!cardForm.cardNumber.trim()) {
      return "Kart numarası zorunludur.";
    }

    if (!cardForm.expireMonth.trim()) {
      return "Son kullanma ayı zorunludur.";
    }

    if (!cardForm.expireYear.trim()) {
      return "Son kullanma yılı zorunludur.";
    }

    if (!cardForm.cvc.trim()) {
      return "CVC zorunludur.";
    }

    return "";
  };

  const handlePayWithCard = async (event) => {
    event.preventDefault();

    const validationMessage = validateCardForm();

    if (validationMessage) {
      setSuccessMessage("");
      setError(validationMessage);
      return;
    }

    try {
      setPaying(true);
      setError("");
      setSuccessMessage("");

      const nextPayment = await payWithCard(paymentId, {
        cardHolderName: cardForm.cardHolderName.trim(),
        cardNumber: cardForm.cardNumber.trim(),
        expireMonth: cardForm.expireMonth.trim(),
        expireYear: cardForm.expireYear.trim(),
        cvc: cardForm.cvc.trim(),
      });

      setPayment(nextPayment);

      if (nextPayment.status === "COMPLETED") {
        clearStoredCartId();
        setSuccessMessage("Ödeme başarıyla tamamlandı.");
        return;
      }

      if (nextPayment.status === "FAILED") {
        setError("Ödeme başarısız oldu. Kart bilgilerini kontrol edin.");
        return;
      }

      setError(`Ödeme tamamlanamadı. Güncel durum: ${nextPayment.status}`);
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setPaying(false);
    }
  };

  const isCompleted = payment?.status === "COMPLETED";
  const isFailed = payment?.status === "FAILED";

  return (
    <div className="app">
      <header className="site-header">
        <div className="header-top detail-header">
          <Link className="logo" to="/" aria-label="n11 ana sayfa">
            n11
          </Link>
          <Link className="back-link" to="/cart">
            Sepete dön
          </Link>
        </div>
      </header>

      <main className="container">
        <section className="payment-page">
          <div className="section-heading">
            <div>
              <span className="section-kicker">Sandbox ödeme</span>
              <h1>Ödeme Sayfası</h1>
            </div>
          </div>

          {loading && (
            <p className="info-message">Ödeme bilgisi yükleniyor...</p>
          )}
          {error && <p className="error-message">{error}</p>}
          {successMessage && (
            <p className="success-message">{successMessage}</p>
          )}

          {!loading && payment && (
            <div className="payment-card">
              <div className="payment-status">
                <span>Durum</span>
                <strong>{payment.status}</strong>
              </div>

              <dl className="detail-facts">
                <div>
                  <dt>Payment ID</dt>
                  <dd>{payment.id}</dd>
                </div>
                <div>
                  <dt>Order ID</dt>
                  <dd>{payment.orderId}</dd>
                </div>
                <div>
                  <dt>Tutar</dt>
                  <dd>{formatMoney(payment.amount)}</dd>
                </div>
                <div>
                  <dt>Provider</dt>
                  <dd>{payment.paymentProvider || "IYZICO"}</dd>
                </div>
              </dl>

              {isFailed && (
                <p className="info-message">
                  Bu ödeme kaydı başarısız durumda. Tekrar denemek için sepete
                  dönüp yeni bir checkout başlatın.
                </p>
              )}

              <form className="payment-form" onSubmit={handlePayWithCard}>
                <label>
                  Kart üzerindeki isim
                  <input
                    name="cardHolderName"
                    value={cardForm.cardHolderName}
                    onChange={handleCardFieldChange}
                    placeholder="John Doe"
                    disabled={isCompleted || isFailed}
                  />
                </label>

                <label>
                  Kart numarası
                  <input
                    name="cardNumber"
                    value={cardForm.cardNumber}
                    onChange={handleCardFieldChange}
                    placeholder="5526080000000006"
                    inputMode="numeric"
                    disabled={isCompleted || isFailed}
                  />
                </label>

                <div className="payment-form-row">
                  <label>
                    Ay
                    <input
                      name="expireMonth"
                      value={cardForm.expireMonth}
                      onChange={handleCardFieldChange}
                      placeholder="12"
                      inputMode="numeric"
                      disabled={isCompleted || isFailed}
                    />
                  </label>

                  <label>
                    Yıl
                    <input
                      name="expireYear"
                      value={cardForm.expireYear}
                      onChange={handleCardFieldChange}
                      placeholder="2030"
                      inputMode="numeric"
                      disabled={isCompleted || isFailed}
                    />
                  </label>

                  <label>
                    CVC
                    <input
                      name="cvc"
                      value={cardForm.cvc}
                      onChange={handleCardFieldChange}
                      placeholder="123"
                      inputMode="numeric"
                      disabled={isCompleted || isFailed}
                    />
                  </label>
                </div>

                <button
                  type="submit"
                  disabled={paying || isCompleted || isFailed}
                >
                  {isCompleted
                    ? "Ödeme Tamamlandı"
                    : isFailed
                      ? "Ödeme Başarısız"
                      : paying
                        ? "Ödeme yapılıyor..."
                        : "Ödemeyi Yap"}
                </button>
              </form>
            </div>
          )}
        </section>
      </main>
    </div>
  );
}

export default PaymentPage;
