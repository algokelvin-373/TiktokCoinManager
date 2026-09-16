package persistence

import (
	"context"
	"database/sql"
	"errors"

	"tiktokcoinmanager/backend/internal/domain"
)

type PostgresTransactionRepository struct {
	db *sql.DB
}

func NewPostgresTransactionRepository(db *sql.DB) *PostgresTransactionRepository {
	return &PostgresTransactionRepository{db: db}
}

func (r *PostgresTransactionRepository) List(ctx context.Context) ([]domain.Transaction, error) {
	rows, err := r.db.QueryContext(ctx, `
		SELECT id, title, transaction_type, coin_amount, topup_expense, currency, note, transaction_date, created_at, updated_at
		FROM transactions
		ORDER BY transaction_date DESC, created_at DESC`)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var transactions []domain.Transaction
	for rows.Next() {
		transaction, err := scanTransaction(rows)
		if err != nil {
			return nil, err
		}
		transactions = append(transactions, transaction)
	}
	return transactions, rows.Err()
}

func (r *PostgresTransactionRepository) GetByID(ctx context.Context, id string) (domain.Transaction, error) {
	row := r.db.QueryRowContext(ctx, `
		SELECT id, title, transaction_type, coin_amount, topup_expense, currency, note, transaction_date, created_at, updated_at
		FROM transactions
		WHERE id = $1`, id)
	transaction, err := scanTransaction(row)
	if errors.Is(err, sql.ErrNoRows) {
		return domain.Transaction{}, domain.ErrNotFound
	}
	return transaction, err
}

func (r *PostgresTransactionRepository) Create(ctx context.Context, transaction domain.Transaction) (domain.Transaction, error) {
	_, err := r.db.ExecContext(ctx, `
		INSERT INTO transactions (id, title, transaction_type, coin_amount, topup_expense, currency, note, transaction_date, created_at, updated_at)
		VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10)`,
		transaction.ID,
		transaction.Title,
		transaction.TransactionType,
		transaction.CoinAmount,
		transaction.TopupExpense,
		transaction.Currency,
		transaction.Note,
		transaction.TransactionDate,
		transaction.CreatedAt,
		transaction.UpdatedAt,
	)
	return transaction, err
}

func (r *PostgresTransactionRepository) Update(ctx context.Context, transaction domain.Transaction) (domain.Transaction, error) {
	result, err := r.db.ExecContext(ctx, `
		UPDATE transactions
		SET title = $2, transaction_type = $3, coin_amount = $4, topup_expense = $5, currency = $6, note = $7, transaction_date = $8, updated_at = $9
		WHERE id = $1`,
		transaction.ID,
		transaction.Title,
		transaction.TransactionType,
		transaction.CoinAmount,
		transaction.TopupExpense,
		transaction.Currency,
		transaction.Note,
		transaction.TransactionDate,
		transaction.UpdatedAt,
	)
	if err != nil {
		return domain.Transaction{}, err
	}
	if affected, _ := result.RowsAffected(); affected == 0 {
		return domain.Transaction{}, domain.ErrNotFound
	}
	return transaction, nil
}

func (r *PostgresTransactionRepository) Delete(ctx context.Context, id string) error {
	result, err := r.db.ExecContext(ctx, `DELETE FROM transactions WHERE id = $1`, id)
	if err != nil {
		return err
	}
	if affected, _ := result.RowsAffected(); affected == 0 {
		return domain.ErrNotFound
	}
	return nil
}

func (r *PostgresTransactionRepository) Summary(ctx context.Context) (domain.DashboardSummary, error) {
	row := r.db.QueryRowContext(ctx, `
		SELECT
			COALESCE(SUM(CASE WHEN transaction_type = 'CREDIT' THEN coin_amount ELSE 0 END), 0),
			COALESCE(SUM(CASE WHEN transaction_type = 'DEBIT' THEN coin_amount ELSE 0 END), 0),
			COALESCE(SUM(topup_expense), 0)
		FROM transactions`)

	var credit int64
	var debit int64
	var expense int64
	if err := row.Scan(&credit, &debit, &expense); err != nil {
		return domain.DashboardSummary{}, err
	}
	return domain.DashboardSummary{
		CoinBalance:       credit - debit,
		TotalCredit:       credit,
		TotalDebit:        debit,
		TotalTopupExpense: expense,
	}, nil
}

type scanner interface {
	Scan(dest ...any) error
}

func scanTransaction(row scanner) (domain.Transaction, error) {
	var transaction domain.Transaction
	err := row.Scan(
		&transaction.ID,
		&transaction.Title,
		&transaction.TransactionType,
		&transaction.CoinAmount,
		&transaction.TopupExpense,
		&transaction.Currency,
		&transaction.Note,
		&transaction.TransactionDate,
		&transaction.CreatedAt,
		&transaction.UpdatedAt,
	)
	return transaction, err
}
