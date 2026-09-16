package com.algokelvin.tiktokcoinmanager.domain.usecase

import com.algokelvin.tiktokcoinmanager.domain.model.TransactionInput
import com.algokelvin.tiktokcoinmanager.domain.repository.TransactionRepository
import javax.inject.Inject

class CreateTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    suspend operator fun invoke(input: TransactionInput) = repository.createTransaction(input)
}

