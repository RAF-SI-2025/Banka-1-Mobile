package rs.raf.banka1.mobile.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import rs.raf.banka1.mobile.R

private val defaultFontFamily = FontFamily(
    Font(R.font.inter_extra_light, FontWeight.ExtraLight),
    Font(R.font.inter_light, FontWeight.Light),
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_semi_bold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_extra_bold, FontWeight.ExtraBold),
    Font(R.font.inter_black, FontWeight.Black)
)

private val defaultStyle = TextStyle.Default.copy(
    platformStyle = PlatformTextStyle(
        includeFontPadding = true
    ), fontFamily = defaultFontFamily
)

val Typography = Typography(
    bodyLarge = defaultStyle.copy(
        fontWeight = FontWeight.W500,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.0.sp
    ),
    bodyMedium = defaultStyle.copy(
        fontSize = 14.sp,
        fontWeight = FontWeight.W500,
        lineHeight = 20.sp,
        letterSpacing = 0.0.sp
    ),
    bodySmall = defaultStyle.copy(
        fontWeight = FontWeight.W500,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.0.sp
    ),

    headlineLarge = defaultStyle.copy(
        fontWeight = FontWeight.W700,
        fontSize = 40.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.3).sp,
    ),

    headlineMedium = defaultStyle.copy(
        fontWeight = FontWeight.W500,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.0.sp,
    ),

    headlineSmall = defaultStyle.copy(
        fontWeight = FontWeight.W700,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.0.sp,
    ),

    titleLarge = defaultStyle.copy(
        fontWeight = FontWeight.W600,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.0.sp,
    ),
    titleMedium = defaultStyle.copy(
        fontWeight = FontWeight.W600,
        fontSize = 18.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.0.sp,
    ),
    titleSmall = defaultStyle.copy(
        fontWeight = FontWeight.W600,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.0.sp,
    ),

    labelMedium = defaultStyle.copy(
        fontWeight = FontWeight.W500,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.0.sp
    ),
)