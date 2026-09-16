package http

import (
	"context"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
	"time"

	"tiktokcoinmanager/backend/internal/domain"
	"tiktokcoinmanager/backend/internal/usecase"
)

func TestRoutesRegisterDashboardSummary(t *testing.T) {
	router := NewRouter(usecase.NewTransactionUseCase(&testRepository{}), true)

	request := httptest.NewRequest(http.MethodGet, "/api/v1/dashboard/summary", nil)
	response := httptest.NewRecorder()

	router.ServeHTTP(response, request)

	if response.Code != http.StatusOK {
		t.Fatalf("expected status 200, got %d", response.Code)
	}
}

func TestRoutesRegisterSwagger(t *testing.T) {
	router := NewRouter(usecase.NewTransactionUseCase(&testRepository{}), true)

	request := httptest.NewRequest(http.MethodGet, "/swagger/index.html", nil)
	response := httptest.NewRecorder()

	router.ServeHTTP(response, request)

	if response.Code != http.StatusOK {
		t.Fatalf("expected status 200, got %d", response.Code)
	}
}

func TestRoutesRegisterMVPTransactionEndpoints(t *testing.T) {
	router := NewRouter(usecase.NewTransactionUseCase(&testRepository{}), true)
	body := `{"transaction_type":"CREDIT","coin_amount":1000,"currency":"IDR","transaction_date":"2026-09-16T09:00:00Z"}`
	tests := []struct {
		name   string
		method string
		path   string
		body   string
	}{
		{name: "list", method: http.MethodGet, path: "/api/v1/transactions"},
		{name: "detail", method: http.MethodGet, path: "/api/v1/transactions/sample-id"},
		{name: "create", method: http.MethodPost, path: "/api/v1/transactions", body: body},
		{name: "update", method: http.MethodPut, path: "/api/v1/transactions/sample-id", body: body},
		{name: "delete", method: http.MethodDelete, path: "/api/v1/transactions/sample-id"},
		{name: "summary", method: http.MethodGet, path: "/api/v1/dashboard/summary"},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			request := httptest.NewRequest(tt.method, tt.path, strings.NewReader(tt.body))
			response := httptest.NewRecorder()

			router.ServeHTTP(response, request)

			if response.Code == http.StatusNotFound && response.Header().Get("Content-Type") != "application/json" {
				t.Fatalf("route is not registered: %s %s", tt.method, tt.path)
			}
			if response.Code == http.StatusMethodNotAllowed {
				t.Fatalf("method is not registered: %s %s", tt.method, tt.path)
			}
		})
	}
}

type testRepository struct{}

func (t *testRepository) List(context.Context) ([]domain.Transaction, error) {
	return nil, nil
}

func (t *testRepository) GetByID(context.Context, string) (domain.Transaction, error) {
	return validTransaction(), nil
}

func (t *testRepository) Create(_ context.Context, transaction domain.Transaction) (domain.Transaction, error) {
	return transaction, nil
}

func (t *testRepository) Update(_ context.Context, transaction domain.Transaction) (domain.Transaction, error) {
	return transaction, nil
}

func (t *testRepository) Delete(context.Context, string) error {
	return nil
}

func (t *testRepository) Summary(context.Context) (domain.DashboardSummary, error) {
	return domain.DashboardSummary{
		CoinBalance:       7500,
		TotalCredit:       10000,
		TotalDebit:        2500,
		TotalTopupExpense: 150000,
	}, nil
}

func validTransaction() domain.Transaction {
	return domain.Transaction{
		TransactionType: domain.TransactionTypeCredit,
		CoinAmount:      1000,
		Currency:        domain.DefaultCurrency,
		TransactionDate: time.Now(),
	}
}
