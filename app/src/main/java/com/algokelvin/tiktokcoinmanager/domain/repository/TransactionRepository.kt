package com.algokelvin.tiktokcoinmanager.domain.repository

import com.algokelvin.tiktokcoinmanager.core.Result
import com.algokelvin.tiktokcoinmanager.domain.model.DashboardSummary
import com.algokelvin.tiktokcoinmanager.domain.model.Transaction
import com.algokelvin.tiktokcoinmanager.domain.model.TransactionInput
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactions(): Flow<Result<List<Transaction>>>
    fun getTransactionDetail(id: String): Flow<Result<Transaction>>
    suspend fun createTransaction(input: TransactionInput): Result<Transaction>
    suspend fun updateTransaction(id: String, input: TransactionInput): Result<Transaction>
    suspend fun deleteTransaction(id: String): Result<Unit>
    fun getDashboardSummary(): Flow<Result<DashboardSummary>>
}

