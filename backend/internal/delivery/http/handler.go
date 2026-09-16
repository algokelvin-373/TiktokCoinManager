package http

import (
	"encoding/json"
	"errors"
	"net/http"
	"strings"

	"tiktokcoinmanager/backend/internal/domain"
	"tiktokcoinmanager/backend/internal/usecase"
)

type Handler struct {
	useCase *usecase.TransactionUseCase
}

func NewHandler(useCase *usecase.TransactionUseCase) *Handler {
	return &Handler{useCase: useCase}
}

func (h *Handler) listTransactions(w http.ResponseWriter, r *http.Request) {
	transactions, err := h.useCase.ListTransactions(r.Context())
	if err != nil {
		writeError(w, err)
		return
	}
	writeJSON(w, http.StatusOK, apiResponse{Success: true, Message: "Transactions retrieved successfully", Data: toTransactionResponses(transactions)})
}

func (h *Handler) getTransaction(w http.ResponseWriter, r *http.Request) {
	transaction, err := h.useCase.GetTransaction(r.Context(), r.PathValue("id"))
	if err != nil {
		writeError(w, err)
		return
	}
	writeJSON(w, http.StatusOK, apiResponse{Success: true, Message: "Transaction retrieved successfully", Data: toTransactionResponse(transaction)})
}

func (h *Handler) createTransaction(w http.ResponseWriter, r *http.Request) {
	var request transactionRequest
	if err := json.NewDecoder(r.Body).Decode(&request); err != nil {
		writeJSON(w, http.StatusBadRequest, apiResponse{Success: false, Message: "Invalid request body", Errors: []string{err.Error()}})
		return
	}

	transaction, err := h.useCase.CreateTransaction(r.Context(), request.toDomain())
	if err != nil {
		writeError(w, err)
		return
	}
	writeJSON(w, http.StatusCreated, apiResponse{Success: true, Message: "Transaction created successfully", Data: toTransactionResponse(transaction)})
}

func (h *Handler) updateTransaction(w http.ResponseWriter, r *http.Request) {
	var request transactionRequest
	if err := json.NewDecoder(r.Body).Decode(&request); err != nil {
		writeJSON(w, http.StatusBadRequest, apiResponse{Success: false, Message: "Invalid request body", Errors: []string{err.Error()}})
		return
	}

	transaction, err := h.useCase.UpdateTransaction(r.Context(), r.PathValue("id"), request.toDomain())
	if err != nil {
		writeError(w, err)
		return
	}
	writeJSON(w, http.StatusOK, apiResponse{Success: true, Message: "Transaction updated successfully", Data: toTransactionResponse(transaction)})
}

func (h *Handler) deleteTransaction(w http.ResponseWriter, r *http.Request) {
	if err := h.useCase.DeleteTransaction(r.Context(), r.PathValue("id")); err != nil {
		writeError(w, err)
		return
	}
	writeJSON(w, http.StatusOK, apiResponse{Success: true, Message: "Transaction deleted successfully"})
}

func (h *Handler) dashboardSummary(w http.ResponseWriter, r *http.Request) {
	summary, err := h.useCase.GetDashboardSummary(r.Context())
	if err != nil {
		writeError(w, err)
		return
	}
	writeJSON(w, http.StatusOK, apiResponse{Success: true, Message: "Dashboard summary retrieved successfully", Data: summary})
}

func writeJSON(w http.ResponseWriter, status int, payload apiResponse) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	_ = json.NewEncoder(w).Encode(payload)
}

func writeError(w http.ResponseWriter, err error) {
	switch {
	case errors.Is(err, domain.ErrInvalidInput), errors.Is(err, domain.ErrInvalidID):
		writeJSON(w, http.StatusUnprocessableEntity, apiResponse{Success: false, Message: "Invalid transaction data", Errors: []string{err.Error()}})
	case errors.Is(err, domain.ErrNotFound):
		writeJSON(w, http.StatusNotFound, apiResponse{Success: false, Message: "Transaction not found", Errors: []string{err.Error()}})
	default:
		writeJSON(w, http.StatusInternalServerError, apiResponse{Success: false, Message: "Server error", Errors: []string{strings.TrimSpace(err.Error())}})
	}
}
