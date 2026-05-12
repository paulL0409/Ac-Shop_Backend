# AC Shop – Full-Stack E-commerce Platform

A full-stack e-commerce application with role-based access (Customer, Shop Owner, Admin), built to demonstrate scalable backend design and real-world deployment.

## Tech Stack

- Backend: Java, Spring Boot, MyBatis, MySQL
- Frontend: Vue (Vite), Element Plus, Axios
- Infra: AWS EC2, RDS, Nginx
- Auth & Payments: JWT, Stripe

## Features

- JWT authentication & role-based authorization
- Product & shop management (CRUD)
- Shopping cart & order system
- Stripe payment integration (PaymentIntent + webhook)
- Pagination & search
- Optimized queries with indexing

## Run Locally

### configure environment variables

    DB_URL=jdbc:mysql://<RDS-ENDPOINT>:3306/db01
    DB_USERNAME=your_username
    DB_PASSWORD=your_password
    STRIPE_SECRET_KEY=your_stripe_secret
    STRIPE_WEBHOOK_SECRET=your_webhook_secret
    export $(grep -v '^#' .env | xargs)    
### Let webhook listen to stripe

    stripe listen --forward-to localhost:8080/payments/webhook

### backend
    mvn spring-boot:run

### frontend
    npm install
    npm run dev

### Deployment

- Backend on EC2 (systemd service)
- Nginx reverse proxy (/api → 8080)
- MySQL on RDS
