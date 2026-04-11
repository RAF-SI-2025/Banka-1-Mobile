package rs.raf.banka1.mobile.presentation.screens.history

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rs.raf.banka1.mobile.data.remote.responses.TransactionResponseDto
import rs.raf.banka1.mobile.data.remote.responses.TransferResponseDto
import rs.raf.banka1.mobile.presentation.components.ErrorDialog
import rs.raf.banka1.mobile.presentation.viewmodels.main.HistoryContract
import rs.raf.banka1.mobile.presentation.viewmodels.main.HistoryTab
import rs.raf.banka1.mobile.presentation.viewmodels.main.HistoryViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateToTransferDetail: (orderNumber: String, fromCurrency: String, toCurrency: String) -> Unit,
    onNavigateToTransactionDetail: (TransactionResponseDto) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent = viewModel::setEvent

    if (state.error != null) {
        ErrorDialog(errorData = state.error) {
            onEvent(HistoryContract.UiEvent.ClearError)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Istorija",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Pregled vasih transfera i transakcija",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Tab Row
        TabRow(
            selectedTabIndex = if (state.selectedTab == HistoryTab.TRANSFERS) 0 else 1,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(
                        tabPositions[if (state.selectedTab == HistoryTab.TRANSFERS) 0 else 1]
                    ),
                    height = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.padding(horizontal = 16.dp).clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = state.selectedTab == HistoryTab.TRANSFERS,
                onClick = { onEvent(HistoryContract.UiEvent.SelectTab(HistoryTab.TRANSFERS)) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.SwapHoriz,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Transferi", fontWeight = FontWeight.SemiBold)
                    }
                }
            )
            Tab(
                selected = state.selectedTab == HistoryTab.TRANSACTIONS,
                onClick = { onEvent(HistoryContract.UiEvent.SelectTab(HistoryTab.TRANSACTIONS)) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Payments,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Transakcije", fontWeight = FontWeight.SemiBold)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val screenState = when {
            state.isLoading -> HistoryScreenState.Loading
            state.selectedTab == HistoryTab.TRANSFERS -> HistoryScreenState.Transfers
            else -> HistoryScreenState.Transactions
        }

        Crossfade(
            targetState = screenState,
            animationSpec = tween(durationMillis = 220),
            modifier = Modifier.fillMaxSize(),
            label = "history_content"
        ) { target ->
            when (target) {
                HistoryScreenState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                HistoryScreenState.Transfers -> {
                    if (state.transfers.isEmpty()) {
                        EmptyState("Nemate transfera")
                    } else {
                        TransfersList(
                            transfers = state.transfers,
                            accountCurrencies = state.accountCurrencies,
                            onTransferClick = onNavigateToTransferDetail
                        )
                    }
                }

                HistoryScreenState.Transactions -> {
                    if (state.transactions.isEmpty()) {
                        EmptyState("Nemate transakcija")
                    } else {
                        TransactionsList(
                            transactions = state.transactions,
                            onTransactionClick = { transaction -> onNavigateToTransactionDetail(transaction) }
                        )
                    }
                }
            }
        }
    }
}

private enum class HistoryScreenState { Loading, Transfers, Transactions }

// --- Transfers List ---

@Composable
private fun TransfersList(
    transfers: List<TransferResponseDto>,
    accountCurrencies: Map<String, String>, // Accept map
    onTransferClick: (String, String, String) -> Unit
) {
    val formatter = remember {
        NumberFormat.getNumberInstance(Locale("sr", "RS")).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(
            items = transfers,
            key = { index, transfer -> transfer.orderNumber ?: "transfer_$index" }
        ) { index, transfer ->
            val animProgress = remember { Animatable(0f) }
            LaunchedEffect(Unit) {
                animProgress.animateTo(
                    1f,
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = index * 50,
                        easing = FastOutSlowInEasing
                    )
                )
            }

            Box(
                modifier = Modifier
                    .offset { IntOffset(0, ((1f - animProgress.value) * 24f * density).roundToInt()) }
                    .alpha(animProgress.value)
            ) {
                TransferRow(
                    transfer = transfer,
                    accountCurrencies = accountCurrencies, // Pass map
                    formatter = formatter,
                    onClick = {
                        val fromCurr = accountCurrencies[transfer.fromAccountNumber] ?: "RSD"
                        val toCurr = accountCurrencies[transfer.toAccountNumber] ?: "RSD"
                        onTransferClick(transfer.orderNumber ?: "", fromCurr, toCurr)
                    }
                )
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun TransferRow(
    transfer: TransferResponseDto,
    accountCurrencies: Map<String, String>, // Accept map
    formatter: NumberFormat,
    onClick: () -> Unit
) {
    val fromCurrency = accountCurrencies[transfer.fromAccountNumber] ?: ""
    val toCurrency = accountCurrencies[transfer.toAccountNumber] ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Transfer icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Transfer",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formatAccountShort(transfer.fromAccountNumber),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formatTimestamp(transfer.timestamp),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = "${formatter.format(transfer.initialAmount ?: 0.0)} $fromCurrency",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (transfer.finalAmount != null && transfer.finalAmount != transfer.initialAmount) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "-> ${formatter.format(transfer.finalAmount)} $toCurrency",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

// --- Transactions List ---

private val transactionStatusColors = mapOf(
    "COMPLETED" to Color(0xFF34A853),
    "SUCCESS" to Color(0xFF34A853),
    "PENDING" to Color(0xFFFBBC04),
    "FAILED" to Color(0xFFEA4335),
    "REJECTED" to Color(0xFFEA4335),
    "CANCELLED" to Color(0xFF9E9E9E)
)

private val transactionStatusLabels = mapOf(
    "COMPLETED" to "Zavrsena",
    "SUCCESS" to "Uspesna",
    "PENDING" to "U obradi",
    "FAILED" to "Neuspela",
    "REJECTED" to "Odbijena",
    "CANCELLED" to "Otkazana"
)

@Composable
private fun TransactionsList(
    transactions: List<TransactionResponseDto>,
    onTransactionClick: (TransactionResponseDto) -> Unit
) {
    val formatter = remember {
        NumberFormat.getNumberInstance(Locale("sr", "RS")).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(
            items = transactions,
            key = { index, tx -> tx.orderNumber ?: "tx_$index" }
        ) { index, transaction ->
            val animProgress = remember { Animatable(0f) }
            LaunchedEffect(Unit) {
                animProgress.animateTo(
                    1f,
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = index * 50,
                        easing = FastOutSlowInEasing
                    )
                )
            }

            Box(
                modifier = Modifier
                    .offset { IntOffset(0, ((1f - animProgress.value) * 24f * density).roundToInt()) }
                    .alpha(animProgress.value)
            ) {
                TransactionRow(
                    transaction = transaction,
                    formatter = formatter,
                    onClick = { onTransactionClick(transaction) }
                )
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun TransactionRow(
    transaction: TransactionResponseDto,
    formatter: NumberFormat,
    onClick: () -> Unit
) {
    val status = transaction.status ?: "COMPLETED"
    val statusColor = transactionStatusColors[status] ?: MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Transaction icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.recipientName?.takeIf { it.isNotBlank() } ?: "Placanje",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = transaction.paymentPurpose?.takeIf { it.isNotBlank() }
                        ?: formatAccountShort(transaction.toAccountNumber),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formatTimestamp(transaction.createdAt),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${formatter.format(transaction.initialAmount ?: 0.0)} ${transaction.fromCurrency ?: ""}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = transactionStatusLabels[status] ?: status,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        ),
                        color = statusColor
                    )
                }
            }
        }
    }
}

// --- Empty State ---

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- Utility ---

internal fun formatAccountShort(number: String?): String {
    if (number.isNullOrBlank()) return "—"
    if (number.length < 10) return number
    return "${number.take(3)}...${number.takeLast(4)}"
}

internal fun formatTimestamp(raw: String?): String {
    if (raw.isNullOrBlank()) return "—"
    // Accepts ISO-ish strings like "2026-04-10T14:32:00" or "2026-04-10 14:32:00"
    val datePart = raw.take(10)
    val timePart = raw.drop(11).take(5)
    return if (timePart.isNotBlank()) "$datePart $timePart" else datePart
}