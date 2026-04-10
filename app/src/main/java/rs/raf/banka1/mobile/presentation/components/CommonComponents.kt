package rs.raf.banka1.mobile.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MarkEmailRead
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.material.icons.rounded.Email
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.scale

// 1. SAFELY ANIMATED ITEM
@Composable
fun StaggeredAnimItem(
    delayMillis: Int,
    content: @Composable () -> Unit
) {
    var itemVisible by remember { mutableStateOf(false) }

    // Triggers independently as soon as the composable enters the screen
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        itemVisible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (itemVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing),
        label = "alpha"
    )
    val offsetY by animateDpAsState(
        targetValue = if (itemVisible) 0.dp else 40.dp,
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing),
        label = "offset"
    )

    Box(
        modifier = Modifier
            .alpha(alpha)
            .offset(y = offsetY)
    ) {
        content()
    }
}



@Composable
fun AbstractEmailArt(
    accentColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    val infiniteTransition = rememberInfiniteTransition(label = "art_anim")

    // 1. Pulsing scale for the center circle
    val circleScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // 2. Fading alpha (fades out as it expands for a "ripple" effect)
    val circleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // 3. Gentle vertical floating for the email icon
    val iconOffset by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Box(
        modifier = Modifier.size(140.dp),
        contentAlignment = Alignment.Center
    ) {
        // Static soft squircle background base
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(accentColor.copy(alpha = 0.12f))
        )

        // Animated pulsing circle (Perfectly centered)
        Box(
            modifier = Modifier
                .size(70.dp)
                .scale(circleScale)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = circleAlpha))
        )

        // Floating email icon
        Icon(
            imageVector = Icons.Rounded.Email,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier
                .size(48.dp)
                .offset(y = iconOffset.dp)
        )
    }
}

// 3. PASSWORD STRENGTH METER
@Composable
fun PasswordStrengthMeter(score: Int, passwordLength: Int) {
    val indicatorColors = listOf(
        MaterialTheme.colorScheme.error,        // Weak
        Color(0xFFF59E0B),                      // Medium (Orange)
        MaterialTheme.colorScheme.primary       // Strong
    )

    val currentColor = if (passwordLength == 0) MaterialTheme.colorScheme.surfaceVariant else indicatorColors[(score - 1).coerceIn(0, 2)]
    val animatedColor by animateColorAsState(targetValue = currentColor, label = "color")

    val textLabel = when {
        passwordLength == 0 -> "Unesite lozinku"
        score <= 1 -> "Slaba lozinka"
        score == 2 -> "Dobra lozinka"
        else -> "Odlična lozinka"
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (i in 1..3) {
                val isActive = score >= i && passwordLength > 0
                val segmentColor by animateColorAsState(
                    targetValue = if (isActive) animatedColor else MaterialTheme.colorScheme.surfaceVariant,
                    label = "segmentColor"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(segmentColor)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = textLabel,
            style = MaterialTheme.typography.labelMedium,
            color = if (passwordLength == 0) MaterialTheme.colorScheme.onSurfaceVariant else animatedColor
        )
    }
}