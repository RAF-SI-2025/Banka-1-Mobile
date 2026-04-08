package rs.raf.banka1.mobile.presentation.screens.auth

import android.util.Patterns
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import rs.raf.banka1.mobile.presentation.components.AbstractEmailArt
import rs.raf.banka1.mobile.presentation.components.ErrorDialog
import rs.raf.banka1.mobile.presentation.components.InputField
import rs.raf.banka1.mobile.presentation.components.StaggeredAnimItem
import rs.raf.banka1.mobile.presentation.viewmodels.auth.ForgotPasswordContract
import rs.raf.banka1.mobile.presentation.viewmodels.auth.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToConfirmation: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent = viewModel::setEvent

    var email by rememberSaveable { mutableStateOf("") }
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val emailError = if (email.isNotEmpty() && !isEmailValid) "Neispravan format" else null

    if (state.error != null) {
        ErrorDialog(errorData = state.error) {
            onEvent(ForgotPasswordContract.UiEvent.ClearError)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ForgotPasswordContract.SideEffect.NavigateToConfirmation ->
                    onNavigateToConfirmation(effect.email)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // --- Abstract Brand Background Blob ---
        Box(
            modifier = Modifier
                .offset(x = 100.dp, y = (-100).dp)
                .size(400.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // Floating Back Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Nazad")
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                StaggeredAnimItem(delayMillis = 0) {
                    Column {
                        Text(
                            text = "Zaboravili ste lozinku?",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bez brige, dešava se svima. Unesite email adresu povezanu sa vašim nalogom.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                StaggeredAnimItem(delayMillis = 150) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        AbstractEmailArt()
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                StaggeredAnimItem(delayMillis = 300) {
                    InputField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Vaša Email adresa",
                        error = emailError,
                        modifier = Modifier.fillMaxWidth(),
                        customKeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                StaggeredAnimItem(delayMillis = 450) {
                    Button(
                        onClick = {
                            onEvent(ForgotPasswordContract.UiEvent.Submit(email))
                        },
                        enabled = isEmailValid && !state.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp)
                            .navigationBarsPadding()
                            .height(64.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimaryContainer)
                        } else {
                            Text("Pošalji link za obnovu", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }
    }
}