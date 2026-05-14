# AC Shop — Full-Stack E-commerce Platform

A production-deployed e-commerce platform featuring role-based access control, async payment processing, and cloud-native infrastructure. Built to demonstrate scalable backend design and end-to-end deployment.

**Live demo:** [acshop.paullin.dev](https://acshop.paullin.dev) &nbsp;|&nbsp; **API docs:** [/swagger-ui.html](https://acshop.paullin.dev/swagger-ui.html)

---

## Architecture

```
                    ┌─────────────────────────────────┐
                    │         Vue 3 Frontend          │
                    │  (Vite · Element Plus · Axios)  │
                    └──────────────┬──────────────────┘
                                   │ HTTPS
                    ┌──────────────▼──────────────────┐
                    │         Nginx (EC2)             │
                    │   /api  →  :8080  │  /  →  dist │
                    └──────────────┬──────────────────┘
                                   │
              ┌────────────────────▼──────────────────────┐
              │         Spring Boot Backend (EC2)          │
              │  REST API · JWT Auth · Stripe Webhook      │
              └──┬──────────┬──────────┬──────────────────┘
                 │          │          │
        ┌────────▼──┐  ┌────▼───┐  ┌──▼──────┐
        │  MySQL    │  │ Redis  │  │  Kafka  │
        │ (AWS RDS) │  │ Cache  │  │ Events  │
        └───────────┘  │ +Rate  │  └──┬──────┘
                       │ Limit  │     │ payment results
                       └────────┘  ┌──▼──────┐
                                   │Consumer │
                    ┌──────────────┴─────────┤
                    │        AWS S3          │
                    │   (product images)     │
                    └────────────────────────┘
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| **Backend** | Java 17, Spring Boot 3.5, MyBatis, PageHelper |
| **Frontend** | Vue 3, Vite, Element Plus, Vue Router |
| **Database** | MySQL 8 (AWS RDS) |
| **Cache / Rate Limiting** | Redis, Bucket4j |
| **Messaging** | Apache Kafka |
| **Auth** | JWT (jjwt 0.12), Spring Security Crypto (BCrypt) |
| **Payments** | Stripe (PaymentIntent + Webhooks) |
| **Storage** | AWS S3 |
| **Infra** | AWS EC2, Nginx, Docker, systemd |
| **API Docs** | SpringDoc OpenAPI (Swagger UI) |

---

## Features

- **Role-based access control** — three roles (Customer, Shop Owner, Admin) with per-endpoint authorization enforced via JWT interceptors
- **Product & shop management** — full CRUD with image uploads to S3; sellers manage their own inventory
- **Shopping cart & orders** — persistent cart, order lifecycle management
- **Stripe payments** — PaymentIntent flow with webhook verification; payment results processed asynchronously via Kafka
- **Rate limiting** — per-user request throttling using Bucket4j backed by Redis
- **Pagination & search** — server-side pagination via PageHelper; full-text product search
- **Swagger UI** — interactive API documentation at `/swagger-ui.html`

---

## Local Development

### Prerequisites

- Java 17+, Maven
- Node.js 18+
- MySQL 8, Redis, Kafka (or use Docker Compose)
- Stripe CLI

### 1. Configure environment variables

Create a `.env` file in the project root:

```env
DB_URL=jdbc:mysql://localhost:3306/acshop
DB_USERNAME=your_username
DB_PASSWORD=your_password

JWT_SECRET=your_jwt_secret

STRIPE_SECRET_KEY=sk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...

AWS_ACCESS_KEY=your_access_key
AWS_SECRET_KEY=your_secret_key
AWS_S3_BUCKET=your_bucket
AWS_S3_REGION=us-east-1

REDIS_HOST=localhost
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

Load them into your shell:

```bash
export $(grep -v '^#' .env | xargs)
```

### 2. Forward Stripe webhooks

```bash
stripe listen --forward-to localhost:8080/payments/webhook
```

### 3. Run the backend

```bash
mvn spring-boot:run
```

### 4. Run the frontend

```bash
# from the acShop-website directory
npm install
npm run dev
```

API docs available at `http://localhost:8080/swagger-ui.html`.

---

## Deployment

| Component | Setup |
|---|---|
| **Backend** | EC2 instance running as a `systemd` service |
| **Frontend** | Static `dist/` served by Nginx |
| **Reverse proxy** | Nginx routes `/api/*` → `:8080`, everything else → Vue build |
| **Database** | AWS RDS (MySQL 8) |
| **Images** | AWS S3 with public read policy |
| **Container** | `Dockerfile` included for containerized deployments |

---

## Project Structure

```
src/main/java/com/acShop/
├── controller/      # REST endpoints (Cart, Order, Payment, Product, Shop, User)
├── service/         # Business logic
├── mapper/          # MyBatis SQL mappers
├── pojo/            # Domain models & DTOs
├── kafka/           # Payment event producer & consumer
├── config/          # Spring config (Redis, S3, Swagger, CORS)
├── interceptor/     # JWT auth interceptor
├── exception/       # Global exception handling
└── utils/           # JWT utilities, helpers
```
