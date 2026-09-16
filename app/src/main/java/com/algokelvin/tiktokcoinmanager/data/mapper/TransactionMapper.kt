package com.algokelvin.tiktokcoinmanager.data.mapper

import com.algokelvin.tiktokcoinmanager.data.dto.DashboardSummaryDto
import com.algokelvin.tiktokcoinmanager.data.dto.TransactionDto
import com.algokelvin.tiktokcoinmanager.data.dto.TransactionRequestDto
import com.algokelvin.tiktokcoinmanager.domain.model.DashboardSummary
import com.algokelvin.tiktokcoinmanager.domain.model.Transaction
import com.algokelvin.tiktokcoinmanager.domain.model.TransactionInput
import com.algokelvin.tiktokcoinmanager.domain.model.TransactionType
import java.time.Instant

fun TransactionDto.toDomain() = Transaction(
    id = id,
    title = title,
    transactionType = TransactionType.valueOf(transactionType),
    coinAmount = coinAmount,
    topupExpense = topupExpense,
    currency = currency,
    note = note,
    transactionDate = Instant.parse(transactionDate),
    createdAt = Instant.parse(createdAt),
    updatedAt = Instant.parse(updatedAt),
)

fun TransactionInput.toDto() = TransactionRequestDto(
    title = title,
    transactionType = transactionType.name,
    coinAmount = coinAmount,
    topupExpense = topupExpense,
    currency = currency,
    note = note,
    transactionDate = transactionDate.toString(),
)

fun DashboardSummaryDto.toDomain() = DashboardSummary(
    coinBalance = coinBalance,
    totalCredit = totalCredit,
    totalDebit = totalDebit,
    totalTopupExpense = totalTopupExpense,
)

