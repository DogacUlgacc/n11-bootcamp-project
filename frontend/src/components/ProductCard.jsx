import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { addToCart } from "../api/cartApi";

function ProductCard({ product }) {
  const [adding, setAdding] = useState(false);
  const [feedback, setFeedback] = useState(null);

  const formattedPrice = new Intl.NumberFormat("tr-TR", {
    style: "currency",
    currency: product.currency || "TRY",
  }).format(product.amount || 0);

  const productInitial =
    product.productName?.charAt(0).toLocaleUpperCase("tr-TR") || "n";
  const hasStock = Number(product.stockQuantity) > 0;

  useEffect(() => {
    if (!feedback) {
      return undefined;
    }

    const timeoutId = window.setTimeout(() => {
      setFeedback(null);
    }, 3000);

    return () => {
      window.clearTimeout(timeoutId);
    };
  }, [feedback]);

  const handleAddToCart = async (productId) => {
    try {
      setAdding(true);
      setFeedback(null);
      await addToCart(productId, 1);
      setFeedback({ type: "success", message: "Sepete eklendi" });
    } catch (error) {
      console.error(error);
      setFeedback({
        type: "error",
        message: "Sepete eklenemedi, tekrar deneyin",
      });
    } finally {
      setAdding(false);
    }
  };

  return (
    <div className="product-card">
      <Link
        className="product-media"
        to={`/products/${product.id}`}
        aria-label={`${product.productName} detay sayfasi`}
      >
        <span className={hasStock ? "stock-badge" : "stock-badge out"}>
          {hasStock ? "Stokta" : "Tukendi"}
        </span>
        <span className="product-initial">{productInitial}</span>
      </Link>
      <div className="product-content">
        <span className="shipping-badge">n11 hizli teslimat</span>
        <h3>{product.productName}</h3>
        <p>{product.productDescription}</p>
        <div className="price-row">
          <strong>{formattedPrice}</strong>
          <span>Stok: {product.stockQuantity ?? "-"}</span>
        </div>
        <button
          type="button"
          disabled={!hasStock || adding}
          onClick={() => handleAddToCart(product.id)}
        >
          {adding ? "Ekleniyor..." : "Sepete Ekle"}
        </button>
        {feedback && (
          <span className={`inline-feedback ${feedback.type}`}>
            {feedback.message}
          </span>
        )}
      </div>
    </div>
  );
}

export default ProductCard;
