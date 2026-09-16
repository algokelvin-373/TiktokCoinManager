package http

import (
	"net/http"

	"tiktokcoinmanager/backend/internal/usecase"
)

func NewRouter(useCase *usecase.TransactionUseCase, enableSwagger bool) http.Handler {
	mux := http.NewServeMux()
	handler := NewHandler(useCase)

	mux.HandleFunc("GET /api/v1/transactions", handler.listTransactions)
	mux.HandleFunc("GET /api/v1/transactions/{id}", handler.getTransaction)
	mux.HandleFunc("POST /api/v1/transactions", handler.createTransaction)
	mux.HandleFunc("PUT /api/v1/transactions/{id}", handler.updateTransaction)
	mux.HandleFunc("DELETE /api/v1/transactions/{id}", handler.deleteTransaction)
	mux.HandleFunc("GET /api/v1/dashboard/summary", handler.dashboardSummary)

	if enableSwagger {
		mux.HandleFunc("GET /swagger/index.html", serveSwaggerIndex)
		mux.HandleFunc("GET /swagger/openapi.yaml", serveOpenAPI)
	}

	return withJSONRecovery(mux)
}

func withJSONRecovery(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		defer func() {
			if recovered := recover(); recovered != nil {
				writeJSON(w, http.StatusInternalServerError, apiResponse{Success: false, Message: "Server error"})
			}
		}()
		next.ServeHTTP(w, r)
	})
}
