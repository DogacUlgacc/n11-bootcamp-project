import { useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  clearStoredCartId,
  getCurrentCart,
  removeCartItem,
  updateCartItemQuantity,
} from "../api/cartApi";
import { checkoutOrder } from "../api/orderApi";
import { waitForPaymentByOrderId } from "../api/paymentApi";
import { getApiErrorMessage, getProductById } from "../api/productApi";

const userId = "07ed7f7a-1340-431a-a564-f1932498dc99";

function CartPage() {
  const navigate = useNavigate();
  const [cart, setCart] = useState(null);
  const [productMap, setProductMap] = useState({});
  const [loading, setLoading] = useState(true);
  const [updatingProductId, setUpdatingProductId] = useState("");
  const [checkingOut, setCheckingOut] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const fetchCartDetails = async () => {
    const cartData = await getCurrentCart(userId, "TRY");

    if (!cartData) {
      return {
        cartData: null,
        productLookup: {},
      };
    }

    const products = await Promise.all(
      (cartData.items || []).map(async (item) => {
        try {
          const product = await getProductById(item.productId);
          return [item.productId, product];
        } catch {
          return [item.productId, null];
        }
      }),
    );

    return {
      cartData,
      productLookup: Object.fromEntries(products),
    };
  };

  const applyCartDetails = ({ cartData, productLookup }) => {
    setCart(cartData);
    setProductMap(productLookup);
  };

  useEffect(() => {
    let ignore = false;

    const run = async () => {
      try {
        setLoading(true);
        setError("");

        const details = await fetchCartDetails();

        if (!ignore) {
          applyCartDetails(details);
        }
      } catch (err) {
        if (!ignore) {
          clearStoredCartId();
          setCart(null);
          setProductMap({});
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
  }, []);

  const itemCount = useMemo(() => {
    return (cart?.items || []).reduce(
      (total, item) => total + item.quantity,
      0,
    );
  }, [cart]);

  const formatMoney = (amount) => {
    return new Intl.NumberFormat("tr-TR", {
      style: "currency",
      currency: cart?.currency || "TRY",
    }).format(amount || 0);
  };

  const handleQuantityChange = async (item, nextQuantity) => {
    const quantity = Number(nextQuantity);

    if (!Number.isInteger(quantity) || quantity < 1) {
      return;
    }

    try {
      setUpdatingProductId(item.productId);
      setError("");
      setSuccessMessage("");

      const updatedCart = await updateCartItemQuantity(
        cart.cartId,
        item.productId,
        quantity,
      );

      setCart(updatedCart);
      setSuccessMessage("Sepet güncellendi.");
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setUpdatingProductId("");
    }
  };

  const handleRemove = async (item) => {
    try {
      setUpdatingProductId(item.productId);
      setError("");
      setSuccessMessage("");

      await removeCartItem(cart.cartId, item.productId);
      setSuccessMessage("Ürün sepetten kaldırıldı.");
      applyCartDetails(await fetchCartDetails());
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setUpdatingProductId("");
    }
  };

  const handleCheckout = async () => {
    if (!cart?.cartId || (cart.items || []).length === 0 || checkingOut) {
      return;
    }

    try {
      setCheckingOut(true);
      setError("");
      setSuccessMessage("");

      const order = await checkoutOrder({
        cartId: cart.cartId,
      });

      const payment = await waitForPaymentByOrderId(order.id);

      navigate(`/payment/${payment.id}`, {
        state: {
          orderId: order.id,
          payment,
        },
      });
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setCheckingOut(false);
    }
  };

  return (
    <div className="app">
      <header className="site-header">
        <div className="header-top detail-header">
          <Link className="logo" to="/" aria-label="n11 ana sayfa">
            n11
          </Link>
          <Link className="back-link" to="/">
            Alışverişe dön
          </Link>
        </div>
      </header>

      <main className="container">
        <section className="cart-page">
          <div className="section-heading">
            <div>
              <span className="section-kicker">Sepetim</span>
              <h1>Sepet Detayları</h1>
            </div>
            <span className="cart-count">{itemCount} ürün</span>
          </div>

          {loading && <p className="info-message">Sepet yükleniyor...</p>}
          {error && <p className="error-message">{error}</p>}
          {successMessage && (
            <p className="success-message">{successMessage}</p>
          )}

          {!loading && cart && (
            <div className="cart-layout">
              <div className="cart-items">
                {(cart.items || []).map((item) => {
                  const product = productMap[item.productId];
                  const isUpdating = updatingProductId === item.productId;

                  return (
                    <article className="cart-item" key={item.productId}>
                      <Link
                        className="cart-item-media"
                        to={`/products/${item.productId}`}
                        aria-label={`${product?.productName || "Ürün"} detayı`}
                      >
                        <span>
                          {product?.productName
                            ?.charAt(0)
                            .toLocaleUpperCase("tr-TR") || "n"}
                        </span>
                      </Link>

                      <div className="cart-item-content">
                        <h2>
                          {product?.productName || "Ürün bilgisi alınamadı"}
                        </h2>
                        <p>{product?.productDescription || item.productId}</p>
                        <strong>{formatMoney(item.unitPrice)}</strong>
                      </div>

                      <div className="cart-item-actions">
                        <label>
                          Adet
                          <input
                            type="number"
                            min="1"
                            value={item.quantity}
                            disabled={isUpdating}
                            onChange={(event) =>
                              handleQuantityChange(item, event.target.value)
                            }
                          />
                        </label>
                        <span>{formatMoney(item.totalPrice)}</span>
                        <button
                          type="button"
                          className="danger-button"
                          disabled={isUpdating}
                          onClick={() => handleRemove(item)}
                        >
                          Kaldır
                        </button>
                      </div>
                    </article>
                  );
                })}

                {(cart.items || []).length === 0 && (
                  <p className="info-message">
                    Sepetiniz boş. Ürünleri inceleyip sepete ekleyebilirsiniz.
                  </p>
                )}
              </div>

              <aside className="cart-summary">
                <h2>Sipariş Özeti</h2>
                <div>
                  <span>Sepet ID</span>
                  <strong>{cart.cartId}</strong>
                </div>
                <div>
                  <span>Toplam ürün</span>
                  <strong>{itemCount}</strong>
                </div>
                <div>
                  <span>Toplam</span>
                  <strong>{formatMoney(cart.totalAmount)}</strong>
                </div>
                <button
                  type="button"
                  disabled={(cart.items || []).length === 0 || checkingOut}
                  onClick={handleCheckout}
                >
                  {checkingOut
                    ? "Sipariş oluşturuluyor..."
                    : "Alışverişi Tamamla"}
                </button>
              </aside>
            </div>
          )}
        </section>
      </main>
    </div>
  );
}

export default CartPage;
