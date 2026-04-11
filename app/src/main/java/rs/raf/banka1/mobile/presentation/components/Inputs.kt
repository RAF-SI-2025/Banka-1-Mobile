package rs.raf.banka1.mobile.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import rs.raf.banka1.mobile.R

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    error: String? = null,
    required: Boolean = true,
    onBackground: Boolean = true,
    customKeyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    customKeyboardActions: KeyboardActions = KeyboardActions.Default
) {
    InnerInputField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        error = error,
        required = required,
        isPassword = false,
        onBackground = onBackground,
        customKeyboardOptions = customKeyboardOptions,
        customKeyboardActions = customKeyboardActions
    )
}

@Composable
fun PasswordInputField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    error: String? = null,
    required: Boolean = true,
    onBackground: Boolean = true,
    customKeyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    customKeyboardActions: KeyboardActions = KeyboardActions.Default
) {
    InnerInputField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        error = error,
        required = required,
        isPassword = true,
        onBackground = onBackground,
        customKeyboardOptions = customKeyboardOptions,
        customKeyboardActions = customKeyboardActions
    )
}

const val OTP_LENGTH = 6

@Composable
fun OtpInputField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    error: String? = null,
    required: Boolean = true,
    onBackground: Boolean = true,
    onDone: () -> Unit = {}
) {
    val maxChar = OTP_LENGTH
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        focusRequester.requestFocus()
    }

    InnerInputField(
        value = value,
        onValueChange = {
            val text = it.take(maxChar)
            if (it.length > maxChar) {
                focusManager.moveFocus(FocusDirection.Down)
            }
            onValueChange(text)
        },
        modifier = modifier,
        innerModifier = Modifier.focusRequester(focusRequester),
        label = label,
        error = error,
        required = required,
        isPassword = false,
        onBackground = onBackground,
        customKeyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        customKeyboardActions = KeyboardActions(
            onDone = {
                if (value.length == maxChar) {
                    onDone()
                }
            }
        )
    )
}

@Composable
fun SearchInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    initialFocus: Boolean = true,
    onSearch: () -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        if (initialFocus) {
            focusRequester.requestFocus()
        }
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        shape = MaterialTheme.shapes.medium,
        placeholder = {
            Text(
                text = "Search…",
                color = MaterialTheme.colorScheme.secondary
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent
        ),
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        textStyle = MaterialTheme.typography.bodyLarge,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(onSearch = {
            focusManager.clearFocus()
            onSearch()
        })
    )
}

@Composable
fun InputTextAreaField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    error: String? = null,
    required: Boolean = true,
    onBackground: Boolean = true,
    customKeyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    customKeyboardActions: KeyboardActions = KeyboardActions.Default,
    maxLines: Int = 1,
    minLines: Int = 1
) {
    InnerInputField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        error = error,
        required = required,
        isPassword = false,
        onBackground = onBackground,
        customKeyboardOptions = customKeyboardOptions,
        customKeyboardActions = customKeyboardActions,
        maxLines = maxLines,
        minLines = minLines
    )
}

@Composable
private fun InnerInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    label: String? = null,
    error: String? = null,
    required: Boolean = true,
    isPassword: Boolean,
    onBackground: Boolean = true,
    customKeyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    customKeyboardActions: KeyboardActions = KeyboardActions.Default,
    customVisualTransformation: VisualTransformation? = null,
    maxLines: Int = 1,
    minLines: Int = 1
) {
    var isPasswordVisible by rememberSaveable { mutableStateOf(!isPassword) }
    var dirty by rememberSaveable { mutableStateOf(false) }
    val requiredError = required && value.isEmpty() && dirty

    val containerColor =
        if (onBackground) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.background

    OutlinedTextField(
        value = value,
        textStyle = MaterialTheme.typography.bodyLarge,
        onValueChange = {
            onValueChange(it)
            if (!dirty) dirty = true
        },
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            disabledContainerColor = containerColor,
            errorContainerColor = containerColor,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary,
            // M3 nicely handles label colors automatically based on focus/error state
        ),
        modifier = modifier.fillMaxWidth(), // Applied outer modifier here
        isError = dirty && (requiredError || error != null),
        label = {
            if (label != null) {
                Text(text = label)
            }
        },
        supportingText = {
            if (dirty && (requiredError || error != null)) {
                Text(
                    text = when {
                        requiredError -> "This field is required"
                        else -> error ?: ""
                    }
                )
            }
        },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (isPasswordVisible) R.drawable.ic_eye_opened
                            else R.drawable.ic_eye_closed
                        ),
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                        tint = if (dirty && (requiredError || error != null))
                            MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.secondary
                    )
                }
            }
        },
        visualTransformation = customVisualTransformation ?: if (isPasswordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = customKeyboardOptions,
        keyboardActions = customKeyboardActions,
        singleLine = maxLines == 1 && minLines == 1,
        maxLines = maxLines,
        minLines = minLines
    )
}