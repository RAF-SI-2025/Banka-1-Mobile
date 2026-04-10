package rs.raf.banka1.mobile.presentation.screens.auth

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Accent color used for the hero elements on the login and forgot-password screens
 * (greeting text, lock/fingerprint/email illustrations, forgot-password link).
 *
 * Light theme keeps the dark brand green ({@code primaryContainer}) which reads well
 * on the pale login surface. In dark theme the same green collapses into the dark
 * background, so we fall back to the turquoise-leaning {@code secondary} which
 * matches the password-field eye icon and stays legible over dark surfaces.
 */
@Composable
fun authAccentColor(): Color =
    if (isSystemInDarkTheme()) MaterialTheme.colorScheme.secondary
    else MaterialTheme.colorScheme.primaryContainer