@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package rs.raf.banka1.mobile.presentation.screens.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rs.raf.banka1.mobile.core.util.RevealDirection
import rs.raf.banka1.mobile.core.util.RevealState
import rs.raf.banka1.mobile.core.util.RevealSwipe
import rs.raf.banka1.mobile.core.util.RevealValue
import rs.raf.banka1.mobile.core.util.rememberRevealState
import rs.raf.banka1.mobile.core.util.reset
import rs.raf.banka1.mobile.presentation.viewmodels.main.VerificationContract
import rs.raf.banka1.mobile.presentation.viewmodels.main.VerificationViewModel

@Composable
fun VerificationScreen(
    viewModel: VerificationViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent = viewModel::setEvent

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 1.5.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else if (state.codes.isEmpty()) {
            EmptyState()
        } else {
            CodeList(
                codes = state.codes,
                onMarkUsed = { onEvent(VerificationContract.UiEvent.MarkUsed(it)) },
                onDelete = { onEvent(VerificationContract.UiEvent.Delete(it)) }
            )
        }
    }
}

// --- Code List with Lockauth-style RevealSwipe ---

@Composable
private fun CodeList(
    codes: List<VerificationContract.VerificationCodeUiModel>,
    onMarkUsed: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val revealStates = remember { mutableStateMapOf<Long, RevealState>() }
    var currentlyOpenItemId by rememberSaveable { mutableStateOf<Long?>(null) }
    // Track items being dismissed for animated removal
    val dismissingItems = remember { mutableStateMapOf<Long, Boolean>() }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = codes,
            key = { it.id }
        ) { code ->
            val canSwipe = code.isExpired || code.isUsed
            val expectedDirections = if (canSwipe) setOf(RevealDirection.EndToStart) else emptySet()

            // If the code just became swipeable (e.g. expired), the cached state
            // still has empty directions — replace it so the delete action appears immediately.
            val existing = revealStates[code.id]
            if (existing != null && existing.directions != expectedDirections) {
                revealStates.remove(code.id)
            }

            val revealState = revealStates.getOrPut(code.id) {
                rememberRevealState(
                    directions = expectedDirections,
                    maxRevealDp = 80.dp
                )
            }

            // Close other open items when a new one opens
            if (canSwipe) {
                LaunchedEffect(revealState.anchoredDraggableState.targetValue) {
                    if (revealState.anchoredDraggableState.targetValue == RevealValue.FullyRevealedStart
                        && currentlyOpenItemId != code.id
                    ) {
                        // Close previously open item
                        currentlyOpenItemId?.let { prevId ->
                            revealStates[prevId]?.reset()
                        }
                        currentlyOpenItemId = code.id
                    } else if (revealState.anchoredDraggableState.targetValue == RevealValue.Default
                        && currentlyOpenItemId == code.id
                    ) {
                        currentlyOpenItemId = null
                    }
                }
            }

            val isDismissing = dismissingItems[code.id] == true

            AnimatedVisibility(
                visible = !isDismissing,
                exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(200)),
                modifier = Modifier.animateItem()
            ) {
                RevealSwipe(
                    enableSwipe = canSwipe,
                    state = revealState,
                    shape = MaterialTheme.shapes.large,
                    hiddenContentEnd = {
                        // Lockauth-style: background color + gap + red delete button
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 10.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.error)
                                    .clickable {
                                        // Animate out, then delete
                                        dismissingItems[code.id] = true
                                        coroutineScope.launch {
                                            delay(350)
                                            revealStates.remove(code.id)
                                            dismissingItems.remove(code.id)
                                            onDelete(code.id)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Obrisi",
                                    tint = MaterialTheme.colorScheme.onError
                                )
                            }
                        }
                    }
                ) { shape ->
                    VerificationCodeCard(
                        code = code,
                        shape = shape,
                        onMarkUsed = { onMarkUsed(code.id) }
                    )
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

// --- Verification Code Card (Lockauth-inspired) ---

@Composable
private fun VerificationCodeCard(
    code: VerificationContract.VerificationCodeUiModel,
    shape: androidx.compose.ui.graphics.Shape = MaterialTheme.shapes.large,
    onMarkUsed: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .then(
                if (code.isActive) Modifier.clickable {
                    clipboardManager.setText(AnnotatedString(code.code))
                } else Modifier
            ),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (code.isActive) 2.dp else 0.dp,
        shape = shape
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Top row: icon + operation info + timer/status
            Row(
                modifier = Modifier.padding(bottom = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OperationIcon(operationType = code.operationType)

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = code.operationLabel,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Sesija #${code.sessionId}",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                when {
                    code.isActive -> {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = formatCountdown(code.remainingSeconds),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.W600,
                                    fontSize = 11.sp
                                ),
                                color = if (code.remainingSeconds < 60)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                            CircularTimer(
                                modifier = Modifier.size(36.dp),
                                progress = code.progress.toDouble(),
                                strokeWidth = 3.dp,
                                progressColor = if (code.remainingSeconds < 60)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                    code.isUsed -> {
                        StatusBadge(
                            text = "Iskoriscen",
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    }
                    code.isExpired -> {
                        StatusBadge(
                            text = "Istekao",
                            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Code display row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        if (code.isActive)
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f)
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = code.code.chunked(3).joinToString(" "),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 28.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (code.isActive)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )

                if (code.isActive) {
                    TextButton(onClick = onMarkUsed) {
                        Text(
                            text = "Iskoristi",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

// --- Operation Type Icon ---

@Composable
private fun OperationIcon(operationType: String) {
    val (icon, bgColor) = when (operationType) {
        "PAYMENT" -> Icons.Default.Payment to MaterialTheme.colorScheme.primary
        "TRANSFER" -> Icons.Default.SwapHoriz to MaterialTheme.colorScheme.secondary
        "LIMIT_CHANGE" -> Icons.Default.TrendingUp to Color(0xFFEF6C00)
        "CARD_REQUEST" -> Icons.Default.CreditCard to Color(0xFF7B1FA2)
        "LOAN_REQUEST" -> Icons.Default.AccountBalance to Color(0xFF0277BD)
        else -> Icons.Default.Shield to MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(bgColor.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = bgColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

// --- Status Badge ---

@Composable
private fun StatusBadge(
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(containerColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// --- Circular Timer (from Lockauth) ---

@Composable
private fun CircularTimer(
    modifier: Modifier = Modifier,
    progress: Double,
    strokeWidth: Dp,
    progressColor: Color,
    trackColor: Color
) {
    val sweepAngle = progress * 360f

    Canvas(modifier = modifier) {
        drawArc(
            color = trackColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx())
        )

        drawArc(
            color = progressColor,
            startAngle = -90f,
            sweepAngle = sweepAngle.toFloat(),
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )
    }
}

// --- Empty State ---

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Nema aktivnih kodova",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Kodovi ce se pojaviti kada pokrenete transakciju",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

// --- Helpers ---

private fun formatCountdown(seconds: Long): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%d:%02d".format(m, s)
}