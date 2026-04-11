package rs.raf.banka1.mobile.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color


val LightColorScheme = lightColorScheme(
    // --- MAIN BRAND COLORS ---
    // The bright green used for key actions (Confirm buttons, FABs)
    primary = Color(0xFF22C55E),
    onPrimary = Color(0xFFFFFFFF),

    // The dark green used for the heavy top bar
    primaryContainer = Color(0xFF166534),
    onPrimaryContainer = Color(0xFFFFFFFF),

    inversePrimary = Color(0xFF86EFAC), // Lighter green for inverse situations

    // --- BACKGROUNDS & SURFACES ---
    // The gray background sitting behind your cards
    background = Color(0xFFE5E7EB),
    onBackground = Color(0xFF374151), // Your mystery dark slate for text!

    // The stark white of the cards themselves
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF374151),

    // The slightly off-white used for the sidebar/navigation
    surfaceVariant = Color(0xFFF8FAFC),
    onSurfaceVariant = Color(0xFF4B5563), // Slightly lighter text for secondary info

    surfaceTint = Color(0xFF22C55E), // Adds subtle green tint to elevated surfaces in M3
    inverseSurface = Color(0xFF1F2937),
    surfaceBright = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFD1D5DB),

    // --- SECONDARY/ACCENTS ---
    secondary = Color(0xFF10B981), // A bridging emerald green
    onSecondary = Color(0xFFFFFFFF),

    // --- BORDERS & OUTLINES ---
    // Used for the outline of the "Nazad" (Back) button and input fields
    outline = Color(0xFFD1D5DB),
    outlineVariant = Color(0xFFE5E7EB),

    // --- ERROR STATES ---
    // Used for the "ODBIJENO" (Rejected) status badges
    error = Color(0xFFEF4444),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE2E2), // Light red background for the badge
    onErrorContainer = Color(0xFFB91C1C)  // Dark red text for the badge
)

val DarkColorScheme = darkColorScheme(
    // --- MAIN BRAND COLORS ---
    // Kept the bright green, but it can be slightly desaturated in dark mode if it vibrates too much
    primary = Color(0xFF22C55E),
    onPrimary = Color(0xFF022C22), // Very dark green text on the bright button

    // Top bar stays dark green, preserving brand identity in dark mode
    primaryContainer = Color(0xFF166534),
    onPrimaryContainer = Color(0xFFD1FAE5), // Light green text/icons

    inversePrimary = Color(0xFF166534),

    // --- BACKGROUNDS & SURFACES ---
    // A deep, dark slate gray for the main background
    background = Color(0xFF111827),
    onBackground = Color(0xFFF9FAFB), // Very light gray/white text

    // Slightly elevated dark color for the cards
    surface = Color(0xFF1F2937),
    onSurface = Color(0xFFF9FAFB),

    // Using your mystery dark color as the variant surface for dark mode sidebars!
    surfaceVariant = Color(0xFF374151),
    onSurfaceVariant = Color(0xFFD1D5DB),

    surfaceTint = Color(0xFF22C55E),
    inverseSurface = Color(0xFFF3F4F6),
    surfaceBright = Color(0xFF374151),
    surfaceDim = Color(0xFF111827),

    // --- SECONDARY/ACCENTS ---
    secondary = Color(0xFF34D399),
    onSecondary = Color(0xFF022C22),

    // --- BORDERS & OUTLINES ---
    outline = Color(0xFF4B5563),
    outlineVariant = Color(0xFF374151),

    // --- ERROR STATES ---
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA)
)