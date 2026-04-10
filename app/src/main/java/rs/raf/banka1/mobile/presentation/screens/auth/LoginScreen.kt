package rs.raf.banka1.mobile.presentation.screens.auth

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rs.raf.banka1.mobile.presentation.components.ErrorDialog
import rs.raf.banka1.mobile.presentation.components.InputField
import rs.raf.banka1.mobile.presentation.components.PasswordInputField
import rs.raf.banka1.mobile.presentation.viewmodels.auth.LoginContract
import rs.raf.banka1.mobile.presentation.viewmodels.auth.LoginViewModel
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToDashboard: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent = viewModel::setEvent

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    // --- Validation Logic ---
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val emailError = if (email.isNotEmpty() && !isEmailValid) "Unesite validan email" else null

    val isPasswordValid = password.length >= 6
    val passwordError = if (password.isNotEmpty() && !isPasswordValid) "Lozinka mora imati bar 6 karaktera" else null

    val isFormValid = isEmailValid && isPasswordValid && email.isNotEmpty() && password.isNotEmpty()

    val focusManager = LocalFocusManager.current

    // Handle Error State
    if (state.error != null) {
        ErrorDialog(errorData = state.error) {
            onEvent(LoginContract.UiEvent.ClearError)
        }
    }

    // Handle Side Effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LoginContract.SideEffect.NavigateToDashboard -> onNavigateToDashboard()
            }
        }
    }

    // Main Layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer) // Dark green background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- Custom Header ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Ulogujte se",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // --- Bottom Curved Surface ---
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                var isVisible by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { isVisible = true }

                AnimatedVisibility(
                    visible = isVisible,
                    enter = slideInVertically(initialOffsetY = { it / 4 }) + fadeIn(tween(600))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                            .padding(top = 32.dp, bottom = 48.dp), // Added more bottom padding
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // Texts
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "Dobro došli",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = authAccentColor()
                            )
                            Text(
                                text = "Brzo, lako i sigurno",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        // Animated Illustration
                        AnimatedLockIllustration()

                        Spacer(modifier = Modifier.height(40.dp))

                        // Form Fields
                        InputField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email",
                            error = emailError,
                            modifier = Modifier.fillMaxWidth(),
                            customKeyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        PasswordInputField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Lozinka",
                            error = passwordError,
                            modifier = Modifier.fillMaxWidth(),
                            customKeyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            customKeyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    if (isFormValid) {
                                        onEvent(LoginContract.UiEvent.SendCredentials(email, password))
                                    }
                                }
                            )
                        )

                        // Forgot Password Link
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                            TextButton(onClick = onNavigateToForgotPassword) {
                                Text(
                                    text = "Zaboravili ste lozinku?",
                                    color = authAccentColor(),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Login Button
                        Button(
                            onClick = { onEvent(LoginContract.UiEvent.SendCredentials(email, password)) },
                            enabled = !state.isLoading && isFormValid,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text(
                                    "Ulogujte se",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }

                        // This spacer pushes the fingerprint icon to the bottom
                        Spacer(modifier = Modifier.weight(1f))

                        // Biometric/Fingerprint section
                        IconButton(
                            onClick = { /* Handle Biometric */ },
                            modifier = Modifier.size(72.dp) // Made it slightly larger since it stands alone now
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Fingerprint,
                                contentDescription = "Biometric Login",
                                tint = authAccentColor(),
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun AnimatedLockIllustration() {
    // Infinite floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating_y"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(160.dp)
    ) {
        // Main Circle Background
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        // Lock Icon (Floating)
        Icon(
            imageVector = Icons.Rounded.Lock,
            contentDescription = "Lock",
            tint = authAccentColor(),
            modifier = Modifier
                .size(48.dp)
                .offset(y = offsetY.dp)
        )

        // Decorative Floating Dots (offset manually to match design)
        val primaryColors = listOf(
            Color(0xFF3B82F6), // Blue
            Color(0xFFF43F5E), // Pink
            Color(0xFFF59E0B), // Orange
            Color(0xFF14B8A6), // Teal
            Color(0xFF4338CA)  // Indigo
        )

        Dot(color = primaryColors[0], size = 8.dp, modifier = Modifier.align(Alignment.CenterEnd).offset(x = 10.dp, y = 20.dp + offsetY.dp * 0.5f))
        Dot(color = primaryColors[1], size = 16.dp, modifier = Modifier.align(Alignment.TopEnd).offset(x = (-10).dp, y = offsetY.dp * 1.5f))
        Dot(color = primaryColors[2], size = 14.dp, modifier = Modifier.align(Alignment.BottomStart).offset(x = 10.dp, y = (-10).dp - offsetY.dp))
        Dot(color = primaryColors[3], size = 8.dp, modifier = Modifier.align(Alignment.CenterStart).offset(x = (-20).dp, y = (-20).dp + offsetY.dp * 0.8f))
        Dot(color = primaryColors[4], size = 8.dp, modifier = Modifier.align(Alignment.TopCenter).offset(x = (-10).dp, y = (-10).dp - offsetY.dp * 1.2f))
    }
}

@Composable
fun Dot(color: Color, size: androidx.compose.ui.unit.Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}