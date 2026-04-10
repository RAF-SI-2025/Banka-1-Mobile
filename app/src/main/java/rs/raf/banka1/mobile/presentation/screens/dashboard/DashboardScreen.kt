package rs.raf.banka1.mobile.presentation.screens.dashboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import rs.raf.banka1.mobile.data.remote.responses.AccountDetailsResponseDto
import rs.raf.banka1.mobile.data.remote.responses.ExchangeRateDto
import rs.raf.banka1.mobile.presentation.components.ErrorDialog
import rs.raf.banka1.mobile.presentation.navigation.LocalDashboardTopBarProgress
import rs.raf.banka1.mobile.presentation.viewmodels.main.DashboardContract
import rs.raf.banka1.mobile.presentation.viewmodels.main.DashboardViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

// --- Stagger entrance animation ---

private const val SECTION_COUNT = 4
private const val STAGGER_DELAY_MS = 40L
private const val ANIM_DURATION_MS = 300
private const val SLIDE_OFFSET_DP = 28f

@Composable
private fun rememberStaggerAnimations(): List<Animatable<Float, *>> {
    val animatables = remember { List(SECTION_COUNT) { Animatable(0f) } }
    LaunchedEffect(Unit) {
        animatables.forEachIndexed { index, anim ->
            delay(index * STAGGER_DELAY_MS)
            anim.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = ANIM_DURATION_MS,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }
    return animatables
}

private fun Modifier.staggerEntrance(progress: Float): Modifier {
    return this
        .offset {
            val offsetDp = (1f - progress) * SLIDE_OFFSET_DP
            IntOffset(0, (offsetDp * density).roundToInt())
        }
        .alpha(progress)
}

// --- Dashboard Screen ---

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToVerification: () -> Unit,
    onNavigateToExchange: () -> Unit,
    onNavigateToAccounts: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent = viewModel::setEvent
    val stagger = rememberStaggerAnimations()

    if (state.error != null) {
        ErrorDialog(errorData = state.error) {
            onEvent(DashboardContract.UiEvent.ClearError)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                DashboardContract.SideEffect.NavigateToVerification -> onNavigateToVerification()
            }
        }
    }

    val scrollState = rememberScrollState()
    val topBarProgress = LocalDashboardTopBarProgress.current
    var greetingHeightPx by remember { mutableIntStateOf(0) }

    LaunchedEffect(scrollState, greetingHeightPx) {
        snapshotFlow { scrollState.value }
            .distinctUntilChanged()
            .collect { scroll ->
                val fadeDistance = (greetingHeightPx * 0.8f).coerceAtLeast(1f)

                topBarProgress.floatValue = (scroll.toFloat() / fadeDistance).coerceIn(0f, 1f)
            }
    }

    DisposableEffect(Unit) {
        onDispose { topBarProgress.floatValue = 0f }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // Section 0: Greeting Header
        Box(
            modifier = Modifier
                .onSizeChanged { greetingHeightPx = it.height }
                .staggerEntrance(stagger[0].value)
        ) {
            GreetingHeader(clientName = state.clientName)
        }

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Section 1: Balance Card
            Box(modifier = Modifier.staggerEntrance(stagger[1].value)) {
                if (state.isLoading) {
                    LoadingCard()
                } else {
                    BalanceCard(
                        totalBalanceRsd = state.totalBalanceRsd,
                        accounts = state.accounts,
                        exchangeRates = state.exchangeRates,
                        onClick = onNavigateToAccounts
                    )
                }
            }

            // Section 2: Verification Card
            Box(modifier = Modifier.staggerEntrance(stagger[2].value)) {
                VerificationCard(
                    onClick = onNavigateToVerification,
                    activeCount = state.activeVerificationCount
                )
            }

            // Section 3: Exchange Rates Card
            Box(modifier = Modifier.staggerEntrance(stagger[3].value)) {
                ExchangeRatesCard(
                    rates = state.exchangeRates,
                    onClick = onNavigateToExchange
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// --- Greeting Header ---

@Composable
private fun GreetingHeader(clientName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Text(
            text = if (clientName.isNotEmpty()) "Dobrodosli, $clientName" else "Dobrodosli",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Pregled vaseg finansijskog stanja",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// --- Balance Card ---

private val chartBarColors = listOf(
    Color(0xFF4285F4),
    Color(0xFF34A853),
    Color(0xFFFBBC04),
    Color(0xFFEA4335),
    Color(0xFF9C27B0),
    Color(0xFF00ACC1),
)

@Composable
private fun BalanceCard(
    totalBalanceRsd: Double,
    accounts: List<AccountDetailsResponseDto>,
    exchangeRates: List<ExchangeRateDto>,
    onClick: () -> Unit = {}
) {
    val rateMap = remember(exchangeRates) {
        exchangeRates.associate { (it.currencyCode ?: "") to (it.sellingRate ?: 1.0) }
    }
    val dailySpending = accounts.sumOf { it.dailySpending ?: 0.0 }
    val dailyLimit = accounts.sumOf { it.dailyLimit ?: 0.0 }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Wallet,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ukupno stanje",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val formatter = remember {
                NumberFormat.getNumberInstance(Locale("sr", "RS")).apply {
                    minimumFractionDigits = 2
                    maximumFractionDigits = 2
                }
            }
            Text(
                text = "${formatter.format(totalBalanceRsd)} RSD",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            val accountCount = accounts.size
            Text(
                text = if (accountCount == 1) "1 racun" else "$accountCount racuna",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (dailyLimit > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                SpendingIndicator(
                    label = "Dnevna potrosnja",
                    spent = dailySpending,
                    limit = dailyLimit,
                    currency = "RSD"
                )
            }

            if (accounts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                AccountBalanceChart(
                    accounts = accounts,
                    totalBalanceRsd = totalBalanceRsd,
                    rateMap = rateMap
                )
            }
        }
    }
}

// --- Account Balance Chart ---

@Composable
private fun AccountBalanceChart(
    accounts: List<AccountDetailsResponseDto>,
    totalBalanceRsd: Double,
    rateMap: Map<String, Double>
) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animProgress.animateTo(
            1f,
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
        )
    }

    val compactFormatter = remember {
        NumberFormat.getNumberInstance(Locale("sr", "RS")).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 0
        }
    }

    val sortedAccounts = remember(accounts) {
        accounts.sortedByDescending { it.raspolozivoStanje ?: 0.0 }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Raspodela po racunima",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Stacked bar
        if (totalBalanceRsd > 0) {
            val barColors = sortedAccounts.mapIndexed { i, _ ->
                chartBarColors[i % chartBarColors.size]
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(RoundedCornerShape(7.dp))
            ) {
                val totalWidth = size.width * animProgress.value
                var xOffset = 0f
                val barHeight = size.height
                val cornerRadius = CornerRadius(7.dp.toPx())

                drawRoundRect(
                    color = Color.LightGray.copy(alpha = 0.15f),
                    size = Size(size.width, barHeight),
                    cornerRadius = cornerRadius
                )

                sortedAccounts.forEachIndexed { index, account ->
                    val balanceRsd = accountBalanceInRsd(account, rateMap)
                    val fraction = (balanceRsd / totalBalanceRsd).toFloat()
                    val segmentWidth = fraction * totalWidth

                    if (segmentWidth > 0f) {
                        drawRect(
                            color = barColors[index],
                            topLeft = Offset(xOffset, 0f),
                            size = Size(segmentWidth, barHeight)
                        )
                        xOffset += segmentWidth
                    }
                }
            }
        }

        // Legend rows
        sortedAccounts.forEachIndexed { index, account ->
            val balance = account.raspolozivoStanje ?: 0.0
            val reserved = account.rezervisanaSredstva ?: 0.0
            val balanceRsd = accountBalanceInRsd(account, rateMap)
            val pct = if (totalBalanceRsd > 0) (balanceRsd / totalBalanceRsd * 100).toInt() else 0
            val color = chartBarColors[index % chartBarColors.size]
            val acctCurrency = account.currency ?: "RSD"

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color)
                )

                Text(
                    text = account.nazivRacuna ?: account.brojRacuna ?: "Racun ${index + 1}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${compactFormatter.format(balance)} $acctCurrency",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (reserved > 0) {
                        Text(
                            text = "Rezervisano: ${compactFormatter.format(reserved)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(color.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$pct%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = color
                    )
                }
            }
        }
    }
}

private fun accountBalanceInRsd(
    account: AccountDetailsResponseDto,
    rateMap: Map<String, Double>
): Double {
    val balance = account.raspolozivoStanje ?: 0.0
    val currency = account.currency ?: "RSD"
    return if (currency == "RSD") balance else balance * (rateMap[currency] ?: 1.0)
}

// --- Spending Indicator ---

@Composable
private fun SpendingIndicator(
    label: String,
    spent: Double,
    limit: Double,
    currency: String
) {
    val progress = if (limit > 0) (spent / limit).toFloat().coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800),
        label = "spending_progress"
    )
    val formatter = remember {
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
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "${formatter.format(spent)} / ${formatter.format(limit)} $currency",
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

// --- Verification Card ---

@Composable
private fun VerificationCard(onClick: () -> Unit, activeCount: Int = 0) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Verifikacioni kodovi",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Potvrdite transakcije sa vaseg naloga",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (activeCount > 0) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (activeCount > 9) "9+" else activeCount.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = MaterialTheme.colorScheme.onError
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// --- Exchange Rates Card ---

private val highlightCurrencies = listOf("EUR", "USD", "CHF", "GBP")

@Composable
private fun ExchangeRatesCard(
    rates: List<ExchangeRateDto>,
    onClick: () -> Unit
) {
    val displayRates = remember(rates) {
        rates.filter { it.currencyCode in highlightCurrencies && it.currencyCode != "RSD" }
            .take(4)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00897B).copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CurrencyExchange,
                        contentDescription = null,
                        tint = Color(0xFF00897B),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Kursna lista",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (displayRates.isNotEmpty()) "Danas" else "Ucitavanje...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (displayRates.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Valuta",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(52.dp)
                    )
                    Text(
                        text = "Kupovni",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Prodajni",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val rateFormatter = remember {
                    NumberFormat.getNumberInstance(Locale("sr", "RS")).apply {
                        minimumFractionDigits = 2
                        maximumFractionDigits = 4
                    }
                }

                displayRates.forEach { rate ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = rate.currencyCode ?: "",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.width(44.dp)
                        )
                        Text(
                            text = rateFormatter.format(rate.buyingRate ?: 0.0),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = rateFormatter.format(rate.sellingRate ?: 0.0),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Prikazite sve kurseve →",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

// --- Loading Card ---

@Composable
private fun LoadingCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
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
    }
}