# Mini E-commerce Microservices System

This project implements a microservice architecture for a mini e-commerce platform (Users and Orders).
The goal of the project is to demonstrate key principles of distributed systems, including **Service Discovery**, **API Gateway**, **Service-to-Service communication**, and **fault tolerance**.

---

## 1. Architecture and Technologies Used

The system is divided into the following modules:

- **`discovery-service`** — Central registry (Service Discovery) — **Eureka Server**
- **`users-service`** — Management of the `User` entity (CRUD) — Spring Boot 3, Spring Data JPA, **H2**
- **`orders-service`** — Management of the `Order` entity, business logic — Spring Boot 3, **OpenFeign**, **Resilience4j**, H2
- **`api-gateway`** — Central entry point, routing, Load Balancing — **Spring Cloud Gateway**, Spring Cloud LoadBalancer

### Communication and Fault Tolerance

* **Service-to-Service:** `orders-service` uses **OpenFeign** to communicate with `users-service` (e.g., `userId` validation).
* **Fault Tolerance:** Feign calls are protected by **Circuit Breaker** and **Retry** mechanisms from the **Resilience4j** library.
* **Load Balancing:** The Gateway uses the **`lb://`** prefix (Spring Cloud LoadBalancer) to distribute requests across multiple running instances of the same service.

---

## 2. Running the Project Locally

### 2.1. Prerequisites

* **Java 17+, Spring Boot 3+**

### 2.2. Build

1. Clone the repository:
```bash
    git clone <REPO_LINK>
    cd e-commerce
```
2. Run the Maven build to download dependencies and compile all modules:
```bash
    mvn clean install
```

### 2.3. Service Startup Order

**1.** `discovery-service` — port `8761` — `DiscoveryServiceApplication` — **must be started first.**
**2.** `users-service` — port `8081` — `UserServiceApplication` — Registers with 8761.
**3.** `orders-service` — port `8082` — `OrdersServiceApplication` — Registers with 8761.
**4.** **`api-gateway`** — port `8080` — `ApiGatewayApplication` — Reads routes from 8761 and registers.

**Status Check:** You can verify registration status on the Eureka Dashboard: [http://localhost:8761](http://localhost:8761)

---

## 3. API Endpoints - Examples

All external calls must go through the **API Gateway** on port `8080`.

| Service | Description | Endpoint | Method | Notes |
|---------|-------------|----------|--------|-------|
| **Users** | Create user | `POST /api/users` | `POST` | Creates a new user. |
| **Users** | Find user | `GET /api/users/{id}` | `GET` | Retrieves user details. |
| **Orders** | Create order | `POST /api/orders` | `POST` | Calls users-service for user validation. |
| **Orders** | Find order | `GET /api/orders/{id}` | `GET` | Retrieves order details. |
| **Orders** | Find order with buyer | `GET /api/orders/{id}/details` | `GET` | Retrieves order details with buyer information. |

### Mapping and Routing

The Gateway uses the **`StripPrefix=1`** filter, meaning the `/api` prefix is stripped before forwarding the request to the service.

* `GET /api/users/1` is transformed into `GET /users/1` and sent to the `USERS-SERVICE`.

---

## 4. Covered Technical Requirements

- **Service Discovery** – Eureka Server
  - One Eureka server + both services registered as clients.
- **API Gateway** – Spring Cloud Gateway
  - Central entry point; routing to services (e.g., `/api/users/**`, `/api/orders/**`).
- **Two Microservices**
  - `users-service`: complete CRUD on `User` (validation, H2).
  - `orders-service`: CRUD on `Order` + one business operation that calls `users-service`.
- **Service-to-Service Communication** – OpenFeign
  - `orders-service` using a Feign client to call `users-service`.
- **Fault Tolerance** – Resilience4j (Circuit Breaker + Retry)
  - Application of CB + Retry on the Feign call (with a short fallback response or clear HTTP errors).
- **Persistence:** H2 (in-memory) for both services.
- **Aggregation Endpoint**
  - `GET /orders/{id}/details` (joining data from both services via a Feign call).
