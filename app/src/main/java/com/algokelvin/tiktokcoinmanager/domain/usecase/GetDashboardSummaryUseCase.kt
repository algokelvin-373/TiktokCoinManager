package com.algokelvin.tiktokcoinmanager.domain.usecase

import com.algokelvin.tiktokcoinmanager.domain.repository.TransactionRepository
import javax.inject.Inject

class GetDashboardSummaryUseCase @Inject constructor(
    private val repository: TransactionRepository,
) {
    operator fun invoke() = repository.getDashboardSummary()
}

