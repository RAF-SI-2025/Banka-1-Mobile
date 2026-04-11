package rs.raf.banka1.mobile.presentation.screens.accounts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rs.raf.banka1.mobile.data.remote.responses.AccountDetailsResponseDto
import rs.raf.banka1.mobile.presentation.components.ErrorDialog
import rs.raf.banka1.mobile.presentation.viewmodels.main.AccountDetailContract
import rs.raf.banka1.mobile.presentation.viewmodels.main.AccountDetailViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

private val accountTypeLabels = mapOf(
    "STANDARDNI" to "Standardni racun",
    "STEDNI" to "Stedni racun",
    "PENZIONERSKI" to "Penzionerski racun",
    "ZA_MLADE" to "Racun za mlade",
    "ZA_STUDENTE" to "Studentski racun",
    "ZA_NEZAPOSLENE" to "Racun za nezaposlene",
    "DOO" to "Poslovni - DOO",
    "AD" to "Poslovni - AD",
    "FONDACIJA" to "Poslovni - Fondacija"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailScreen(
    viewModel: AccountDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCardDetail: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val titleAlpha by remember {
        derivedStateOf {
            val fadeStart = with(density) { 24.dp.toPx() } // Start fading as it scrolls past top padding
            val fadeEnd = with(density) { 64.dp.toPx() }   // Fully visible when past the text
            ((scrollState.value - fadeStart) / (fadeEnd - fadeStart)).coerceIn(0f, 1f)
        }
    }


    if (state.error != null) {
        ErrorDialog(errorData = state.error) {
            viewModel.setEvent(AccountDetailContract.UiEvent.ClearError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (titleAlpha > 0f) {
                        Text(
                            text = state.account?.nazivRacuna ?: "Tekuci racun",
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
            } else if (state.account != null) {
                AccountDetailContent(
                    account = state.account!!,
                    onNavigateToCardDetail = onNavigateToCardDetail,
                    scrollState = scrollState
                )
            }
        }
    }
}

@Composable
private fun AccountDetailContent(
    account: AccountDetailsResponseDto,
    scrollState: ScrollState,
    onNavigateToCardDetail: (String) -> Unit
) {
    val formatter = remember {
        NumberFormat.getNumberInstance(Locale("sr", "RS")).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
    }
    val currency = account.currency ?: "RSD"
    val isActive = account.status == "ACTIVE"
    val isBusiness = account.accountType == "BUSINESS"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Header with balance
        BalanceHeader(account = account, formatter = formatter, currency = currency, isActive = isActive)

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Spending limits section
            val dailyLimit = account.dailyLimit ?: 0.0
            val monthlyLimit = account.monthlyLimit ?: 0.0
            if (dailyLimit > 0 || monthlyLimit > 0) {
                DetailSection(
                    title = "Limiti potrosnje",
                    icon = Icons.Default.Speed
                ) {
                    if (dailyLimit > 0) {
                        SpendingRow(
                            label = "Dnevna potrosnja",
                            spent = account.dailySpending ?: 0.0,
                            limit = dailyLimit,
                            currency = currency,
                            formatter = formatter
                        )
                    }
                    if (dailyLimit > 0 && monthlyLimit > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    if (monthlyLimit > 0) {
                        SpendingRow(
                            label = "Mesecna potrosnja",
                            spent = account.monthlySpending ?: 0.0,
                            limit = monthlyLimit,
                            currency = currency,
                            formatter = formatter
                        )
                    }
                }
            }

            // Account info section
            DetailSection(
                title = "Informacije o racunu",
                icon = Icons.Default.Info
            ) {
                val typeLabel = when {
                    account.accountCategory == "FOREIGN_CURRENCY" -> "Devizni racun"
                    account.subtype != null -> accountTypeLabels[account.subtype] ?: "Tekuci racun"
                    else -> "Tekuci racun"
                }

                InfoRow("Tip racuna", typeLabel)
                InfoRow("Vlasnistvo", if (isBusiness) "Poslovni" else "Licni")
                InfoRow("Valuta", currency)
                InfoRow("Broj racuna", formatAccountNumber(account.brojRacuna ?: ""))
                InfoRow("Status", if (isActive) "Aktivan" else "Neaktivan")
            }

            // Dates section
            if (account.creationDate != null || account.expirationDate != null) {
                DetailSection(
                    title = "Datumi",
                    icon = Icons.Default.CalendarToday
                ) {
                    if (account.creationDate != null) {
                        InfoRow("Datum otvaranja", formatDate(account.creationDate))
                    }
                    if (account.expirationDate != null) {
                        InfoRow("Datum isteka", account.expirationDate)
                    }
                }
            }

            // Company info section (for business accounts)
            if (isBusiness && account.nazivFirme != null) {
                DetailSection(
                    title = "Podaci o firmi",
                    icon = Icons.Default.Business
                ) {
                    InfoRow("Naziv firme", account.nazivFirme)
                    if (account.companyRegistrationNumber != null) {
                        InfoRow("Maticni broj", account.companyRegistrationNumber)
                    }
                    if (account.companyTaxId != null) {
                        InfoRow("PIB", account.companyTaxId)
                    }
                    if (account.companyActivityCode != null) {
                        InfoRow("Sifra delatnosti", account.companyActivityCode)
                    }
                    if (account.companyAddress != null) {
                        InfoRow("Adresa", account.companyAddress)
                    }
                }
            }

            // Cards section
            val cards = account.cards
            if (!cards.isNullOrEmpty()) {
                DetailSection(
                    title = "Kartice na racunu",
                    icon = Icons.Default.CreditCard
                ) {
                    cards.forEachIndexed { index, card ->
                        if (index > 0) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = card.cardType ?: "Kartica",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = formatMaskedCardNumber(card.cardNumber ?: ""),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            val status = card.status ?: "ACTIVE"
                            val statusColor = when (status) {
                                "ACTIVE", "ACTIVATED" -> Color(0xFF34A853)
                                "BLOCKED" -> Color(0xFFEA4335)
                                else -> Color(0xFF9E9E9E)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(statusColor.copy(alpha = 0.12f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = when (status) {
                                        "ACTIVE", "ACTIVATED" -> "Aktivna"
                                        "BLOCKED" -> "Blokirana"
                                        else -> status
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = statusColor
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// --- Balance Header ---

@Composable
private fun BalanceHeader(
    account: AccountDetailsResponseDto,
    formatter: NumberFormat,
    currency: String,
    isActive: Boolean
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
                imageVector = Icons.Default.AccountBalance,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = account.nazivRacuna ?: "Racun", // over here
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Balance card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Raspolozivo stanje",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${formatter.format(account.raspolozivoStanje ?: 0.0)} $currency",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                val reserved = account.rezervisanaSredstva ?: 0.0
                val total = account.stanjeRacuna ?: 0.0
                if (reserved > 0 || total != (account.raspolozivoStanje ?: 0.0)) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Ukupno stanje",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${formatter.format(total)} $currency",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        if (reserved > 0) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Rezervisano",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${formatter.format(reserved)} $currency",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }

                if (!isActive) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.08f))
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Ovaj racun je trenutno neaktivan",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

// --- Detail Section Card ---

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

// --- Info Row ---

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

// --- Spending Row with progress ---

@Composable
private fun SpendingRow(
    label: String,
    spent: Double,
    limit: Double,
    currency: String,
    formatter: NumberFormat
) {
    val progress = if (limit > 0) (spent / limit).toFloat().coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800),
        label = "spending"
    )
    val compactFormatter = remember {
        NumberFormat.getNumberInstance(Locale("sr", "RS")).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 0
        }
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${compactFormatter.format(spent)} / ${compactFormatter.format(limit)} $currency",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = if (progress > 0.8f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.outlineVariant,
            strokeCap = StrokeCap.Round
        )
    }
}

// --- Utility ---

private fun formatAccountNumber(number: String): String {
    if (number.length < 10) return number
    return "${number.take(3)}-${number.drop(3).dropLast(2)}-${number.takeLast(2)}"
}

private fun formatMaskedCardNumber(number: String): String {
    return number.replace("-", "").chunked(4).joinToString(" ")
}

private fun formatDate(dateString: String): String {
    // Handle ISO datetime like "2024-01-15T10:30:00"
    return dateString.take(10)
}
