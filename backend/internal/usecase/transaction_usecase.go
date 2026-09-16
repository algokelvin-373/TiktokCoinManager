package usecase

import (
	"context"
	"crypto/rand"
	"encoding/hex"
	"strings"
	"time"

	"tiktokcoinmanager/backend/internal/domain"
)

type TransactionUseCase struct {
	repository domain.TransactionRepository
	now        func() time.Time
}

func NewTransactionUseCase(repository domain.TransactionRepository) *TransactionUseCase {
	return &TransactionUseCase{
		repository: repository,
		now:        func() time.Time { return time.Now().UTC() },
	}
}

func (u *TransactionUseCase) ListTransactions(ctx context.Context) ([]domain.Transaction, error) {
	return u.repository.List(ctx)
}

func (u *TransactionUseCase) GetTransaction(ctx context.Context, id string) (domain.Transaction, error) {
	if strings.TrimSpace(id) == "" {
		return domain.Transaction{}, domain.ErrInvalidID
	}
	return u.repository.GetByID(ctx, id)
}

func (u *TransactionUseCase) CreateTransaction(ctx context.Context, transaction domain.Transaction) (domain.Transaction, error) {
	if transaction.Currency == "" {
		transaction.Currency = domain.DefaultCurrency
	}
	if err := transaction.Validate(); err != nil {
		return domain.Transaction{}, err
	}

	now := u.now()
	transaction.ID = newID()
	transaction.CreatedAt = now
	transaction.UpdatedAt = now
	return u.repository.Create(ctx, transaction)
}

func (u *TransactionUseCase) UpdateTransaction(ctx context.Context, id string, transaction domain.Transaction) (domain.Transaction, error) {
	if strings.TrimSpace(id) == "" {
		return domain.Transaction{}, domain.ErrInvalidID
	}
	if transaction.Currency == "" {
		transaction.Currency = domain.DefaultCurrency
	}
	if err := transaction.Validate(); err != nil {
		return domain.Transaction{}, err
	}

	existing, err := u.repository.GetByID(ctx, id)
	if err != nil {
		return domain.Transaction{}, err
	}

	transaction.ID = id
	transaction.CreatedAt = existing.CreatedAt
	transaction.UpdatedAt = u.now()
	return u.repository.Update(ctx, transaction)
}

func (u *TransactionUseCase) DeleteTransaction(ctx context.Context, id string) error {
	if strings.TrimSpace(id) == "" {
		return domain.ErrInvalidID
	}
	return u.repository.Delete(ctx, id)
}

func (u *TransactionUseCase) GetDashboardSummary(ctx context.Context) (domain.DashboardSummary, error) {
	return u.repository.Summary(ctx)
}

func newID() string {
	bytes := make([]byte, 16)
	if _, err := rand.Read(bytes); err != nil {
		return hex.EncodeToString([]byte(time.Now().UTC().Format(time.RFC3339Nano)))
	}
	bytes[6] = (bytes[6] & 0x0f) | 0x40
	bytes[8] = (bytes[8] & 0x3f) | 0x80
	return hex.EncodeToString(bytes[0:4]) + "-" +
		hex.EncodeToString(bytes[4:6]) + "-" +
		hex.EncodeToString(bytes[6:8]) + "-" +
		hex.EncodeToString(bytes[8:10]) + "-" +
		hex.EncodeToString(bytes[10:16])
}
