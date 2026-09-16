package com.algokelvin.tiktokcoinmanager.domain.usecase

import com.algokelvin.tiktokcoinmanager.domain.repository.TransactionRepository
import javax.inject.Inject

class GetTransactionDetailUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    operator fun invoke(id: String) = repository.getTransactionDetail(id)
}

