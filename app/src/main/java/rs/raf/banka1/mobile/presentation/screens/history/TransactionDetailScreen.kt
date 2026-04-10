package rs.raf.banka1.mobile.presentation.screens.history

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rs.raf.banka1.mobile.data.remote.responses.TransactionResponseDto
import rs.raf.banka1.mobile.presentation.components.ErrorDialog
import rs.raf.banka1.mobile.presentation.viewmodels.main.TransactionDetailContract
import rs.raf.banka1.mobile.presentation.viewmodels.main.TransactionDetailViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

private val statusColors = mapOf(
    "COMPLETED" to Color(0xFF34A853),
    "SUCCESS" to Color(0xFF34A853),
    "PENDING" to Color(0xFFFBBC04),
    "FAILED" to Color(0xFFEA4335),
    "REJECTED" to Color(0xFFEA4335),
    "CANCELLED" to Color(0xFF9E9E9E)
)

private val statusLabels = mapOf(
    "COMPLETED" to "Zavrsena",
    "SUCCESS" to "Uspesna",
    "PENDING" to "U obradi",
    "FAILED" to "Neuspela",
    "REJECTED" to "Odbijena",
    "CANCELLED" to "Otkazana"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    viewModel: TransactionDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val titleAlpha by remember {
        derivedStateOf {
            val fadeStart = with(density) { 24.dp.toPx() }
            val fadeEnd = with(density) { 64.dp.toPx() }
            ((scrollState.value - fadeStart) / (fadeEnd - fadeStart)).coerceIn(0f, 1f)
        }
    }

    if (state.error != null) {
        ErrorDialog(errorData = state.error) {
            viewModel.setEvent(TransactionDetailContract.UiEvent.ClearError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (titleAlpha > 0f) {
                        Text(
                            text = "Detalji transakcije",
                            modifier = Modifier.alpha(titleAlpha),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Nazad",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else if (state.transaction != null) {
                TransactionDetailContent(
                    transaction = state.transaction!!,
                    scrollState = scrollState
                )
            }
        }
    }
}

@Composable
private fun TransactionDetailContent(
    transaction: TransactionResponseDto,
    scrollState: ScrollState
) {
    val formatter = remember {
        NumberFormat.getNumberInstance(Locale("sr", "RS")).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
    }

    val status = transaction.status ?: "COMPLETED"
    val statusColor = statusColors[status] ?: MaterialTheme.colorScheme.primary
    val statusLabel = statusLabels[status] ?: status

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        TransactionHeader(
            transaction = transaction,
            formatter = formatter,
            statusColor = statusColor,
            statusLabel = statusLabel
        )

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Recipient section
            if (!transaction.recipientName.isNullOrBlank()) {
                DetailSection(
                    title = "Primalac",
                    icon = Icons.Default.Person
                ) {
                    InfoRow("Ime primaoca", transaction.recipientName)
                }
            }

            // Accounts section
            DetailSection(
                title = "Racuni",
                icon = Icons.Default.Payments
            ) {
                AccountArrowRow(
                    label = "Sa racuna",
                    account = transaction.fromAccountNumber,
                    icon = Icons.Default.ArrowUpward
                )
                Spacer(modifier = Modifier.height(10.dp))
                AccountArrowRow(
                    label = "Na racun",
                    account = transaction.toAccountNumber,
                    icon = Icons.Default.ArrowDownward
                )
            }

            // Amounts section
            DetailSection(
                title = "Iznosi",
                icon = Icons.Default.Info
            ) {
                InfoRow(
                    "Pocetni iznos",
                    "${formatter.format(transaction.initialAmount ?: 0.0)} ${transaction.fromCurrency ?: ""}"
                )
                InfoRow(
                    "Konacni iznos",
                    "${formatter.format(transaction.finalAmount ?: 0.0)} ${transaction.toCurrency ?: ""}"
                )
            }

            // Conversion section
            if (transaction.exchangeRate != null && transaction.exchangeRate != 1.0) {
                DetailSection(
                    title = "Konverzija",
                    icon = Icons.Default.Percent
                ) {
                    InfoRow("Kurs", formatter.format(transaction.exchangeRate))
                    if (!transaction.fromCurrency.isNullOrBlank() && !transaction.toCurrency.isNullOrBlank()) {
                        InfoRow("Valute", "${transaction.fromCurrency} -> ${transaction.toCurrency}")
                    }
                }
            }

            // Payment codes section
            val hasCodes = !transaction.paymentCode.isNullOrBlank() || !transaction.referenceNumber.isNullOrBlank()
            if (hasCodes) {
                DetailSection(
                    title = "Sifre i pozivi",
                    icon = Icons.Default.Tag
                ) {
                    transaction.paymentCode?.takeIf { it.isNotBlank() }?.let {
                        InfoRow("Sifra placanja", it)
                    }
                    transaction.referenceNumber?.takeIf { it.isNotBlank() }?.let {
                        InfoRow("Poziv na broj", it)
                    }
                }
            }

            // Purpose section
            if (!transaction.paymentPurpose.isNullOrBlank()) {
                DetailSection(
                    title = "Svrha placanja",
                    icon = Icons.Default.Notes
                ) {
                    Text(
                        text = transaction.paymentPurpose,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Metadata section
            DetailSection(
                title = "Nalog",
                icon = Icons.Default.CalendarToday
            ) {
                InfoRow("Broj naloga", transaction.orderNumber ?: "—")
                InfoRow("Datum", formatTimestamp(transaction.createdAt))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TransactionHeader(
    transaction: TransactionResponseDto,
    formatter: NumberFormat,
    statusColor: Color,
    statusLabel: String
) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .offset { IntOffset(0, ((1f - animProgress.value) * 20f * density).roundToInt()) }
            .alpha(animProgress.value)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Payments,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Detalji transakcije", //over here
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Iznos placanja",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${formatter.format(transaction.initialAmount ?: 0.0)} ${transaction.fromCurrency ?: ""}",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(statusColor.copy(alpha = 0.14f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = statusLabel,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = statusColor
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun AccountArrowRow(
    label: String,
    account: String?,
    icon: ImageVector
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = account?.takeIf { it.isNotBlank() } ?: "—",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}