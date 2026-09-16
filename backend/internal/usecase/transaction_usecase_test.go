package usecase

import (
	"context"
	"testing"
	"time"

	"tiktokcoinmanager/backend/internal/domain"
)

func TestCreateTransactionRejectsInvalidCoinAmount(t *testing.T) {
	repository := &fakeRepository{}
	useCase := NewTransactionUseCase(repository)

	_, err := useCase.CreateTransaction(context.Background(), domain.Transaction{
		TransactionType: domain.TransactionTypeCredit,
		CoinAmount:      0,
		TransactionDate: time.Now(),
	})

	if err != domain.ErrInvalidInput {
		t.Fatalf("expected invalid input, got %v", err)
	}
}

func TestGetDashboardSummaryReturnsRepositorySummary(t *testing.T) {
	repository := &fakeRepository{
		summary: domain.DashboardSummary{
			CoinBalance:       7500,
			TotalCredit:       10000,
			TotalDebit:        2500,
			TotalTopupExpense: 150000,
		},
	}
	useCase := NewTransactionUseCase(repository)

	summary, err := useCase.GetDashboardSummary(context.Background())
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if summary.CoinBalance != 7500 {
		t.Fatalf("expected coin balance 7500, got %d", summary.CoinBalance)
	}
}

type fakeRepository struct {
	summary domain.DashboardSummary
}

func (f *fakeRepository) List(context.Context) ([]domain.Transaction, error) {
	return nil, nil
}

func (f *fakeRepository) GetByID(context.Context, string) (domain.Transaction, error) {
	return domain.Transaction{CreatedAt: time.Now()}, nil
}

func (f *fakeRepository) Create(_ context.Context, transaction domain.Transaction) (domain.Transaction, error) {
	return transaction, nil
}

func (f *fakeRepository) Update(_ context.Context, transaction domain.Transaction) (domain.Transaction, error) {
	return transaction, nil
}

func (f *fakeRepository) Delete(context.Context, string) error {
	return nil
}

func (f *fakeRepository) Summary(context.Context) (domain.DashboardSummary, error) {
	return f.summary, nil
}
