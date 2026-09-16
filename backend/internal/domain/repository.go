package domain

import "context"

type TransactionRepository interface {
	List(ctx context.Context) ([]Transaction, error)
	GetByID(ctx context.Context, id string) (Transaction, error)
	Create(ctx context.Context, transaction Transaction) (Transaction, error)
	Update(ctx context.Context, transaction Transaction) (Transaction, error)
	Delete(ctx context.Context, id string) error
	Summary(ctx context.Context) (DashboardSummary, error)
}
