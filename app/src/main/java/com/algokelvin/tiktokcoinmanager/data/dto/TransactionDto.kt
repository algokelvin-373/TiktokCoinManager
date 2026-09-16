package com.algokelvin.tiktokcoinmanager.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    val id: String,
    val title: String,
    @SerialName("transaction_type") val transactionType: String,
    @SerialName("coin_amount") val coinAmount: Long,
    @SerialName("topup_expense") val topupExpense: Long? = null,
    val currency: String,
    val note: String,
    @SerialName("transaction_date") val transactionDate: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
)

@Serializable
data class TransactionRequestDto(
    val title: String,
    @SerialName("transaction_type") val transactionType: String,
    @SerialName("coin_amount") val coinAmount: Long,
    @SerialName("topup_expense") val topupExpense: Long? = null,
    val currency: String,
    val note: String,
    @SerialName("transaction_date") val transactionDate: String,
)

