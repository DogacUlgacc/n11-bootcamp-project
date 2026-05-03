import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getApiErrorMessage } from "../api/productApi";
import { getUserById } from "../api/userApi";

const userId = "07ed7f7a-1340-431a-a564-f1932498dc99";

function AccountPage() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let ignore = false;

    const run = async () => {
      try {
        setLoading(true);
        setError("");

        const data = await getUserById(userId);

        if (!ignore) {
          setUser(data);
        }
      } catch (err) {
        if (!ignore) {
          setUser(null);
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

  const fullName = `${user?.firstName || ""} ${user?.lastName || ""}`.trim();

  return (
    <div className="app">
      <header className="site-header">
        <div className="header-top detail-header">
          <Link className="logo" to="/" aria-label="n11 ana sayfa">
            n11
          </Link>
          <div className="detail-header-actions">
            <Link className="back-link" to="/">
              Alışverişe dön
            </Link>
            <Link className="cart-button" to="/cart">
              Sepetim
            </Link>
          </div>
        </div>
      </header>

      <main className="container">
        <section className="account-page">
          <div className="section-heading">
            <div>
              <span className="section-kicker">Hesabım</span>
              <h1>Kullanıcı Bilgileri</h1>
            </div>
          </div>

          {loading && <p className="info-message">Kullanıcı bilgileri yükleniyor...</p>}
          {error && <p className="error-message">{error}</p>}

          {!loading && user && (
            <div className="account-layout">
              <aside className="account-summary">
                <span className="account-avatar">
                  {user.firstName?.charAt(0).toLocaleUpperCase("tr-TR") || "n"}
                </span>
                <h2>{fullName || "Kullanıcı"}</h2>
                <p>{user.userType}</p>
              </aside>

              <div className="account-card">
                <dl className="detail-facts">
                  <div>
                    <dt>Ad</dt>
                    <dd>{user.firstName || "-"}</dd>
                  </div>
                  <div>
                    <dt>Soyad</dt>
                    <dd>{user.lastName || "-"}</dd>
                  </div>
                  <div>
                    <dt>E-posta</dt>
                    <dd>{user.email || "-"}</dd>
                  </div>
                  <div>
                    <dt>Telefon</dt>
                    <dd>{user.phoneNumber || "-"}</dd>
                  </div>
                  <div>
                    <dt>Üyelik tipi</dt>
                    <dd>{user.userType || "-"}</dd>
                  </div>
                </dl>
              </div>

              <section className="account-addresses">
                <div className="section-heading compact">
                  <div>
                    <span className="section-kicker">Adresler</span>
                    <h2>Kayıtlı adresler</h2>
                  </div>
                </div>

                {(user.address || []).map((address) => (
                  <article className="address-card" key={address.title}>
                    <h3>{address.title}</h3>
                    <p>{address.street}</p>
                    <span>
                      {address.city}, {address.country}
                    </span>
                  </article>
                ))}

                {(user.address || []).length === 0 && (
                  <p className="info-message">Kayıtlı adres bulunamadı.</p>
                )}
              </section>
            </div>
          )}
        </section>
      </main>
    </div>
  );
}

export default AccountPage;
