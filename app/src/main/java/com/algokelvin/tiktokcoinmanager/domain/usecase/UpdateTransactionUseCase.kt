package com.algokelvin.tiktokcoinmanager.domain.usecase

import com.algokelvin.tiktokcoinmanager.domain.model.TransactionInput
import com.algokelvin.tiktokcoinmanager.domain.repository.TransactionRepository
import javax.inject.Inject

class UpdateTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    suspend operator fun invoke(id: String, input: TransactionInput) = repository.updateTransaction(id, input)
}

