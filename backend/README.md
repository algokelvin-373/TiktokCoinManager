# Backend

Clean Architecture Go REST API for the TikTok Coin Management MVP.

## Run locally

1. Create PostgreSQL database `tiktok_coin_manager`.
2. Copy `.env.example` to `.env` and adjust `DATABASE_URL`.
3. Apply migration:

```powershell
psql $env:DATABASE_URL -f migrations/001_create_transactions.sql
```

4. Start server:

```powershell
go run ./cmd/server
```

Swagger UI:

```text
http://localhost:8080/swagger/index.html
```

## Verify

```powershell
go test ./...
go build ./cmd/server
```

