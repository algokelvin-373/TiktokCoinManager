# Product Requirements Document (PRD)
## TikTok Coin Management App

**Document Version:** 1.0  
**Project Type:** Study / Portfolio Project  
**Platform:** Android Native + REST API  
**Android:** Kotlin, MVVM, Clean Architecture  
**Backend:** Golang, Clean Architecture  
**API Documentation:** Swagger / OpenAPI  
**Status:** Draft for Development

---

## 1. Product Overview

TikTok Coin Management App adalah aplikasi pencatatan sederhana untuk membantu pengguna mencatat pergerakan TikTok Coins dan biaya yang dikeluarkan untuk melakukan top up.

Konsep aplikasi dibuat menyerupai aplikasi Notes: pengguna dapat melihat daftar transaksi, menambahkan data, mengubah data, melihat detail, dan menghapus data.

Walaupun fitur dibuat sederhana, aplikasi harus memiliki UI/UX yang modern, mudah dipahami, dan menarik secara visual. Project ini juga digunakan sebagai studi implementasi **Clean Code**, **MVVM**, dan **Clean Architecture** pada Android Native serta **Clean Architecture** pada backend Golang.

---

## 2. Product Goals

Tujuan utama project:

1. Membuat aplikasi pencatatan TikTok Coins yang sederhana dan cepat digunakan.
2. Mencatat transaksi **Debit Coins** dan **Credit Coins**.
3. Mencatat biaya uang yang dikeluarkan ketika melakukan top up TikTok Coins.
4. Menampilkan saldo TikTok Coins berdasarkan seluruh transaksi.
5. Menampilkan total biaya top up.
6. Menyediakan fungsi CRUD seperti aplikasi Notes.
7. Menerapkan MVVM dan Clean Architecture pada Android.
8. Menerapkan Clean Architecture pada backend Golang.
9. Menyediakan Swagger UI agar API dapat diuji tanpa Postman.
10. Menjadi project portfolio yang mudah dikembangkan pada fase berikutnya.

---

## 3. Non-Goals / Out of Scope MVP

Fitur berikut belum menjadi prioritas MVP:

- Integrasi langsung dengan TikTok API.
- Pembelian TikTok Coins dari aplikasi.
- Payment Gateway.
- Sinkronisasi transaksi otomatis dari TikTok.
- Multi-currency conversion.
- Laporan akuntansi kompleks.
- Double-entry accounting.
- Multi-user / role management.
- Social login.
- Notifikasi transaksi.
- Export PDF / Excel.
- Cloud backup khusus selain penyimpanan melalui backend.

Fitur tersebut dapat dipertimbangkan pada versi berikutnya.

---

## 4. Target User

### Primary User

Pengguna yang rutin membeli, menerima, atau menggunakan TikTok Coins dan ingin memiliki pencatatan sederhana mengenai:

- Coins masuk.
- Coins keluar.
- Saldo Coins.
- Pengeluaran uang untuk top up.
- Riwayat aktivitas Coins.

### User Persona Sederhana

**Nama:** Personal TikTok Coin User  
**Kebutuhan utama:** Mengetahui jumlah Coins yang masuk, keluar, saldo saat ini, dan total uang yang sudah digunakan untuk top up.  
**Masalah:** Pencatatan manual mudah hilang dan sulit melihat histori atau total pengeluaran secara cepat.

---

## 5. MVP Scope

MVP memiliki 4 kemampuan utama:

1. **Dashboard**
2. **Transaction List**
3. **Add / Edit Transaction**
4. **Transaction Detail & Delete**

---

## 6. Core Business Concept

Aplikasi memiliki dua nilai utama:

### 6.1 TikTok Coins

Pergerakan Coins dicatat sebagai:

- **CREDIT** → Coins masuk.
- **DEBIT** → Coins keluar.

Rumus saldo:

```text
Current Coin Balance = Total Credit Coins - Total Debit Coins
```

### 6.2 Top Up Expense

Ketika pengguna membeli TikTok Coins, pengguna dapat mencatat biaya yang dikeluarkan dalam Rupiah.

Contoh:

```text
Transaction Type : CREDIT
Coin Amount      : 1,000 Coins
Top Up Expense   : Rp150.000
Note             : Top up TikTok Coins
```

Biaya top up tidak mengurangi saldo Coins. Nilai tersebut digunakan untuk menghitung total pengeluaran uang.

```text
Total Top Up Expense = Sum of all Top Up Expense
```

Field biaya top up bersifat opsional karena tidak semua Credit Coins harus berasal dari pembelian.

---

## 7. Functional Requirements

### FR-01 — View Dashboard

User dapat melihat ringkasan:

- Current Coin Balance.
- Total Credit Coins.
- Total Debit Coins.
- Total Top Up Expense.
- Recent Transactions.

### FR-02 — View Transaction List

User dapat melihat seluruh transaksi.

Setiap item minimal menampilkan:

- Transaction type.
- Coin amount.
- Transaction title.
- Date.
- Top up expense jika tersedia.

Urutan default:

```text
Newest → Oldest
```

### FR-03 — Add Transaction

User dapat membuat transaksi baru.

Mandatory fields:

- Transaction Type.
- Coin Amount.
- Date.

Optional fields:

- Title.
- Top Up Expense.
- Description / Note.

### FR-04 — Edit Transaction

User dapat mengubah transaksi yang sudah tersimpan.

### FR-05 — View Transaction Detail

User dapat melihat detail transaksi secara lengkap.

### FR-06 — Delete Transaction

User dapat menghapus transaksi.

Sebelum menghapus data, aplikasi harus menampilkan confirmation dialog.

Contoh:

```text
Delete this transaction?
This action cannot be undone.
```

### FR-07 — Automatic Summary Calculation

Setelah Add, Edit, atau Delete:

- Coin Balance harus diperbarui.
- Total Credit harus diperbarui.
- Total Debit harus diperbarui.
- Total Top Up Expense harus diperbarui.

### FR-08 — Empty State

Jika belum ada transaksi:

```text
No transaction yet.
Start by adding your first TikTok Coin transaction.
```

Tampilkan CTA:

```text
+ Add Transaction
```

### FR-09 — Error Handling

Aplikasi harus menangani:

- API unavailable.
- Timeout.
- Invalid request.
- Server error.
- Empty data.
- Failed create/update/delete.

User harus menerima pesan yang mudah dipahami dan opsi Retry jika relevan.

---

## 8. Transaction Data Model

### Transaction

| Field | Type | Required | Description |
|---|---|---:|---|
| id | UUID/String | Yes | Unique transaction ID |
| title | String | No | Judul transaksi |
| transaction_type | Enum | Yes | CREDIT / DEBIT |
| coin_amount | Integer | Yes | Jumlah TikTok Coins |
| topup_expense | Decimal | No | Biaya pembelian Coins |
| currency | String | Yes | Default: IDR |
| note | String/Text | No | Catatan tambahan |
| transaction_date | DateTime | Yes | Waktu transaksi |
| created_at | DateTime | Yes | Waktu data dibuat |
| updated_at | DateTime | Yes | Waktu terakhir diubah |

### Validation

- `coin_amount > 0`
- `topup_expense >= 0`
- `transaction_type` hanya `CREDIT` atau `DEBIT`
- `currency` default `IDR`
- `transaction_date` wajib diisi

---

## 9. Main Screens

### 9.1 Dashboard Screen

Komponen:

- App Header.
- Coin Balance Card.
- Credit Summary.
- Debit Summary.
- Total Top Up Expense.
- Recent Transactions.
- Floating Action Button `+`.

Visual priority:

1. Current Balance.
2. Add Transaction.
3. Recent activity.

### 9.2 Transaction List Screen

Komponen:

- Page title.
- List transaksi.
- Credit / Debit indicator.
- Date.
- Amount.
- Top up expense.
- Floating Action Button.

Future-ready filter:

- All.
- Credit.
- Debit.

Filter dapat disiapkan pada desain tetapi implementasinya bersifat optional pada MVP.

### 9.3 Add Transaction Screen

Form:

```text
Transaction Type
[ Credit ] [ Debit ]

Coin Amount
[____________]

Top Up Expense
Rp [____________]

Title
[____________]

Date
[____________]

Note
[________________________]
[________________________]

[ Save Transaction ]
```

### 9.4 Edit Transaction Screen

Menggunakan form yang sama dengan Add Transaction dengan data lama sebagai initial value.

CTA:

```text
Save Changes
```

### 9.5 Transaction Detail Screen

Menampilkan:

- Credit / Debit.
- Coin amount.
- Top up expense.
- Title.
- Note.
- Transaction date.
- Created date.
- Updated date.

Action:

```text
Edit
Delete
```

---

## 10. UX Principles

UI/UX harus mengikuti prinsip:

### Simple

User dapat membuat transaksi maksimal dalam beberapa langkah.

### Clear

Credit dan Debit harus mudah dibedakan secara visual.

### Focused

Tidak menampilkan terlalu banyak informasi dalam satu layar.

### Familiar

Interaksi mengikuti pola aplikasi Notes / Expense Tracker.

### Fast

Add Transaction menjadi action utama dan selalu mudah ditemukan.

### Safe

Delete membutuhkan confirmation dialog.

---

## 11. Suggested UI Style

Arah visual yang disarankan:

- Modern financial dashboard.
- Rounded cards.
- Clean typography.
- Spacious layout.
- Minimal iconography.
- Strong visual hierarchy.
- Dark Mode optional untuk MVP.
- TikTok-inspired accent dapat digunakan secara terbatas, tetapi aplikasi tidak boleh terlihat seperti aplikasi resmi TikTok.

Status transaksi harus tetap dapat dibedakan tidak hanya berdasarkan warna, tetapi juga menggunakan label atau icon:

```text
↑ CREDIT
↓ DEBIT
```

---

## 12. Android Technical Requirements

### Technology

- Kotlin.
- Android Native.
- MVVM.
- Clean Architecture.
- Coroutines.
- Flow / StateFlow.
- Retrofit.
- OkHttp.
- Dependency Injection: Hilt.
- JSON Serialization: Kotlin Serialization / Moshi.
- ViewModel.
- Navigation Component atau Compose Navigation.
- Jetpack Compose direkomendasikan untuk UI modern.

### Android Architecture

```text
presentation
    ├── screen
    ├── component
    ├── viewmodel
    └── state

domain
    ├── model
    ├── repository
    └── usecase

data
    ├── remote
    ├── dto
    ├── mapper
    └── repository
```

Dependency direction:

```text
Presentation → Domain ← Data
```

Domain layer tidak boleh bergantung pada Android Framework atau implementation detail dari Data Layer.

---

## 13. Android Use Cases

Minimum use cases:

```text
GetTransactionsUseCase
GetTransactionDetailUseCase
CreateTransactionUseCase
UpdateTransactionUseCase
DeleteTransactionUseCase
GetDashboardSummaryUseCase
```

Repository contract berada pada Domain Layer.

Contoh:

```text
TransactionRepository
```

Implementation berada pada Data Layer.

---

## 14. Backend Technical Requirements

### Technology

- Golang.
- REST API.
- Clean Architecture.
- PostgreSQL direkomendasikan.
- Swagger / OpenAPI.
- Docker optional.
- Environment configuration menggunakan `.env`.

### Backend Layers

```text
cmd/
internal/
    domain/
    usecase/
    repository/
    delivery/
        http/
    infrastructure/
        database/
        config/
docs/
```

Alternative naming dapat digunakan selama dependency rule tetap konsisten.

Dependency utama:

```text
HTTP Handler
    ↓
Use Case
    ↓
Repository Interface
    ↓
Repository Implementation
    ↓
Database
```

Domain dan Use Case tidak boleh bergantung langsung pada HTTP framework atau database driver.

---

## 15. REST API Requirements

Base path:

```text
/api/v1
```

### Transaction APIs

#### Get Transactions

```http
GET /api/v1/transactions
```

#### Get Transaction Detail

```http
GET /api/v1/transactions/{id}
```

#### Create Transaction

```http
POST /api/v1/transactions
```

#### Update Transaction

```http
PUT /api/v1/transactions/{id}
```

#### Delete Transaction

```http
DELETE /api/v1/transactions/{id}
```

### Dashboard Summary

```http
GET /api/v1/dashboard/summary
```

Example response:

```json
{
  "coin_balance": 7500,
  "total_credit": 10000,
  "total_debit": 2500,
  "total_topup_expense": 1500000
}
```

---

## 16. Standard API Response

Success:

```json
{
  "success": true,
  "message": "Transaction retrieved successfully",
  "data": {}
}
```

Error:

```json
{
  "success": false,
  "message": "Invalid transaction data",
  "errors": []
}
```

HTTP status harus digunakan secara sesuai:

```text
200 OK
201 Created
400 Bad Request
404 Not Found
422 Unprocessable Entity
500 Internal Server Error
```

---

## 17. Swagger / OpenAPI Requirements

Backend wajib menyediakan Swagger UI.

Target development endpoint:

```text
http://localhost:<port>/swagger/index.html
```

Swagger harus mendokumentasikan:

- Endpoint.
- HTTP Method.
- Request body.
- Request parameter.
- Response schema.
- HTTP response code.
- Example request.
- Example response.

Developer harus dapat melakukan:

```text
Try it out → Execute
```

langsung dari Swagger UI tanpa membutuhkan Postman untuk basic API testing.

---

## 18. Database

### Table: transactions

Suggested schema:

```text
id
title
transaction_type
coin_amount
topup_expense
currency
note
transaction_date
created_at
updated_at
```

Index minimum:

```text
PRIMARY KEY (id)
INDEX (transaction_date)
INDEX (transaction_type)
```

---

## 19. Clean Code Requirements

Android dan Backend harus mengikuti standar:

- Naming jelas dan konsisten.
- Function memiliki satu tanggung jawab utama.
- Hindari duplicated logic.
- Hindari God Class.
- Hindari hardcoded configuration.
- Gunakan mapper untuk konversi DTO ↔ Domain Model.
- Error handling terpusat.
- Business rule ditempatkan pada Domain / Use Case.
- Repository hanya bertanggung jawab terhadap akses data.
- Presentation tidak boleh mengandung business logic utama.
- HTTP handler backend tidak boleh mengandung business logic utama.

---

## 20. State Management Android

Setiap screen yang membutuhkan data remote menggunakan UI State.

Contoh:

```text
Idle
Loading
Success
Empty
Error
```

ViewModel bertanggung jawab:

- Memanggil Use Case.
- Mengelola UI State.
- Menerima UI Event.
- Tidak mengakses Retrofit secara langsung.

---

## 21. API Error Mapping

Backend error harus dipetakan ke domain error yang dapat dipahami aplikasi.

Contoh:

```text
400 → Validation Error
404 → Data Not Found
500 → Server Error
Network Failure → Connection Error
```

Android menampilkan pesan yang relevan tanpa mengekspos raw stack trace atau technical error ke user.

---

## 22. Security Baseline

Walaupun project bersifat study project:

- Database credential tidak boleh hardcoded.
- `.env` tidak boleh masuk Git repository.
- Android tidak menyimpan secret backend.
- Backend melakukan request validation.
- SQL query harus aman dari injection.
- Swagger dapat dibatasi atau dimatikan pada production jika project berkembang menjadi aplikasi publik.

Authentication belum diwajibkan untuk MVP.

---

## 23. Testing Requirements

### Android

Minimum:

- Unit test untuk Use Case.
- Unit test untuk ViewModel.
- Repository test menggunakan fake/mock source.
- Validation test.

### Backend

Minimum:

- Use Case unit test.
- Repository test.
- HTTP handler test.
- Validation test.
- CRUD API integration test jika memungkinkan.

Focus utama testing:

```text
Create
Read
Update
Delete
Balance Calculation
Expense Calculation
Validation
Error Handling
```

---

## 24. Acceptance Criteria

MVP dianggap selesai apabila:

1. User dapat melihat daftar transaksi.
2. User dapat menambahkan transaksi Credit.
3. User dapat menambahkan transaksi Debit.
4. User dapat mengisi biaya top up.
5. User dapat melihat detail transaksi.
6. User dapat mengubah transaksi.
7. User dapat menghapus transaksi.
8. Coin balance dihitung dengan benar.
9. Total credit dihitung dengan benar.
10. Total debit dihitung dengan benar.
11. Total biaya top up dihitung dengan benar.
12. Dashboard otomatis berubah setelah CRUD.
13. Android menggunakan MVVM + Clean Architecture.
14. Backend menggunakan Clean Architecture.
15. API dapat digunakan oleh Android.
16. Swagger UI tersedia dan seluruh endpoint MVP dapat diuji dari browser.
17. Error dan empty state tersedia.
18. Source code memenuhi baseline Clean Code project.

---

## 25. Suggested Development Phases

### Phase 1 — Foundation

- Setup Android project.
- Setup Golang project.
- Setup Clean Architecture.
- Setup database.
- Setup Swagger.
- Setup networking Android.

### Phase 2 — Backend CRUD

- Transaction model.
- Repository.
- Use Case.
- REST handler.
- CRUD API.
- Dashboard summary.
- Swagger documentation.
- Backend tests.

### Phase 3 — Android CRUD

- Dashboard.
- Transaction list.
- Add transaction.
- Detail transaction.
- Edit transaction.
- Delete transaction.
- API integration.

### Phase 4 — UX Polish

- Loading state.
- Empty state.
- Error state.
- Delete confirmation.
- Form validation.
- Animation / transition ringan.
- Responsive layout.
- Dark Mode jika diperlukan.

### Phase 5 — Quality

- Refactor.
- Unit test.
- Integration test.
- Clean Code review.
- Architecture review.
- README documentation.

---

## 26. Future Roadmap

Potential Version 2:

- Transaction search.
- Date filter.
- Credit / Debit filter.
- Monthly summary.
- Spending chart.
- Top up history.
- Average price per Coin.
- Local Room cache.
- Offline-first mode.
- Authentication.
- Multiple user accounts.
- Export CSV / Excel.
- Backup.
- Dark Mode.
- Biometric lock.

Potential advanced analytics:

```text
Total Coins Purchased
Total Coins Used
Total Money Spent
Average Top Up Cost
Average Cost per Coin
Monthly Coin Usage
```

---

## 27. Definition of Done

Sebuah feature dianggap selesai ketika:

- Requirement terpenuhi.
- UI sesuai desain.
- API berhasil diintegrasikan.
- Loading / Empty / Error state tersedia.
- Validation berjalan.
- Tidak terdapat business logic utama pada UI atau HTTP Handler.
- Unit test utama lulus.
- Swagger sudah diperbarui jika endpoint berubah.
- Tidak terdapat hardcoded secret.
- Code sudah melalui Clean Code review.

---

## 28. Final MVP Product Structure

```text
TikTok Coin Management

Dashboard
├── Coin Balance
├── Total Credit
├── Total Debit
├── Total Top Up Expense
└── Recent Transactions

Transactions
├── Transaction List
├── Add
├── Detail
├── Edit
└── Delete

Backend
├── REST API
├── Transaction CRUD
├── Dashboard Summary
├── PostgreSQL
└── Swagger UI
```

---

## 29. Product Principle

> Keep the product simple like Notes, structure the code like a production application.

Fokus utama project bukan memperbanyak fitur, tetapi menunjukkan bahwa sebuah aplikasi sederhana tetap dapat dibangun dengan:

- UX yang baik.
- Clean Code.
- Clean Architecture.
- Separation of Concerns.
- Testable business logic.
- Dokumentasi API yang jelas.
- Struktur project yang scalable.
