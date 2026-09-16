package com.algokelvin.tiktokcoinmanager.domain.model

import java.time.Instant

data class Transaction(
    val id: String,
    val title: String,
    val transactionType: TransactionType,
    val coinAmount: Long,
    val topupExpense: Long?,
    val currency: String,
    val note: String,
    val transactionDate: Instant,
    val createdAt: Instant,
    val updatedAt: Instant,
)

enum class TransactionType {
    CREDIT,
    DEBIT,
}

