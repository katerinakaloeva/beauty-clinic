# Beauty Clinic

A Spring Boot appointment-booking application for a beauty clinic. This is a
learning project focused on building a secure backend and gradually moving from
server-rendered MVC pages to a REST API with a Vanilla JavaScript frontend.

## Tech stack

- Java 21, Spring Boot, Gradle
- Spring Security, session-based authentication, and role-based access control
- Spring Data JPA / Hibernate and MySQL
- Thymeleaf for legacy MVC pages during the migration
- HTML, CSS, and Vanilla JavaScript for the new frontend

## Highlights

- Public treatment catalogue loaded from a REST API
- Customer booking flow with available appointment times
- Customer appointment page with status display and cancellation
- `CUSTOMER`, `AESTHETICIAN`, and `ADMIN` roles
- Customer-only booking page and booking API
- CSRF protection for both Thymeleaf forms and JavaScript `fetch` requests
- JSON error responses for REST API failures

## Architecture migration

The project started as a Spring MVC application with Thymeleaf templates. New
features are now added as REST endpoints with static JavaScript pages, while
both approaches use the same service layer.

```text
Thymeleaf MVC controllers ─┐
                           ├─> service layer ─> repositories ─> MySQL
REST API controllers <─ JS ┘
```

This approach keeps existing features working during the migration and avoids
duplicating business logic. The final target is a REST API with a Vanilla
JavaScript frontend, which can later be replaced without changing the API.

## Current REST endpoints

- `GET /api/treatments`
- `GET /api/treatments/{id}`
- `GET /api/bookings/available-times` — `CUSTOMER`
- `POST /api/bookings` — `CUSTOMER`
- `GET /api/bookings/my` — `CUSTOMER`
- `PATCH /api/bookings/{id}/cancel` — `CUSTOMER`

## Next steps

- Expand automated test coverage using the isolated test profile
- Add unit tests for validation and appointment rules
- Add REST API integration tests for security and error handling
- Build staff REST features for appointments and treatment management

## Run locally

Requirements: Java 21, MySQL database `beauty_clinic_db`, and the environment
variables `DB_PASSWORD`, `DEV_ADMIN_PASSWORD`, and `DEV_AESTHETICIAN_PASSWORD`.

```powershell
.\gradlew.bat bootRun --args="--spring.profiles.active=dev"
```

Open [http://localhost:8080](http://localhost:8080).
