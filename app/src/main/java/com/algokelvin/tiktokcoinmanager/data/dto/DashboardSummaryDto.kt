package com.algokelvin.tiktokcoinmanager.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DashboardSummaryDto(
    @SerialName("coin_balance") val coinBalance: Long,
    @SerialName("total_credit") val totalCredit: Long,
    @SerialName("total_debit") val totalDebit: Long,
    @SerialName("total_topup_expense") val totalTopupExpense: Long,
)

