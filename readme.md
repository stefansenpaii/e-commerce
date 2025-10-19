# Mini E-commerce Mikroservisni Sistem

Ovaj projekat implementira mikroservisnu arhitekturu za mini e-commerce (Korisnici i Narudžbine).

Cilj projekta je demonstracija ključnih principa distribuiranih sistema, uključujući **Service Discovery**, **API Gateway**, **Service-to-Service komunikaciju** i **otpornost na greške**.

---

## 1. Arhitektura i Korišćene Tehnologije

Sistem je podeljen na sledeće module:

 - **`discovery-service`** Centralni registar (Service Discovery) **Eureka Server**

- **`users-service`** Upravljanje entitetom `User` (CRUD) Spring Boot 3, Spring Data JPA, **H2** 

- **`orders-service`**  Upravljanje entitetom `Order`, poslovna logika  Spring Boot 3, **OpenFeign**, **Resilience4j**, H2 

- **`api-gateway`** Centralna ulazna tačka, rutiranje, Load Balancing | **Spring Cloud Gateway**, Spring Cloud LoadBalancer 

### Komunikacija i Otpornost

* **Service-to-Service:** `orders-service` koristi **OpenFeign** za komunikaciju sa `users-service` (npr. validacija `userId`).
* **Otpornost:** Feign pozivi zaštićeni su mehanizmima **Circuit Breaker** i **Retry** iz **Resilience4j** biblioteke.
* **Load Balancing:** Gateway koristi **`lb://`** prefiks (Spring Cloud LoadBalancer) za distribuciju zahteva između više pokrenutih instanci istog servisa.

---

## 2. Lokalno Pokretanje Projekta

### 2.1. Preduslovi

* **Java 17+, Spring Boot 3+**

### 2.2. Sastavljanje (Build)

1.  Klonirajte repozitorijum:
    ```bash
    git clone <LINK_DO_REPO>
    cd e-commerce
    ```
2.  Pokrenite Maven build da bi se preuzele zavisnosti i sastavili svi moduli:
    ```bash
    mvn clean install
    ```

### 2.3. Redosled Pokretanja Servisa 


 **1.**  `discovery-service`  `8761`  `DiscoveryServiceApplication`  **mora biti prvi.**

 **2.**  `users-service`  `8081`  `UserServiceApplication`  Registruje se na 8761.

 **3.**  `orders-service`  `8082`  `OrdersServiceApplication`  Registruje se na 8761.

 **4.**  **`api-gateway`**  `8080`  `ApiGatewayApplication`   Čita rute sa 8761 i registruje se.

**Provera Statusa:** Status registracije možete proveriti na Eureka Dashboardu: [http://localhost:8761](http://localhost:8761)

---

## 3. API Endpoints - Primeri

Svi eksterni pozivi moraju ići preko **API Gateway-a** na portu `8080`.

 **Users**  Kreiraj korisnika  `POST /api/users`  `POST`  Kreiranje novog korisnika. 

 **Users**  Pronađi korisnika  `GET /api/users/{id}`  `GET`  Dohvatanje detalja korisnika. 

 **Orders**  Kreiraj narudžbinu `POST /api/orders`  `POST`  Poziv ka users-service za validaciju korisnika. 

 **Orders**  Pronađi narudžbinu  `GET /api/orders/{id}`  `GET`  Dohvatanje detalja narudžbine. 

 **Orders**  Pronađi narudžbinu sa kupcem  `GET /api/orders/{id}/details`  `GET`  Dohvatanje detalja narudžbine sa podacima kupca. 

### Mapiranje i Rutiranje

Gateway koristi **`StripPrefix=1`** filter, što znači da se `/api` prefiks uklanja pre slanja zahteva servisu.

* `GET /api/users/1` se pretvara u `GET /users/1` i šalje se servisu `USERS-SERVICE`.

---
## 4. Obuhvaćeni tehnički zahtevi
- Service Discovery – Eureka Server

   - Jedan Eureka server + oba servisa registrovana kao klijenti.

- API Gateway – Spring Cloud Gateway

  - Centralna ulazna tačka; rutiranje ka servisima (npr. /api/users/**, /api/orders/**).

- Dva mikroservisa
    - users-service: kompletan CRUD nad User (validacija, H2).
    - orders-service: CRUD nad Order + jedna business operacija koja poziva users-service

- Komunikacija servis–servis – OpenFeign
    - orders-service koji koristi Feign klijent ka users-service.

- Otpornost – Resilience4j (Circuit Breaker + Retry)
  -  Primena CB + Retry na Feign pozivu (sa kratkim fallback odgovorom ili jasnim HTTP
greškama).

- Persistencija: H2 (in-memory) za oba servisa.

- Agregacioni endpoint
  - GET /orders/{id}/details (spajanje podataka iz oba servisa preko Feign poziva).