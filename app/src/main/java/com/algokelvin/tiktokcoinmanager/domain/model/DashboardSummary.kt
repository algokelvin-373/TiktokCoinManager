package com.algokelvin.tiktokcoinmanager.domain.model

data class DashboardSummary(
    val coinBalance: Long,
    val totalCredit: Long,
    val totalDebit: Long,
    val totalTopupExpense: Long,
)

