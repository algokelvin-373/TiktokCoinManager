package com.algokelvin.tiktokcoinmanager.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.algokelvin.tiktokcoinmanager.domain.model.Transaction
import com.algokelvin.tiktokcoinmanager.presentation.state.UiState
import com.algokelvin.tiktokcoinmanager.presentation.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val summaryState by viewModel.summaryState.collectAsState()
    val transactionsState by viewModel.transactionsState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { }) {
                Text("+")
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("TikTok Coin Manager")
            SummaryContent(summaryState, onRetry = viewModel::refresh)
            TransactionContent(transactionsState, onRetry = viewModel::refresh)
        }
    }
}

@Composable
private fun SummaryContent(
    state: UiState<com.algokelvin.tiktokcoinmanager.domain.model.DashboardSummary>,
    onRetry: () -> Unit,
) {
    Card {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            when (state) {
                UiState.Idle, UiState.Loading -> CircularProgressIndicator()
                UiState.Empty -> Text("No transaction yet.")
                is UiState.Error -> {
                    Text(state.message)
                    Button(onClick = onRetry) { Text("Retry") }
                }
                is UiState.Success -> {
                    Text("Current Coins: ${state.data.coinBalance}")
                    Text("Credit: ${state.data.totalCredit}")
                    Text("Debit: ${state.data.totalDebit}")
                    Text("Top Up Expense: Rp${state.data.totalTopupExpense}")
                }
            }
        }
    }
}

@Composable
private fun TransactionContent(
    state: UiState<List<Transaction>>,
    onRetry: () -> Unit,
) {
    when (state) {
        UiState.Idle, UiState.Loading -> CircularProgressIndicator()
        UiState.Empty -> Text("No transaction yet.\nStart by adding your first TikTok Coin transaction.")
        is UiState.Error -> {
            Text(state.message)
            Button(onClick = onRetry) { Text("Retry") }
        }
        is UiState.Success -> {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.data) { transaction ->
                    Card {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("${transaction.transactionType} - ${transaction.coinAmount} Coins")
                            Text(transaction.title.ifBlank { "Untitled transaction" })
                        }
                    }
                }
            }
        }
    }
}

