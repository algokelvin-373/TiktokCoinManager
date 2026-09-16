package com.algokelvin.tiktokcoinmanager.domain.model

import java.time.Instant

data class TransactionInput(
    val title: String = "",
    val transactionType: TransactionType,
    val coinAmount: Long,
    val topupExpense: Long? = null,
    val currency: String = "IDR",
    val note: String = "",
    val transactionDate: Instant,
)

