# TiktokCoinManager

TikTok Coin tracker foundation built from `PRD_TikTok_Coin_Management.md`.

## Implementation Plan

1. Backend foundation: Clean Architecture layers, PostgreSQL migration, REST routes, centralized response/error handling, Swagger UI.
2. Android foundation: MVVM + Clean Architecture packages, Retrofit networking, repository/use case contracts, Hilt dependency injection, UI state baseline.
3. MVP build-out next: implement the PRD screens and connect CRUD flows without adding features outside the MVP.

## Project Structure

```text
backend/
  cmd/server/                 # API entry point
  internal/domain/            # Entities, repository contracts, domain errors
  internal/usecase/           # Application business rules
  internal/delivery/http/     # REST routing, handlers, Swagger assets
  internal/infrastructure/    # Config, database, PostgreSQL repository
  migrations/                 # PostgreSQL schema
  docs/                       # OpenAPI reference

app/
  src/main/java/.../core/         # Result and app error models
  src/main/java/.../domain/       # Models, repository contract, use cases
  src/main/java/.../data/         # DTOs, Retrofit API, mapper, repository impl
  src/main/java/.../di/           # Hilt modules
  src/main/java/.../presentation/ # Compose screen, state, ViewModel
```

## Backend Setup

```powershell
Copy-Item .env.example .env
# Edit POSTGRES_USER and POSTGRES_PASSWORD in .env.
docker compose up -d postgres
cd backend
Copy-Item .env.example .env
# Edit DATABASE_URL in .env for your PostgreSQL instance.
psql $env:DATABASE_URL -f migrations/001_create_transactions.sql
go run ./cmd/server
```

Swagger UI:

```text
http://localhost:8080/swagger/index.html
```

Verify backend:

```powershell
cd backend
go test ./...
go build -o NUL ./cmd/server
```

## Android Setup

Requirements:

- JDK 17
- Android SDK with `ANDROID_HOME` or `ANDROID_SDK_ROOT`

Build:

```powershell
.\gradlew.bat :app:assembleDebug
```

The emulator base URL is configured as `http://10.0.2.2:8080/` through `BuildConfig.BASE_URL`.

## Remaining MVP Work

- Build the Add/Edit/Detail/Delete screens described in the PRD.
- Wire navigation and delete confirmation.
- Add focused Android ViewModel and validation tests.
- Add backend integration tests against a test PostgreSQL database.
