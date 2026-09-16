package domain

import "errors"

var (
	ErrNotFound          = errors.New("transaction not found")
	ErrInvalidInput      = errors.New("invalid transaction data")
	ErrInvalidID         = errors.New("invalid transaction id")
	ErrRepositoryFailure = errors.New("repository failure")
)
