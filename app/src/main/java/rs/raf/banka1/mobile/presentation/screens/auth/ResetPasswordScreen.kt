package rs.raf.banka1.mobile.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import rs.raf.banka1.mobile.presentation.components.PasswordInputField
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import rs.raf.banka1.mobile.presentation.components.PasswordStrengthMeter
import rs.raf.banka1.mobile.presentation.components.StaggeredAnimItem

@Composable
fun ResetPasswordScreen(
    onNavigateBack: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val hasMinLength = newPassword.length >= 8
    val hasNumber = newPassword.any { it.isDigit() }
    val score = (if (hasMinLength) 1 else 0) + (if (hasNumber) 1 else 0) + (if (newPassword.length >= 12) 1 else 0)

    val doPasswordsMatch = newPassword == confirmPassword && newPassword.isNotEmpty()
    val isFormValid = hasMinLength && doPasswordsMatch

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .offset(x = (-100).dp, y = (-50).dp)
                .size(350.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
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
                            text = "Nova lozinka",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Vaša nova lozinka mora biti drugačija od prethodne i sadržati najmanje 8 karaktera.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                StaggeredAnimItem(delayMillis = 150) {
                    Column {
                        PasswordInputField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = "Nova lozinka",
                            modifier = Modifier.fillMaxWidth(),
                            customKeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                            customKeyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        PasswordStrengthMeter(score = score, passwordLength = newPassword.length)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                StaggeredAnimItem(delayMillis = 300) {
                    PasswordInputField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Potvrdite lozinku",
                        error = if (confirmPassword.isNotEmpty() && !doPasswordsMatch) "Lozinke se ne poklapaju" else null,
                        modifier = Modifier.fillMaxWidth(),
                        customKeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        customKeyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            if (isFormValid) onSubmit(newPassword)
                        })
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                StaggeredAnimItem(delayMillis = 450) {
                    Button(
                        onClick = {
                            isSubmitting = true
                            onSubmit(newPassword)
                        },
                        enabled = isFormValid && !isSubmitting,
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
                        if (isSubmitting) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimaryContainer)
                        } else {
                            Text("Ažuriraj lozinku", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }
    }
}