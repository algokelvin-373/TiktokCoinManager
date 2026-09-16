package com.algokelvin.tiktokcoinmanager.domain.usecase

import com.algokelvin.tiktokcoinmanager.domain.repository.TransactionRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    suspend operator fun invoke(id: String) = repository.deleteTransaction(id)
}

