package com.algokelvin.tiktokcoinmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.algokelvin.tiktokcoinmanager.core.AppError
import com.algokelvin.tiktokcoinmanager.core.Result
import com.algokelvin.tiktokcoinmanager.domain.model.DashboardSummary
import com.algokelvin.tiktokcoinmanager.domain.model.Transaction
import com.algokelvin.tiktokcoinmanager.domain.usecase.GetDashboardSummaryUseCase
import com.algokelvin.tiktokcoinmanager.domain.usecase.GetTransactionsUseCase
import com.algokelvin.tiktokcoinmanager.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardSummaryUseCase: GetDashboardSummaryUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase,
) : ViewModel() {
    private val _summaryState = MutableStateFlow<UiState<DashboardSummary>>(UiState.Idle)
    val summaryState: StateFlow<UiState<DashboardSummary>> = _summaryState.asStateFlow()

    private val _transactionsState = MutableStateFlow<UiState<List<Transaction>>>(UiState.Idle)
    val transactionsState: StateFlow<UiState<List<Transaction>>> = _transactionsState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        loadSummary()
        loadTransactions()
    }

    private fun loadSummary() {
        viewModelScope.launch {
            _summaryState.value = UiState.Loading
            getDashboardSummaryUseCase().collect { result ->
                _summaryState.value = when (result) {
                    is Result.Success -> UiState.Success(result.data)
                    is Result.Failure -> UiState.Error(result.error.userMessage())
                }
            }
        }
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            _transactionsState.value = UiState.Loading
            getTransactionsUseCase().collect { result ->
                _transactionsState.value = when (result) {
                    is Result.Success -> if (result.data.isEmpty()) UiState.Empty else UiState.Success(result.data)
                    is Result.Failure -> UiState.Error(result.error.userMessage())
                }
            }
        }
    }
}

private fun AppError.userMessage(): String = when (this) {
    AppError.Connection -> "Connection unavailable. Please try again."
    AppError.Timeout -> "Request timed out. Please retry."
    AppError.Validation -> "Please check the transaction data."
    AppError.NotFound -> "Transaction was not found."
    AppError.Server -> "Server error. Please try again later."
    is AppError.Unknown -> message.ifBlank { "Unexpected error. Please retry." }
}

