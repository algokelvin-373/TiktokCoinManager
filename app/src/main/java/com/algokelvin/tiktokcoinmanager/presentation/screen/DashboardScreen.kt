package com.algokelvin.tiktokcoinmanager.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.algokelvin.tiktokcoinmanager.domain.model.DashboardSummary
import com.algokelvin.tiktokcoinmanager.domain.model.Transaction
import com.algokelvin.tiktokcoinmanager.domain.model.TransactionType
import com.algokelvin.tiktokcoinmanager.presentation.state.UiState
import com.algokelvin.tiktokcoinmanager.presentation.viewmodel.DashboardViewModel
import java.text.NumberFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ScreenBackground = Color(0xFFFAF8FF)
private val Ink = Color(0xFF12182B)
private val Muted = Color(0xFF5A4D43)
private val Orange = Color(0xFFFFA000)
private val DarkPanel = Color(0xFF293248)
private val Green = Color(0xFF007A58)
private val GreenSoft = Color(0xFFBDF8DD)
private val Red = Color(0xFFC5162E)
private val RedSoft = Color(0xFFFFE4E2)
private val Lavender = Color(0xFFEFF1FF)

private enum class HomeTab(val label: String, val icon: String) {
    Dashboard("Dashboard", "▦"),
    Notes("Catatan", "▤"),
    Analysis("Analisis", "⌁"),
    Account("Akun", "◎"),
}

@Preview
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val summaryState by viewModel.summaryState.collectAsState()
    val transactionsState by viewModel.transactionsState.collectAsState()
    var selectedTab by remember { mutableStateOf(HomeTab.Dashboard) }

    Scaffold(
        containerColor = ScreenBackground,
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onSelected = { selectedTab = it },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = Orange,
                contentColor = Color(0xFF5F3A00),
                shape = CircleShape,
                modifier = Modifier.size(72.dp),
            ) {
                Text("+", fontSize = 38.sp, fontWeight = FontWeight.Light)
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                AppHeader(subtitle = if (selectedTab == HomeTab.Dashboard) "Dashboard" else "Catatan Transaksi")
            }

            when (selectedTab) {
                HomeTab.Dashboard -> item { DashboardContent(summaryState, transactionsState, viewModel::refresh) }
                HomeTab.Notes -> {
                    item { NotesToolbar(summaryState) }
                    item { TransactionListHeader(transactionsState) }
                    when (transactionsState) {
                        UiState.Idle, UiState.Loading -> item { LoadingCard() }
                        UiState.Empty -> item { EmptyStateCard(viewModel::refresh) }
                        is UiState.Error -> item { ErrorCard((transactionsState as UiState.Error).message, viewModel::refresh) }
                        is UiState.Success -> {
                            items((transactionsState as UiState.Success<List<Transaction>>).data) { transaction ->
                                NoteTransactionCard(transaction)
                            }
                        }
                    }
                    item {
                        Box(Modifier.padding(horizontal = 24.dp)) {
                            CoinTipCard("Tips Pembukuan Koin", "Catat setiap gift stream & top-up agar estimasi cashflow tetap rapi.")
                        }
                    }
                }
                HomeTab.Analysis, HomeTab.Account -> item {
                    PlaceholderCard(
                        title = selectedTab.label,
                        message = "Belum termasuk prioritas MVP. Fitur utama saat ini mengikuti PRD.",
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(88.dp)) }
        }
    }
}

@Composable
private fun AppHeader(subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.92f))
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Orange),
            contentAlignment = Alignment.Center,
        ) {
            Text("♪", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("TikTok Coin Ledger", color = Ink, fontSize = 24.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(subtitle, color = Muted, fontSize = 16.sp)
        }
        StatusPill()
        Spacer(Modifier.width(14.dp))
        Text("♢", color = Muted, fontSize = 24.sp)
        Spacer(Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Lavender),
            contentAlignment = Alignment.Center,
        ) {
            Text("A", color = Ink, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Green),
            )
        }
    }
}

@Composable
private fun StatusPill() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFBDF5DF))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(Green),
        )
        Spacer(Modifier.width(5.dp))
        Text("Online", color = Green, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DashboardContent(
    summaryState: UiState<DashboardSummary>,
    transactionsState: UiState<List<Transaction>>,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        when (summaryState) {
            UiState.Idle, UiState.Loading -> LoadingCard(noOuterPadding = true)
            UiState.Empty -> BalanceHero(DashboardSummary(0, 0, 0, 0))
            is UiState.Error -> ErrorCard(summaryState.message, onRetry, noOuterPadding = true)
            is UiState.Success -> BalanceHero(summaryState.data)
        }

        val summary = (summaryState as? UiState.Success)?.data ?: DashboardSummary(0, 0, 0, 0)
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            SummaryMiniCard(
                title = "Total Credit",
                value = "↑ ${coin(summary.totalCredit)}",
                caption = "Coins Masuk",
                color = Green,
                background = GreenSoft,
                modifier = Modifier.weight(1f),
            )
            SummaryMiniCard(
                title = "Total Debit",
                value = "↓ ${coin(summary.totalDebit)}",
                caption = "Coins Keluar",
                color = Red,
                background = RedSoft,
                modifier = Modifier.weight(1f),
            )
        }

        TopupExpenseCard(summary.totalTopupExpense, summary.totalCredit)
        PeriodSelector()

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Orange),
            )
            Spacer(Modifier.width(10.dp))
            Text("Transaksi Terbaru", color = Ink, fontSize = 25.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("Lihat Semua›", color = Color(0xFF8A5800), fontWeight = FontWeight.Bold)
        }

        when (transactionsState) {
            UiState.Idle, UiState.Loading -> LoadingCard(noOuterPadding = true)
            UiState.Empty -> EmptyStateCard(onRetry, noOuterPadding = true)
            is UiState.Error -> ErrorCard(transactionsState.message, onRetry, noOuterPadding = true)
            is UiState.Success -> {
                transactionsState.data.take(3).forEach { transaction ->
                    DashboardTransactionCard(transaction)
                }
            }
        }

        CoinTipCard("Target Koin Minggu Ini", "Capai 15,000 Coins untuk target live berikutnya.")
    }
}

@Composable
private fun BalanceHero(summary: DashboardSummary) {
    Card(
        shape = RoundedCornerShape(0.dp, 0.dp, 14.dp, 14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFFFA800), Color(0xFF9D6500)),
                    ),
                )
                .padding(28.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("SALDO KOIN SAAT INI  ◉", color = Color(0xFF704900), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${coin(summary.coinBalance)} Coins", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 37.sp)
                Text("▣ Credit (${coin(summary.totalCredit)}) - Debit (${coin(summary.totalDebit)})", color = Color(0xFF614713), fontSize = 16.sp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0x33604700)),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF74FFD0)))
                    Spacer(Modifier.width(10.dp))
                    Text("Akun Tersinkronisasi", color = Color(0xFF614713), modifier = Modifier.weight(1f), fontSize = 16.sp)
                    Text(
                        "ID: @rizky_live",
                        color = Color(0xFF704900),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x33FFFFFF))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                    )
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFFFFF)),
                contentAlignment = Alignment.Center,
            ) {
                Text("♪", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SummaryMiniCard(
    title: String,
    value: String,
    caption: String,
    color: Color,
    background: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, color = Muted, fontSize = 16.sp, modifier = Modifier.weight(1f))
                Box(Modifier.size(42.dp).clip(CircleShape).background(background), contentAlignment = Alignment.Center) {
                    Text(if (color == Green) "↗" else "↙", color = color, fontSize = 25.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(value, color = color, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
            Text(caption, color = color, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
private fun TopupExpenseCard(totalExpense: Long, totalCredit: Long) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkPanel),
        elevation = CardDefaults.cardElevation(6.dp),
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(58.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF59627B)), contentAlignment = Alignment.Center) {
                Text("▣", color = Color(0xFFFFD08D), fontSize = 28.sp)
            }
            Spacer(Modifier.width(18.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("TOTAL BIAYA TOP UP", color = Color(0xFFBBC1D3), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(rupiah(totalExpense), color = Color(0xFFF0F3FF), fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
            }
            val average = if (totalCredit > 0) totalExpense / totalCredit else 0
            Text(
                "↯ Avg: Rp$average/Koin",
                color = Color(0xFFFFE5BC),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF6B5636))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun PeriodSelector() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("PERIODE DATA", color = Muted, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("◎ Live Auto-sync", color = Green, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("Semua", "Hari ini", "Minggu ini", "Bulan ini").forEachIndexed { index, label ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (index == 0) DarkPanel else Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(label, color = if (index == 0) Color.White else Muted, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
private fun DashboardTransactionCard(transaction: Transaction) {
    val typeColor = if (transaction.transactionType == TransactionType.CREDIT) Green else Red
    val typeBg = if (transaction.transactionType == TransactionType.CREDIT) GreenSoft else RedSoft
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(22.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TransactionIcon(transaction)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    if (transaction.transactionType == TransactionType.CREDIT) "↑ CREDIT" else "↓ DEBIT",
                    color = typeColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(typeBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                )
                Text(transaction.title.ifBlank { "Transaksi Koin" }, color = Ink, fontWeight = FontWeight.Bold, fontSize = 19.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(transactionSubtitle(transaction), color = Muted, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${if (transaction.transactionType == TransactionType.CREDIT) "+" else "-"}${coin(transaction.coinAmount)}", color = typeColor, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Text("Coins", color = Muted, fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun NotesToolbar(summaryState: UiState<DashboardSummary>) {
    val summary = (summaryState as? UiState.Success)?.data ?: DashboardSummary(0, 0, 0, 0)
    Column(
        modifier = Modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip("∞  Semua", true, Modifier.weight(1f))
            FilterChip("↓  Credit (+ Masuk)", false, Modifier.weight(1.55f), GreenSoft, Green)
            FilterChip("↑  Debit (- Keluar)", false, Modifier.weight(1.35f), RedSoft, Red)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Lavender)
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFF4D28D)), contentAlignment = Alignment.Center) {
                Text("⌁", color = Color(0xFF8A6200), fontSize = 25.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Status Periode", color = Muted, fontSize = 15.sp)
                Text("Menampilkan transaksi", color = Ink, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Text("+${coin(summary.totalCredit)}  /  -${coin(summary.totalDebit)}", color = Green, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    background: Color = DarkPanel,
    content: Color = Color.White,
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(if (selected) DarkPanel else background)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = if (selected) Color.White else content, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Clip)
    }
}

@Composable
private fun TransactionListHeader(state: UiState<List<Transaction>>) {
    val count = (state as? UiState.Success)?.data?.size ?: 0
    Row(
        modifier = Modifier.padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(11.dp).clip(CircleShape).background(Orange))
        Spacer(Modifier.width(10.dp))
        Text("HARI INI • ${todayLabel()}", color = Muted, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text("$count Catatan", color = Muted, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun NoteTransactionCard(transaction: Transaction) {
    val typeColor = if (transaction.transactionType == TransactionType.CREDIT) Green else Red
    val sign = if (transaction.transactionType == TransactionType.CREDIT) "+" else "-"
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TransactionIcon(transaction)
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(transaction.title.ifBlank { "Transaksi Koin" }, color = Ink, fontWeight = FontWeight.Bold, fontSize = 24.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("Biaya: ${transaction.topupExpense?.let { rupiah(it) } ?: "-"}", color = Muted, fontSize = 16.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("$sign${coin(transaction.coinAmount)}", color = typeColor, fontWeight = FontWeight.ExtraBold, fontSize = 27.sp)
                    Text(
                        if (transaction.transactionType == TransactionType.CREDIT) "↑ CREDIT" else "↓ DEBIT",
                        color = typeColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (transaction.transactionType == TransactionType.CREDIT) GreenSoft else RedSoft)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF5F4FC))
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(if (transaction.transactionType == TransactionType.CREDIT) "▣" else "♚", color = Muted, fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Text(transaction.note.ifBlank { "Tidak ada catatan tambahan." }, color = Muted, fontSize = 15.sp, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(time(transaction), color = Muted, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TransactionIcon(transaction: Transaction) {
    val credit = transaction.transactionType == TransactionType.CREDIT
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (credit) GreenSoft else RedSoft),
        contentAlignment = Alignment.Center,
    ) {
        Text(if (credit) "↙" else "↗", color = if (credit) Green else Red, fontSize = 29.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun LoadingCard(noOuterPadding: Boolean = false) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (noOuterPadding) Modifier else Modifier.padding(horizontal = 24.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Orange)
        }
    }
}

@Composable
private fun EmptyStateCard(onRetry: () -> Unit, noOuterPadding: Boolean = false) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (noOuterPadding) Modifier else Modifier.padding(horizontal = 24.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Belum Ada Catatan Transaksi", color = Ink, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("No transaction yet. Start by adding your first TikTok Coin transaction.", color = Muted)
            Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color(0xFF5F3A00))) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit, noOuterPadding: Boolean = false) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (noOuterPadding) Modifier else Modifier.padding(horizontal = 24.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(message, color = Red, fontWeight = FontWeight.Bold)
            Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = Orange, contentColor = Color(0xFF5F3A00))) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun CoinTipCard(title: String, message: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 86.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Lavender)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(64.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFF7DDA8)), contentAlignment = Alignment.Center) {
            Text("♙", color = Color(0xFF9A6500), fontSize = 28.sp)
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, color = Ink, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text(message, color = Muted, fontSize = 15.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun PlaceholderCard(title: String, message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, color = Ink, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Text(message, color = Muted)
        }
    }
}

@Composable
private fun BottomNavigationBar(
    selectedTab: HomeTab,
    onSelected: (HomeTab) -> Unit,
) {
    Box {
        NavigationBar(containerColor = Color.White.copy(alpha = 0.96f), tonalElevation = 8.dp) {
            HomeTab.entries.forEach { tab ->
                NavigationBarItem(
                    selected = selectedTab == tab,
                    onClick = { onSelected(tab) },
                    icon = {
                        Text(tab.icon, fontSize = 26.sp, color = if (selectedTab == tab) Orange else Muted)
                    },
                    label = {
                        Text(tab.label, color = if (selectedTab == tab) Orange else Muted, fontSize = 13.sp)
                    },
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-16).dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(Orange.copy(alpha = 0.35f)),
        )
    }
}

private fun coin(value: Long): String = NumberFormat.getNumberInstance(Locale.US).format(value)

private fun rupiah(value: Long): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    formatter.maximumFractionDigits = 0
    return formatter.format(value)
}

private fun transactionSubtitle(transaction: Transaction): String {
    val expense = transaction.topupExpense?.let { "Biaya: ${rupiah(it)}" } ?: "Tanpa Biaya Top Up"
    return "$expense • ${dateLabel(transaction)}"
}

private fun dateLabel(transaction: Transaction): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("id", "ID")).withZone(ZoneId.systemDefault())
    return formatter.format(transaction.transactionDate)
}

private fun time(transaction: Transaction): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale("id", "ID")).withZone(ZoneId.systemDefault())
    return "${formatter.format(transaction.transactionDate)} WIB"
}

private fun todayLabel(): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("id", "ID"))
    return formatter.format(LocalDate.now()).uppercase(Locale("id", "ID"))
}
