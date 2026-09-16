package domain

import "time"

type TransactionType string

const (
	TransactionTypeCredit TransactionType = "CREDIT"
	TransactionTypeDebit  TransactionType = "DEBIT"
	DefaultCurrency       string          = "IDR"
)

type Transaction struct {
	ID              string
	Title           string
	TransactionType TransactionType
	CoinAmount      int64
	TopupExpense    *int64
	Currency        string
	Note            string
	TransactionDate time.Time
	CreatedAt       time.Time
	UpdatedAt       time.Time
}

func (t Transaction) Validate() error {
	if t.TransactionType != TransactionTypeCredit && t.TransactionType != TransactionTypeDebit {
		return ErrInvalidInput
	}
	if t.CoinAmount <= 0 {
		return ErrInvalidInput
	}
	if t.TopupExpense != nil && *t.TopupExpense < 0 {
		return ErrInvalidInput
	}
	if t.Currency == "" {
		t.Currency = DefaultCurrency
	}
	if t.TransactionDate.IsZero() {
		return ErrInvalidInput
	}
	return nil
}
