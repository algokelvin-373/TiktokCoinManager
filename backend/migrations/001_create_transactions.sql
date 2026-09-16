CREATE TABLE IF NOT EXISTS transactions (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL DEFAULT '',
    transaction_type TEXT NOT NULL CHECK (transaction_type IN ('CREDIT', 'DEBIT')),
    coin_amount BIGINT NOT NULL CHECK (coin_amount > 0),
    topup_expense BIGINT NULL CHECK (topup_expense IS NULL OR topup_expense >= 0),
    currency TEXT NOT NULL DEFAULT 'IDR',
    note TEXT NOT NULL DEFAULT '',
    transaction_date TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_transactions_transaction_date ON transactions (transaction_date DESC);
CREATE INDEX IF NOT EXISTS idx_transactions_transaction_type ON transactions (transaction_type);

