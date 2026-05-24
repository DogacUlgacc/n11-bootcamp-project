import axios from "axios";
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
const REGISTER_URL = "http://localhost:8888/api/v1/users/register";
function RegisterPage() {
  // Controlled form: input value'lari React state'inden gelir, her tus vurusunda
  // state guncellenir. Boylece submit aninda tum form elimizdedir.
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phoneNumber: "",
    password: "",
    confirmPassword: "",
    userType: "CUSTOMER",
  });
  const [addressData, setAddressData] = useState({
    title: "",
    city: "",
    street: "",
    country: "",
  });
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const navigate = useNavigate();
  const updateFormData = (event) => {
    const { name, value } = event.target;

    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const updateAddressData = (event) => {
    const { name, value } = event.target;

    setAddressData({
      ...addressData,
      [name]: value,
    });
  };
  const isBlank = (value) => value.trim() === "";
  const handleSubmit = async (event) => {
    event.preventDefault();
    // Sayfa yenilenmeden form kontrol edilir; hata varsa sadece state degisir
    // ve React ayni ekrani hata mesaji ile tekrar render eder.
    setErrorMessage("");
    setSuccessMessage("");

    if (
      isBlank(formData.firstName) ||
      isBlank(formData.lastName) ||
      isBlank(formData.email) ||
      isBlank(formData.phoneNumber) ||
      isBlank(formData.password) ||
      isBlank(formData.confirmPassword) ||
      isBlank(addressData.title) ||
      isBlank(addressData.city) ||
      isBlank(addressData.street) ||
      isBlank(addressData.country)
    ) {
      setErrorMessage("Lütfen tüm alanları doldurun.");
      return;
    }
    if (formData.password !== formData.confirmPassword) {
      setErrorMessage("Şifreler eşleşmiyor.");
      return;
    }
    const registerRequest = {
      firstName: formData.firstName,
      lastName: formData.lastName,
      email: formData.email,
      phoneNumber: formData.phoneNumber,
      password: formData.password,
      userType: formData.userType,
      addresses: [
        {
          title: addressData.title,
          city: addressData.city,
          street: addressData.street,
          country: addressData.country,
        },
      ],
    };

    try {
      const response = await axios.post(REGISTER_URL, registerRequest);
      console.log("Register request:", registerRequest);
      console.log("Backend response:", response.data);
      setSuccessMessage("Kayıt formu başarıyla hazırlandı.");
      setTimeout(() => {
        navigate("/login");
      }, 1500);
    } catch (error) {
      console.error("Register error:", error);
      console.error("Status:", error.response?.status);
      console.error("Response data:", error.response?.data);
      console.log("registerRequest", registerRequest);
      setErrorMessage(
        error.response?.data?.message || "Kayıt sırasında bir hata oluştu.",
      );
    }
  };

  return (
    <div className="register-page">
      <header className="login-header">
        <Link className="logo login-logo" to="/" aria-label="n11 ana sayfa">
          n11
        </Link>
        <span>Clone</span>
      </header>

      <main className="register-shell">
        <section className="register-panel" aria-labelledby="register-title">
          <div className="register-heading">
            <span className="section-kicker">Yeni hesap</span>
            <h1 id="register-title">Uye Ol</h1>
            <p>Alisverise baslamak icin hesap ve adres bilgilerini doldurun.</p>
          </div>
          {errorMessage && (
            <p className="register-error-message">{errorMessage}</p>
          )}

          {successMessage && (
            <p className="register-success-message">{successMessage}</p>
          )}
          <form className="register-form" onSubmit={handleSubmit}>
            <fieldset className="register-fieldset">
              <legend>Kisisel Bilgiler</legend>

              <label className="register-field">
                <span>Ad</span>
                <input
                  type="text"
                  name="firstName"
                  value={formData.firstName}
                  placeholder="Adiniz"
                  onChange={updateFormData}
                  required
                />
              </label>

              <label className="register-field">
                <span>Soyad</span>
                <input
                  type="text"
                  name="lastName"
                  value={formData.lastName}
                  placeholder="Soyadiniz"
                  onChange={updateFormData}
                  required
                />
              </label>

              <label className="register-field">
                <span>E-posta</span>
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  placeholder="ornek@mail.com"
                  onChange={updateFormData}
                  required
                />
              </label>

              <label className="register-field">
                <span>Telefon</span>
                <input
                  type="tel"
                  name="phoneNumber"
                  value={formData.phoneNumber}
                  placeholder="05xx xxx xx xx"
                  onChange={updateFormData}
                  required
                />
              </label>

              <label className="register-field">
                <span>Sifre</span>
                <input
                  type="password"
                  name="password"
                  value={formData.password}
                  placeholder="Sifreniz"
                  onChange={updateFormData}
                  required
                  minLength={6}
                />
              </label>

              <label className="register-field">
                <span>Sifre Tekrar</span>
                <input
                  type="password"
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  placeholder="Sifrenizi tekrar girin"
                  onChange={updateFormData}
                  required
                  minLength={6}
                />
              </label>
            </fieldset>

            <fieldset className="register-fieldset">
              <legend>Adres Bilgileri</legend>

              <label className="register-field">
                <span>Adres Basligi</span>
                <input
                  type="text"
                  name="title"
                  value={addressData.title}
                  placeholder="Ev, is, okul"
                  onChange={updateAddressData}
                  required
                />
              </label>

              <label className="register-field">
                <span>Sehir</span>
                <input
                  type="text"
                  name="city"
                  value={addressData.city}
                  placeholder="Istanbul"
                  onChange={updateAddressData}
                  required
                />
              </label>

              <label className="register-field register-field-wide">
                <span>Adres</span>
                <input
                  type="text"
                  name="street"
                  value={addressData.street}
                  placeholder="Mahalle, cadde, bina ve daire bilgisi"
                  onChange={updateAddressData}
                  required
                />
              </label>

              <label className="register-field">
                <span>Ulke</span>
                <input
                  type="text"
                  name="country"
                  value={addressData.country}
                  placeholder="Turkiye"
                  onChange={updateAddressData}
                  required
                />
              </label>
            </fieldset>

            <div className="register-actions">
              <button className="login-primary-button" type="submit">
                Hesap Olustur
              </button>
              <Link className="register-login-link" to="/login">
                Zaten hesabim var
              </Link>
            </div>
          </form>
        </section>
      </main>
    </div>
  );
}
export default RegisterPage;
