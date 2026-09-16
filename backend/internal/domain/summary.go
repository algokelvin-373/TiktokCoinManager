package domain

type DashboardSummary struct {
	CoinBalance       int64 `json:"coin_balance"`
	TotalCredit       int64 `json:"total_credit"`
	TotalDebit        int64 `json:"total_debit"`
	TotalTopupExpense int64 `json:"total_topup_expense"`
}
