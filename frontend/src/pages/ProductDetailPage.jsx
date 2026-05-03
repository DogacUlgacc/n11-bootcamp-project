import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { addToCart } from "../api/cartApi";
import { getApiErrorMessage, getProductById } from "../api/productApi";

function ProductDetailPage() {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [adding, setAdding] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  useEffect(() => {
    if (!successMessage) {
      return undefined;
    }

    const timeoutId = window.setTimeout(() => {
      setSuccessMessage("");
    }, 3000);

    return () => {
      window.clearTimeout(timeoutId);
    };
  }, [successMessage]);

  useEffect(() => {
    let ignore = false;

    const fetchProduct = async () => {
      try {
        setLoading(true);
        setError("");

        const data = await getProductById(id);

        if (!ignore) {
          setProduct(data);
        }
      } catch (err) {
        if (!ignore) {
          setProduct(null);
          setError(getApiErrorMessage(err));
        }
      } finally {
        if (!ignore) {
          setLoading(false);
        }
      }
    };

    fetchProduct();

    return () => {
      ignore = true;
    };
  }, [id]);

  const handleAddToCart = async () => {
    if (!product) {
      return;
    }

    try {
      setAdding(true);
      setError("");
      setSuccessMessage("");

      await addToCart(product.id, 1);
      setSuccessMessage(`${product.productName} sepete eklendi.`);
    } catch (err) {
      console.error(err);
      setError("Sepete eklenemedi, tekrar deneyin");
    } finally {
      setAdding(false);
    }
  };

  const formattedPrice = new Intl.NumberFormat("tr-TR", {
    style: "currency",
    currency: product?.currency || "TRY",
  }).format(product?.amount || 0);
  const productInitial =
    product?.productName?.charAt(0).toLocaleUpperCase("tr-TR") || "n";
  const hasStock = Number(product?.stockQuantity) > 0;

  return (
    <div className="app">
      <header className="site-header">
        <div className="header-top detail-header">
          <Link className="logo" to="/" aria-label="n11 ana sayfa">
            n11
          </Link>
          <div className="detail-header-actions">
            <Link className="back-link" to="/">
              Urunlere don
            </Link>
            <Link className="cart-button" to="/cart">
              Sepetim
            </Link>
          </div>
        </div>
      </header>

      <main className="container">
        {loading && <p className="info-message">Urun detayi yukleniyor...</p>}
        {error && <p className="error-message">{error}</p>}
        {successMessage && <p className="success-message">{successMessage}</p>}

        {!loading && product && (
          <section className="product-detail">
            <div className="detail-media">
              <span className={hasStock ? "stock-badge" : "stock-badge out"}>
                {hasStock ? "Stokta" : "Tukendi"}
              </span>
              <span className="detail-initial">{productInitial}</span>
            </div>

            <div className="detail-content">
              <span className="section-kicker">Urun detayi</span>
              <h1>{product.productName}</h1>
              <p>{product.productDescription}</p>

              <dl className="detail-facts">
                <div>
                  <dt>Fiyat</dt>
                  <dd>{formattedPrice}</dd>
                </div>
                <div>
                  <dt>Stok</dt>
                  <dd>{product.stockQuantity}</dd>
                </div>
                <div>
                  <dt>Urun Aciklamasi</dt>
                  <dd>{product.productName}</dd>
                </div>
              </dl>

              <button
                className="detail-cart-button"
                type="button"
                disabled={!hasStock || adding}
                onClick={handleAddToCart}
              >
                {adding ? "Ekleniyor..." : "Sepete Ekle"}
              </button>
            </div>
          </section>
        )}
      </main>
    </div>
  );
}

export default ProductDetailPage;
