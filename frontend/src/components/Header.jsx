import { Link } from "react-router-dom";

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

function Header({ searchTerm, onSearchChange, isAuthenticated, onLogout }) {
  return (
    <header className="site-header">
      <div className="header-top">
        <Link className="logo" to="/" aria-label="n11 ana sayfa">
          n11
        </Link>

        <label className="search-bar">
          <span>Ürün ara</span>
          <input
            value={searchTerm}
            onChange={(event) => onSearchChange(event.target.value)}
            placeholder="Marka, ürün veya kategori ara"
            type="search"
          />
          <button type="button">Ara</button>
        </label>

        <div className="header-actions">
          {isAuthenticated ? (
            <>
              <Link to="/account" className="cart-button">
                Hesabım
              </Link>
              <button type="button" className="cart-button" onClick={onLogout}>
                Çıkış Yap
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className="cart-button">
                Giriş Yap
              </Link>
              <Link to="/register" className="cart-button">
                Üye ol
              </Link>
            </>
          )}
        </div>
      </div>

      <nav className="category-menu" aria-label="Kategori menüsü">
        {categories.map((category) => (
          <Link key={category} to="/">
            {category}
          </Link>
        ))}
      </nav>
    </header>
  );
}

export default Header;
