# n11 Demo Bootcamp - Backend Microservices Project

## 1) Proje Tanımı
Bu proje, e-ticaret senaryolarını uçtan uca göstermek için hazırlanmış **mikroservis tabanlı bir demo backend** uygulamasıdır. Amaç; tek bir monolit yerine, iş yeteneklerinin bağımsız servisler halinde nasıl tasarlanıp birlikte çalıştırıldığını göstermektir.

Proje özellikle backend odaklıdır ve temel e-ticaret akışlarını kapsar:
- Ürün yönetimi
- Sepet yönetimi
- Sipariş yönetimi
- Ödeme yönetimi
- Kullanıcı yönetimi

---

## 2) Mimari Yaklaşım

- **Microservices Architecture:** Her iş alanı (user, product, cart, order, payment) ayrı servis olarak konumlanır.
- **DDD (Domain Driven Design):** Domain, application, infrastructure ayrımıyla iş kuralları katmanlı şekilde tutulur.
- **CQRS:** Yazma ve okuma operasyonları command/query ayrımıyla ele alınır.
- **CommandBus / QueryBus:** Controller katmanından gelen istekler bus yapısı ile ilgili handler’lara yönlendirilir.
- **Event-Driven Architecture:** Özellikle checkout/ödeme tarafında servisler event’ler ile asenkron haberleşir.
- **Kafka:** Servisler arası event taşıma katmanı olarak kullanılır.
- **API Gateway:** Dış dünyadan gelen istekler tek giriş noktası üzerinden yönlendirilir.
- **Eureka Discovery Server:** Servislerin dinamik olarak bulunmasını sağlar.
- **Config Server:** Ortak konfigürasyonlar merkezi repo’dan yönetilir.
- **Docker Compose:** Yerel geliştirme ortamı tek komutla ayağa kaldırılabilir.
- **Keycloak:** Authentication/authorization altyapısı için kullanılır.
- **Swagger/OpenAPI:** Servis bazlı API keşfi ve test için kullanılır.

---

## 3) Servisler
- **user-service:** Kullanıcı oluşturma, güncelleme, silme ve sorgulama işlemleri.
- **product-service:** Ürün CRUD, listeleme ve sayfalama işlemleri.
- **cart-service:** Sepet oluşturma, sepete ürün ekleme/çıkarma, adet güncelleme.
- **order-service:** Sipariş oluşturma, checkout başlatma, sipariş durum yönetimi.
- **payment-service:** Ödeme kaydı, ödeme denemesi ve ödeme sonucu yönetimi.
- **gateway-server:** API isteklerini ilgili servislere route eder.
- **discovery-server:** Eureka registry (servis keşfi) görevi görür.
- **config-server:** Merkezi konfigürasyon yönetimi sağlar.

---

## 4) Temel Akış (Checkout)
Kısa checkout akışı şu şekildedir:
1. Kullanıcı ürünleri listeler.
2. Kullanıcı sepete ürün ekler.
3. Kullanıcı checkout ile sipariş oluşturur.
4. `OrderCreatedEvent` Kafka’ya yayınlanır.
5. `payment-service` bu event’i dinleyip ödeme kaydı/ödeme sürecini başlatır.
6. Ödeme başarılı/başarısız event’i tekrar `order-service` tarafına iletilir.
7. `order-service` sipariş durumunu günceller.

---

## 5) API Endpointleri
Aşağıdaki tablo, controller sınıflarında tanımlı endpointleri içerir.

| Service | Method | Endpoint | Description |
|---|---|---|---|
| product-service | POST | /api/v1/products | Creates a product |
| product-service | GET | /api/v1/products/{id} | Gets product by id |
| product-service | GET | /api/v1/products/all | Lists all products |
| product-service | GET | /api/v1/products?page=&size=&sortBy=&direction= | Lists products with pagination |
| product-service | PUT | /api/v1/products/{id} | Updates a product |
| product-service | DELETE | /api/v1/products/{id} | Deletes a product |
| user-service | POST | /api/v1/users | Creates a user |
| user-service | PUT | /api/v1/users/{id} | Updates a user |
| user-service | DELETE | /api/v1/users/{id} | Deletes a user |
| user-service | GET | /api/v1/users/{id} | Gets user by id |
| user-service | GET | /api/v1/users/by-external-id/{externalId} | Gets user identity by external id |
| user-service | GET | /api/v1/users | Lists users |
| cart-service | POST | /api/v1/carts | Creates a cart |
| cart-service | POST | /api/v1/carts/items | Adds item to cart |
| cart-service | GET | /api/v1/carts/{cartId} | Gets cart by id |
| cart-service | PUT | /api/v1/carts/{cartId}/items/{productId} | Updates cart item quantity |
| cart-service | DELETE | /api/v1/carts/{cartId}/items/{productId} | Removes item from cart |
| order-service | POST | /api/v1/orders | Creates an order manually |
| order-service | POST | /api/v1/orders/checkout | Creates order from cart checkout |
| order-service | PUT | /api/v1/orders/{id} | Updates order status |
| order-service | DELETE | /api/v1/orders/{id} | Deletes an order |
| order-service | GET | /api/v1/orders/{id} | Gets order by id |
| order-service | GET | /api/v1/orders | Lists orders |
| payment-service | POST | /api/v1/payments | Creates pending payment |
| payment-service | POST | /api/v1/payments/{paymentId}/complete | Marks payment as completed |
| payment-service | POST | /api/v1/payments/{paymentId}/fail | Marks payment as failed |
| payment-service | POST | /api/v1/payments/{paymentId}/pay | Tries payment with Iyzico |
| payment-service | GET | /api/v1/payments/{id} | Gets payment by id |
| payment-service | GET | /api/v1/payments | Lists payments |

---

## 6) Teknolojiler
- Java
- Spring Boot
- Spring Security
- Spring Data JPA / Hibernate
- PostgreSQL
- Kafka
- Docker / Docker Compose
- Keycloak
- Eureka
- Spring Cloud Gateway
- OpenFeign
- Swagger / OpenAPI
- Maven

---

## 7) Projeyi Çalıştırma
Yerel ortam için temel adımlar:

```bash
docker compose up -d
```

Ardından servisleri aşağıdaki sırayla çalıştırmanız önerilir:
1. `config-server` (8071)
2. `discovery-server` (8761)
3. `gateway-server` (8888)
4. Domain servisleri: `user-service` (8082), `product-service` (8081), `cart-service` (8083), `payment-service` (8084), `order-service` (8085)

Not: `docker-compose.yml` dosyası altyapı bileşenlerini (PostgreSQL örnekleri, Kafka, Keycloak vb.) ayağa kaldırır; servisleri IDE/Maven ile ayrıca başlatabilirsiniz.

---

## 8) Swagger
Varsayılan SpringDoc endpoint desenleri ile servis bazlı erişim:

- product-service: `http://localhost:8081/swagger-ui/index.html`
- user-service: `http://localhost:8082/swagger-ui/index.html`
- cart-service: `http://localhost:8083/swagger-ui/index.html`
- payment-service: `http://localhost:8084/swagger-ui/index.html`
- order-service: `http://localhost:8085/swagger-ui/index.html`

OpenAPI JSON:
- `http://localhost:{port}/v3/api-docs`

Gateway üstünden route edilen API path’leri:
- `/api/v1/products/**`
- `/api/v1/users/**`
- `/api/v1/carts/**`
- `/api/v1/orders/**`
- `/api/v1/payments/**`

---

## 9) Notlar
> ⚠️ Not: Bu proje frontend ve backend olarak ayrı repolar halinde geliştirilmiştir.  
> Detaylar ve commit geçmişi için aşağıdaki "Kaynak Repolar" bölümüne bakabilirsiniz.
> be: https://github.com/DogacUlgacc/n11-demo-bootcamp
> fe: https://github.com/DogacUlgacc/n11-demo-bootcamp-front
