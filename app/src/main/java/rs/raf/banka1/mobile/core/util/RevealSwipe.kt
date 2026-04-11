@file:OptIn(ExperimentalFoundationApi::class)

package rs.raf.banka1.mobile.core.util

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

enum class RevealDirection { StartToEnd, EndToStart }

enum class RevealValue { Default, FullyRevealedEnd, FullyRevealedStart }

data class RevealState(
    val maxRevealDp: Dp = 80.dp,
    val directions: Set<RevealDirection>,
    private val density: Density,
) {
    val anchoredDraggableState = AnchoredDraggableState(
        initialValue = RevealValue.Default,
        positionalThreshold = { distance: Float -> distance * 0.5f },
        velocityThreshold = { with(density) { 125.dp.toPx() } },
        snapAnimationSpec = spring(),
        decayAnimationSpec = androidx.compose.animation.splineBasedDecay(density),
        anchors = DraggableAnchors {
            RevealValue.Default at 0f
            if (RevealDirection.StartToEnd in directions)
                RevealValue.FullyRevealedEnd at with(density) { maxRevealDp.toPx() }
            if (RevealDirection.EndToStart in directions)
                RevealValue.FullyRevealedStart at -with(density) { maxRevealDp.toPx() }
        }
    )
}

@Composable
fun rememberRevealState(
    maxRevealDp: Dp = 80.dp,
    directions: Set<RevealDirection> = setOf(RevealDirection.EndToStart),
): RevealState {
    val density = LocalDensity.current
    return remember {
        RevealState(maxRevealDp = maxRevealDp, directions = directions, density = density)
    }
}

suspend fun RevealState.reset() {
    anchoredDraggableState.animateTo(RevealValue.Default)
}

/**
 *
 * Swipe partially to reveal a hidden action (e.g. delete button), then tap the button.
 * This does NOT dismiss the item — it just reveals the background action.
 */
@Composable
fun RevealSwipe(
    modifier: Modifier = Modifier,
    enableSwipe: Boolean = true,
    state: RevealState = rememberRevealState(),
    shape: CornerBasedShape,
    hiddenContentEnd: @Composable () -> Unit = {},
    content: @Composable (Shape) -> Unit
) {
    val maxRevealPx = with(LocalDensity.current) { state.maxRevealDp.toPx() }
    val draggedRatio = if (maxRevealPx != 0f)
        (state.anchoredDraggableState.offset.absoluteValue / maxRevealPx.absoluteValue).coerceIn(0f, 1f)
    else 0f
    val alphaEasing: Easing = CubicBezierEasing(0.4f, 0.4f, 0.17f, 0.9f)
    val alpha = alphaEasing.transform(draggedRatio)

    Box(modifier = modifier) {
        // Background layer — the hidden delete button
        Card(
            modifier = Modifier.matchParentSize(),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            shape = shape
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(alpha)
            ) {
                if (state.directions.contains(RevealDirection.EndToStart)) {
                    Box(
                        modifier = Modifier
                            .width(state.maxRevealDp)
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        hiddenContentEnd()
                    }
                }
            }
        }

        // Foreground layer — the actual card content
        BoxWithConstraints(
            modifier = Modifier.then(
                if (enableSwipe)
                    Modifier
                        .offset {
                            IntOffset(
                                x = state.anchoredDraggableState
                                    .requireOffset()
                                    .roundToInt(),
                                y = 0,
                            )
                        }
                        .anchoredDraggable(
                            state = state.anchoredDraggableState,
                            orientation = Orientation.Horizontal,
                        )
                else Modifier
            )
        ) { this
            // Tap foreground to close if open
            val isOpen = state.anchoredDraggableState.targetValue != RevealValue.Default
            Box(
                modifier = if (isOpen && enableSwipe) {
                    Modifier.clickable(
                        indication = null,
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                    ) { /* handled by LaunchedEffect in parent */ }
                } else Modifier
            ) {
                content(shape)
            }
        }
    }
}
