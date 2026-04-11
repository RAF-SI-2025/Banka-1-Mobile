package rs.raf.banka1.mobile.presentation.screens.accounts

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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rs.raf.banka1.mobile.data.remote.responses.AccountDetailsResponseDto
import rs.raf.banka1.mobile.presentation.components.ErrorDialog
import rs.raf.banka1.mobile.presentation.viewmodels.main.AccountsCardsContract
import rs.raf.banka1.mobile.presentation.viewmodels.main.AccountsCardsTab
import rs.raf.banka1.mobile.presentation.viewmodels.main.AccountsCardsViewModel
import rs.raf.banka1.mobile.presentation.viewmodels.main.CardWithAccount
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

// --- Currency colors for account icons ---
private val accountColors = mapOf(
    "RSD" to Color(0xFF4285F4),
    "EUR" to Color(0xFF34A853),
    "USD" to Color(0xFFFBBC04),
    "CHF" to Color(0xFFEA4335),
    "GBP" to Color(0xFF9C27B0),
    "JPY" to Color(0xFF00ACC1),
    "CAD" to Color(0xFFFF7043),
    "AUD" to Color(0xFF5C6BC0)
)

private val accountTypeLabels = mapOf(
    "STANDARDNI" to "Standardni",
    "STEDNI" to "Stedni",
    "PENZIONERSKI" to "Penzionerski",
    "ZA_MLADE" to "Za mlade",
    "ZA_STUDENTE" to "Studentski",
    "ZA_NEZAPOSLENE" to "Za nezaposlene",
    "DOO" to "DOO",
    "AD" to "AD",
    "FONDACIJA" to "Fondacija"
)

private val currencySymbols = mapOf(
    "RSD" to "din.",
    "EUR" to "\u20AC",
    "USD" to "$",
    "CHF" to "CHF",
    "GBP" to "\u00A3",
    "JPY" to "\u00A5",
    "CAD" to "C$",
    "AUD" to "A$"
)

@Composable
fun AccountsCardsScreen(
    viewModel: AccountsCardsViewModel,
    onNavigateToAccountDetail: (String) -> Unit,
    onNavigateToCardDetail: (String, String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent = viewModel::setEvent

    if (state.error != null) {
        ErrorDialog(errorData = state.error) {
            onEvent(AccountsCardsContract.UiEvent.ClearError)
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
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Racuni i kartice",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Pregled vasih racuna i kartica",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Tab Row
        TabRow(
            selectedTabIndex = if (state.selectedTab == AccountsCardsTab.ACCOUNTS) 0 else 1,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(
                        tabPositions[if (state.selectedTab == AccountsCardsTab.ACCOUNTS) 0 else 1]
                    ),
                    height = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.padding(horizontal = 16.dp).clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = state.selectedTab == AccountsCardsTab.ACCOUNTS,
                onClick = { onEvent(AccountsCardsContract.UiEvent.SelectTab(AccountsCardsTab.ACCOUNTS)) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AccountBalance,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Racuni", fontWeight = FontWeight.SemiBold)
                    }
                }
            )
            Tab(
                selected = state.selectedTab == AccountsCardsTab.CARDS,
                onClick = { onEvent(AccountsCardsContract.UiEvent.SelectTab(AccountsCardsTab.CARDS)) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CreditCard,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Kartice", fontWeight = FontWeight.SemiBold)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val screenState = when {
            state.isLoading -> AccountsCardsScreenState.Loading
            state.selectedTab == AccountsCardsTab.ACCOUNTS -> AccountsCardsScreenState.Accounts
            else -> AccountsCardsScreenState.Cards
        }

        Crossfade(
            targetState = screenState,
            animationSpec = tween(durationMillis = 220),
            modifier = Modifier.fillMaxSize(),
            label = "accounts_cards_content"
        ) { target ->
            when (target) {
                AccountsCardsScreenState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                AccountsCardsScreenState.Accounts -> {
                    if (state.accounts.isEmpty()) {
                        EmptyState("Nemate otvorene racune")
                    } else {
                        AccountsList(
                            accounts = state.accounts,
                            onAccountClick = { accountNumber -> onNavigateToAccountDetail(accountNumber) }
                        )
                    }
                }

                AccountsCardsScreenState.Cards -> {
                    if (state.cards.isEmpty()) {
                        EmptyState("Nemate izdate kartice")
                    } else {
                        CardsList(
                            cards = state.cards,
                            onCardClick = { accountNumber, cardNumber ->
                                onNavigateToCardDetail(accountNumber, cardNumber)
                            }
                        )
                    }
                }
            }
        }
    }
}

private enum class AccountsCardsScreenState { Loading, Accounts, Cards }

// --- Accounts List ---

@Composable
private fun AccountsList(
    accounts: List<AccountDetailsResponseDto>,
    onAccountClick: (String) -> Unit
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
            items = accounts,
            key = { _, account -> account.brojRacuna ?: "" }
        ) { index, account ->
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
                AccountCard(
                    account = account,
                    formatter = formatter,
                    onClick = { onAccountClick(account.brojRacuna ?: "") }
                )
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun AccountCard(
    account: AccountDetailsResponseDto,
    formatter: NumberFormat,
    onClick: () -> Unit
) {
    val currency = account.currency ?: "RSD"
    val accentColor = accountColors[currency] ?: MaterialTheme.colorScheme.primary
    val isActive = account.status == "ACTIVE"

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
            // Currency icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currencySymbols[currency] ?: currency.take(2),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = account.nazivRacuna ?: "Racun",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val typeLabel = when {
                        account.accountCategory == "FOREIGN_CURRENCY" -> "Devizni"
                        account.subtype != null -> accountTypeLabels[account.subtype] ?: "Tekuci"
                        else -> "Tekuci"
                    }
                    Text(
                        text = typeLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (!isActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Neaktivan",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formatAccountNumber(account.brojRacuna ?: ""),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${formatter.format(account.raspolozivoStanje ?: 0.0)} $currency",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                val reserved = account.rezervisanaSredstva ?: 0.0
                if (reserved > 0) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Rez: ${formatter.format(reserved)}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

// --- Cards List ---

@Composable
private fun CardsList(
    cards: List<CardWithAccount>,
    onCardClick: (String, String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(
            items = cards,
            key = { _, cwa -> cwa.card.cardNumber ?: "" }
        ) { index, cardWithAccount ->
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
                CardItem(
                    cardWithAccount = cardWithAccount,
                    onClick = {
                        onCardClick(
                            cardWithAccount.card.accountNumber ?: "",
                            cardWithAccount.card.cardNumber ?: ""
                        )
                    }
                )
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

private val cardStatusColors = mapOf(
    "ACTIVE" to Color(0xFF34A853),
    "ACTIVATED" to Color(0xFF34A853),
    "BLOCKED" to Color(0xFFEA4335),
    "DEACTIVATED" to Color(0xFF9E9E9E),
    "EXPIRED" to Color(0xFFFBBC04),
    "CANCELLED" to Color(0xFF9E9E9E)
)

private val cardStatusLabels = mapOf(
    "ACTIVE" to "Aktivna",
    "ACTIVATED" to "Aktivna",
    "BLOCKED" to "Blokirana",
    "DEACTIVATED" to "Deaktivirana",
    "EXPIRED" to "Istekla",
    "CANCELLED" to "Otkazana"
)

@Composable
private fun CardItem(
    cardWithAccount: CardWithAccount,
    onClick: () -> Unit
) {
    val card = cardWithAccount.card
    val status = card.status ?: "ACTIVE"
    val statusColor = cardStatusColors[status] ?: MaterialTheme.colorScheme.primary

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
            // Card icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.cardType ?: "Kartica",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formatMaskedCardNumber(card.cardNumber ?: ""),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = cardWithAccount.accountName,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = cardStatusLabels[status] ?: status,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = statusColor
                    )
                }

                if (card.expiryDate != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Istice: ${card.expiryDate}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
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
                imageVector = Icons.Default.AccountBalance,
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

private fun formatAccountNumber(number: String): String {
    if (number.length < 10) return number
    return "${number.take(3)}-${number.drop(3).dropLast(2)}-${number.takeLast(2)}"
}

private fun formatMaskedCardNumber(number: String): String {
    // Format as groups of 4: XXXX XXXX XXXX 1234
    return number.replace("-", "").chunked(4).joinToString(" ")
}
