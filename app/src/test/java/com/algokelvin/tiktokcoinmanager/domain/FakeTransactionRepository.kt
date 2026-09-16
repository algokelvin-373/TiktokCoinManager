package com.algokelvin.tiktokcoinmanager.domain

import com.algokelvin.tiktokcoinmanager.core.Result
import com.algokelvin.tiktokcoinmanager.domain.model.DashboardSummary
import com.algokelvin.tiktokcoinmanager.domain.model.Transaction
import com.algokelvin.tiktokcoinmanager.domain.model.TransactionInput
import com.algokelvin.tiktokcoinmanager.domain.repository.TransactionRepository
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeTransactionRepository : TransactionRepository {
    override fun getTransactions(): Flow<Result<List<Transaction>>> = flowOf(Result.Success(emptyList()))

    override fun getTransactionDetail(id: String): Flow<Result<Transaction>> = flowOf(Result.Success(sampleTransaction(id)))

    override suspend fun createTransaction(input: TransactionInput): Result<Transaction> = Result.Success(sampleTransaction())

    override suspend fun updateTransaction(id: String, input: TransactionInput): Result<Transaction> = Result.Success(sampleTransaction(id))

    override suspend fun deleteTransaction(id: String): Result<Unit> = Result.Success(Unit)

    override fun getDashboardSummary(): Flow<Result<DashboardSummary>> = flowOf(
        Result.Success(DashboardSummary(coinBalance = 0, totalCredit = 0, totalDebit = 0, totalTopupExpense = 0)),
    )

    private fun sampleTransaction(id: String = "sample-id") = Transaction(
        id = id,
        title = "Sample",
        transactionType = com.algokelvin.tiktokcoinmanager.domain.model.TransactionType.CREDIT,
        coinAmount = 1000,
        topupExpense = 150000,
        currency = "IDR",
        note = "",
        transactionDate = Instant.parse("2026-09-16T09:00:00Z"),
        createdAt = Instant.parse("2026-09-16T09:00:00Z"),
        updatedAt = Instant.parse("2026-09-16T09:00:00Z"),
    )
}

