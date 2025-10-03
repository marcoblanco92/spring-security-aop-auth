# spring-security-aop-auth

**spring-security-aop-auth** è un progetto dimostrativo sviluppato con **Spring Boot**, **Spring Security**, **Spring Data**, **PostgreSQL** e **OAuth2**, pensato per applicare le best practice nella gestione di autenticazione, autorizzazione e sicurezza di API REST.

---

## Caratteristiche principali

- **Gestione utenti e ruoli**
  - Persistenza su PostgreSQL con schema `auth`.
  - CRUD utenti con controllo di esistenza.
  - Gestione ruoli tramite entità dedicate.

- **Autenticazione e sicurezza**
  - Login standard con username/password.
  - Integrazione OAuth2 (Google come provider di esempio).
  - Filtri di sicurezza multipli tramite `SecurityFilterChain`.
  - Gestione eccezioni centralizzata (`UserAlreadyExistsException`, `InvalidAuthorizationHeaderException`).
  - Logout, change password e reset password sicuri.

- **Monitoring e logging**
  - Integrazione con **Micrometer + Prometheus**.
  - Dashboard Grafana (importando JSON).
  - AOP per audit e logging centralizzato (`AuditAspect`, `LoggingAspect`).

- **Documentazione API**
  - Swagger/OpenAPI per tutti gli endpoint REST.
  - Supporto per provider OAuth2 come parametro negli endpoint.

- **Altri componenti**
  - Kafka per invio email (`KafkaEmailProducer`).
  - Filtri JWT per autenticazione e refresh (`JwtAuthenticationFilter`, `JwtRefreshFilter`).
  - Gestione cache e utility privacy (`CacheUtil`, `PrivacyUtils`).

---

## Tecnologie utilizzate

- Java 17+
- Spring Boot 3+
- Spring Security 6+
- Spring Data
- PostgreSQL
- OAuth2 Client (Google)
- Micrometer & Prometheus
- Swagger/OpenAPI
- Docker (per database e monitoring)
- Spring Web Starter
- Kafka (per invio eventi email)

---

## Struttura progetto

```text
src/main/java/com/marbl/spring_security_aop_auth/
├── annotation/       # Annotations custom (es. ExactlyOneField)
├── aspect/           # Logging e audit AOP
├── configuration/    # Configurazioni (Security, OpenAPI, App)
├── controller/       # Controller REST API
│   ├── auth/
│   └── user/
├── dto/              # DTO per auth, user, audit
├── entity/           # Entity JPA (user, role, token, audit)
├── exception/        # Eccezioni custom e handler globali
├── mapper/           # Mapper per DTO e entity
├── service/          # Logica business (auth, user, audit, blacklist)
├── component/        # Componenti aggiuntivi (OAuth2, Kafka, filter)
├── utils/            # Utility (JWT, cache, privacy)
├── model/            # Model per token, response, Kafka
└── SpringSecurityAopAuthApplication.java
```
---

## Configurazione

- Il progetto utilizza **application.yml**.
- Configurare PostgreSQL nello yml (schema `auth`).
- Configurare OAuth2, logging, Kafka e server tramite yml.

---

## Esecuzione

### Opzione 1: Locale tramite IntelliJ
1. Avviare PostgreSQL (locale o via Docker).
2. Aprire il progetto in **IntelliJ IDEA**.
3. Avviare il microservizio tramite la configurazione di run generata da IntelliJ.
4. Accedere alle API via Swagger: `http://localhost:8080/swagger-ui.html`.

### Opzione 2: Docker Compose
- **Full stack** (microservizio + Prometheus + Grafana):
  ```bash
  docker-compose up --build

- **Light**
  ```bash
  docker-compose -f docker-compose-light.yml up --build
