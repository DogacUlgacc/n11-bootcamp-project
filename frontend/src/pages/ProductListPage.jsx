import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import {
  createProduct,
  deleteProduct,
  getApiErrorMessage,
  getProducts,
  updateProduct,
} from "../api/productApi";
import ProductCard from "../components/ProductCard";

const categories = [
  "Elektronik",
  "Moda",
  "Ev & Yaşam",
  "Anne & Bebek",
  "Kozmetik",
  "Spor",
  "Süpermarket",
  "Otomotiv",
];

const emptyForm = {
  productName: "",
  productDescription: "",
  amount: "",
  currency: "TRY",
  stockQuantity: "",
};

function ProductListPage() {
  const [products, setProducts] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(8);
  const [sortBy, setSortBy] = useState("id");
  const [direction, setDirection] = useState("asc");
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [formValues, setFormValues] = useState(emptyForm);
  const [editingProduct, setEditingProduct] = useState(null);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      setError("");

      const data = await getProducts({ page, size, sortBy, direction });

      setProducts(data.content || []);
      setTotalPages(data.totalPages || 0);
      setTotalElements(data.totalElements || 0);
    } catch (err) {
      setProducts([]);
      setTotalPages(0);
      setTotalElements(0);
      setError(getApiErrorMessage(err));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    let ignore = false;

    const run = async () => {
      try {
        setLoading(true);
        setError("");

        const data = await getProducts({ page, size, sortBy, direction });

        if (!ignore) {
          setProducts(data.content || []);
          setTotalPages(data.totalPages || 0);
          setTotalElements(data.totalElements || 0);
        }
      } catch (err) {
        if (!ignore) {
          setProducts([]);
          setTotalPages(0);
          setTotalElements(0);
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
  }, [page, size, sortBy, direction]);

  const visibleProducts = useMemo(() => {
    const normalizedSearch = searchTerm.trim().toLocaleLowerCase("tr-TR");

    if (!normalizedSearch) {
      return products;
    }

    return products.filter((product) =>
      `${product.productName} ${product.productDescription}`
        .toLocaleLowerCase("tr-TR")
        .includes(normalizedSearch),
    );
  }, [products, searchTerm]);

  const resetForm = () => {
    setFormValues(emptyForm);
    setEditingProduct(null);
  };

  const handleFieldChange = (event) => {
    const { name, value } = event.target;

    setFormValues((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const toProductPayload = () => ({
    productName: formValues.productName.trim(),
    productDescription: formValues.productDescription.trim(),
    amount: Number(formValues.amount),
    currency: formValues.currency.trim().toUpperCase(),
    stockQuantity: Number(formValues.stockQuantity),
  });

  const validateForm = () => {
    if (!formValues.productName.trim()) {
      return "Ürün adı zorunludur.";
    }

    if (formValues.productName.trim().length > 255) {
      return "Ürün adı en fazla 255 karakter olabilir.";
    }

    if (!formValues.productDescription.trim()) {
      return "Ürün açıklaması zorunludur.";
    }

    if (formValues.amount === "" || Number(formValues.amount) < 0) {
      return "Fiyat 0 veya daha büyük olmalıdır.";
    }

    if (!formValues.currency.trim()) {
      return "Para birimi zorunludur.";
    }

    if (
      formValues.stockQuantity === "" ||
      Number(formValues.stockQuantity) < 0
    ) {
      return "Stok miktarı 0 veya daha büyük olmalıdır.";
    }

    return "";
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const validationMessage = validateForm();

    if (validationMessage) {
      setSuccessMessage("");
      setError(validationMessage);
      return;
    }

    try {
      setSaving(true);
      setError("");
      setSuccessMessage("");

      const payload = toProductPayload();

      if (editingProduct) {
        await updateProduct(editingProduct.id, payload);
        setSuccessMessage("Ürün başarıyla güncellendi.");
      } else {
        await createProduct(payload);
        setSuccessMessage("Ürün başarıyla oluşturuldu.");
      }

      resetForm();
      await fetchProducts();
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = (product) => {
    setEditingProduct(product);
    setFormValues({
      productName: product.productName || "",
      productDescription: product.productDescription || "",
      amount: product.amount ?? "",
      currency: product.currency || "TRY",
      stockQuantity: product.stockQuantity ?? "",
    });
    setSuccessMessage("");
    setError("");
  };

  const handleDelete = async (product) => {
    const confirmed = window.confirm(
      `${product.productName} ürününü silmek istediğinize emin misiniz?`,
    );

    if (!confirmed) {
      return;
    }

    try {
      setError("");
      setSuccessMessage("");
      await deleteProduct(product.id);
      setSuccessMessage("Ürün başarıyla silindi.");
      await fetchProducts();
    } catch (err) {
      setError(getApiErrorMessage(err));
    }
  };

  const handleSortChange = (event) => {
    const [nextSortBy, nextDirection] = event.target.value.split(":");

    setPage(0);
    setSortBy(nextSortBy);
    setDirection(nextDirection);
  };

  return (
    <div className="app">
      <header className="site-header">
        <div className="header-top">
          <a className="logo" href="/" aria-label="n11 ana sayfa">
            n11
          </a>

          <label className="search-bar">
            <span>Ürün ara</span>
            <input
              value={searchTerm}
              onChange={(event) => setSearchTerm(event.target.value)}
              placeholder="Marka, ürün veya kategori ara"
              type="search"
            />
            <button type="button">Ara</button>
          </label>

          <div className="header-actions">
            <Link to="/account" className="ghost-button">
              Hesabım
            </Link>
            <Link to="/cart" className="cart-button">
              Sepetim
            </Link>
          </div>
        </div>

        <nav className="category-menu" aria-label="Kategori menüsü">
          {categories.map((category) => (
            <a key={category} href="/">
              {category}
            </a>
          ))}
        </nav>
      </header>

      <main className="container">
        <section className="hero-banner">
          <div className="hero-content">
            <span className="campaign-label">Backend bağlantılı vitrin</span>
            <h1>n11 ürün yönetimi ve alışveriş vitrini</h1>
            <p>
              Ürünler doğrudan Product Service API üzerinden listelenir; stok,
              fiyat ve kayıt işlemleri aynı ekranda yönetilir.
            </p>
          </div>
          <div className="hero-panel" aria-label="Ürün özeti">
            <span>Toplam ürün</span>
            <strong>{totalElements}</strong>
            <small>Sayfa {page + 1}</small>
          </div>
        </section>

        <section className="dashboard-grid">
          <form className="product-form" onSubmit={handleSubmit}>
            <div className="section-heading compact">
              <div>
                <span className="section-kicker">
                  {editingProduct ? "Ürün güncelle" : "Yeni ürün"}
                </span>
                <h2>{editingProduct ? "Ürünü düzenle" : "Ürün oluştur"}</h2>
              </div>
            </div>

            <label>
              Ürün adı
              <input
                name="productName"
                value={formValues.productName}
                maxLength={255}
                onChange={handleFieldChange}
                placeholder="iPhone 15"
              />
            </label>

            <label>
              Açıklama
              <textarea
                name="productDescription"
                value={formValues.productDescription}
                onChange={handleFieldChange}
                placeholder="128 GB"
                rows="4"
              />
            </label>

            <div className="form-row">
              <label>
                Fiyat
                <input
                  name="amount"
                  value={formValues.amount}
                  min="0"
                  step="0.01"
                  onChange={handleFieldChange}
                  placeholder="59999.99"
                  type="number"
                />
              </label>

              <label>
                Para birimi
                <input
                  name="currency"
                  value={formValues.currency}
                  onChange={handleFieldChange}
                  placeholder="TRY"
                />
              </label>
            </div>

            <label>
              Stok
              <input
                name="stockQuantity"
                value={formValues.stockQuantity}
                min="0"
                step="1"
                onChange={handleFieldChange}
                placeholder="25"
                type="number"
              />
            </label>

            <div className="form-actions">
              <button type="submit" disabled={saving}>
                {saving
                  ? "Kaydediliyor..."
                  : editingProduct
                    ? "Güncelle"
                    : "Oluştur"}
              </button>
              {editingProduct && (
                <button
                  type="button"
                  className="ghost-button"
                  onClick={resetForm}
                >
                  Vazgeç
                </button>
              )}
            </div>
          </form>

          <section className="deals-section">
            <div className="section-heading">
              <div>
                <span className="section-kicker">Canlı ürün listesi</span>
                <h2>Ürünler</h2>
              </div>

              <div className="list-controls">
                <select
                  value={`${sortBy}:${direction}`}
                  onChange={handleSortChange}
                  aria-label="Sıralama"
                >
                  <option value="id:asc">Varsayılan</option>
                  <option value="productName:asc">Ada göre artan</option>
                  <option value="productName:desc">Ada göre azalan</option>
                  <option value="price.amount:asc">Fiyat artan</option>
                  <option value="price.amount:desc">Fiyat azalan</option>
                </select>
                <select
                  value={size}
                  onChange={(event) => {
                    setPage(0);
                    setSize(Number(event.target.value));
                  }}
                  aria-label="Sayfa boyutu"
                >
                  <option value={4}>4 ürün</option>
                  <option value={8}>8 ürün</option>
                  <option value={12}>12 ürün</option>
                </select>
              </div>
            </div>

            {successMessage && (
              <p className="success-message">{successMessage}</p>
            )}
            {error && <p className="error-message">{error}</p>}
            {loading && <p className="info-message">Ürünler yükleniyor...</p>}

            <div className="product-list">
              {visibleProducts.map((product) => (
                <div className="product-shell" key={product.id}>
                  <ProductCard product={product} />
                  <div className="product-admin-actions">
                    <button type="button" onClick={() => handleEdit(product)}>
                      Düzenle
                    </button>
                    <button
                      type="button"
                      className="danger-button"
                      onClick={() => handleDelete(product)}
                    >
                      Sil
                    </button>
                  </div>
                </div>
              ))}
            </div>

            {!loading && visibleProducts.length === 0 && (
              <p className="info-message">
                Gösterilecek ürün bulunamadı. Yeni bir ürün oluşturabilirsiniz.
              </p>
            )}

            <div className="pagination">
              <button
                type="button"
                disabled={page === 0 || loading}
                onClick={() => setPage((current) => current - 1)}
              >
                Önceki
              </button>

              <span>
                Sayfa {page + 1} / {totalPages || 1}
              </span>

              <button
                type="button"
                disabled={page + 1 >= totalPages || loading}
                onClick={() => setPage((current) => current + 1)}
              >
                Sonraki
              </button>
            </div>
          </section>
        </section>
      </main>
    </div>
  );
}

export default ProductListPage;
