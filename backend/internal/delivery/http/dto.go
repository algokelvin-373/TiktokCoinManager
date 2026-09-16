package http

import (
	"time"

	"tiktokcoinmanager/backend/internal/domain"
)

type apiResponse struct {
	Success bool     `json:"success"`
	Message string   `json:"message"`
	Data    any      `json:"data,omitempty"`
	Errors  []string `json:"errors,omitempty"`
}

type transactionRequest struct {
	Title           string                 `json:"title"`
	TransactionType domain.TransactionType `json:"transaction_type"`
	CoinAmount      int64                  `json:"coin_amount"`
	TopupExpense    *int64                 `json:"topup_expense"`
	Currency        string                 `json:"currency"`
	Note            string                 `json:"note"`
	TransactionDate time.Time              `json:"transaction_date"`
}

type transactionResponse struct {
	ID              string                 `json:"id"`
	Title           string                 `json:"title"`
	TransactionType domain.TransactionType `json:"transaction_type"`
	CoinAmount      int64                  `json:"coin_amount"`
	TopupExpense    *int64                 `json:"topup_expense,omitempty"`
	Currency        string                 `json:"currency"`
	Note            string                 `json:"note"`
	TransactionDate time.Time              `json:"transaction_date"`
	CreatedAt       time.Time              `json:"created_at"`
	UpdatedAt       time.Time              `json:"updated_at"`
}

func (request transactionRequest) toDomain() domain.Transaction {
	return domain.Transaction{
		Title:           request.Title,
		TransactionType: request.TransactionType,
		CoinAmount:      request.CoinAmount,
		TopupExpense:    request.TopupExpense,
		Currency:        request.Currency,
		Note:            request.Note,
		TransactionDate: request.TransactionDate,
	}
}

func toTransactionResponse(transaction domain.Transaction) transactionResponse {
	return transactionResponse{
		ID:              transaction.ID,
		Title:           transaction.Title,
		TransactionType: transaction.TransactionType,
		CoinAmount:      transaction.CoinAmount,
		TopupExpense:    transaction.TopupExpense,
		Currency:        transaction.Currency,
		Note:            transaction.Note,
		TransactionDate: transaction.TransactionDate,
		CreatedAt:       transaction.CreatedAt,
		UpdatedAt:       transaction.UpdatedAt,
	}
}

func toTransactionResponses(transactions []domain.Transaction) []transactionResponse {
	responses := make([]transactionResponse, 0, len(transactions))
	for _, transaction := range transactions {
		responses = append(responses, toTransactionResponse(transaction))
	}
	return responses
}
