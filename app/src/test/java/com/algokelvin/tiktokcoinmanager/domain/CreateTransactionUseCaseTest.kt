package com.algokelvin.tiktokcoinmanager.domain

import com.algokelvin.tiktokcoinmanager.core.Result
import com.algokelvin.tiktokcoinmanager.domain.model.TransactionInput
import com.algokelvin.tiktokcoinmanager.domain.model.TransactionType
import com.algokelvin.tiktokcoinmanager.domain.repository.TransactionRepository
import com.algokelvin.tiktokcoinmanager.domain.usecase.CreateTransactionUseCase
import java.time.Instant
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateTransactionUseCaseTest {
    @Test
    fun delegatesToRepository() = runTest {
        val useCase = CreateTransactionUseCase(FakeTransactionRepository())
        val result = useCase(
            TransactionInput(
                transactionType = TransactionType.CREDIT,
                coinAmount = 1000,
                transactionDate = Instant.parse("2026-09-16T09:00:00Z"),
            ),
        )

        assertTrue(result is Result.Success)
    }
}

