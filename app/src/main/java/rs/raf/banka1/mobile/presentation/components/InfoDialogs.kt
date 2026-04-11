package rs.raf.banka1.mobile.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class ErrorData(
    val code: String? = null,
    val title: String,
    val message: String
)

@Composable
fun ErrorDialog(errorData: ErrorData?, onClose: () -> Unit) {
    errorData?.let {
        AppAlertDialog(
            title = {
                Text(
                    errorData.title,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        errorData.message,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmText = "Close",
            confirmAction = onClose,
            dismissAction = onClose,
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "error",
                    modifier = Modifier.size(36.dp)
                )
            },
            iconContentColor = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun SuccessDialog(title: String?, message: String?, onClose: () -> Unit) {
    AppAlertDialog(
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title ?: "Success",
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = message ?: "Operation completed successfully.",
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmText = "Close",
        confirmAction = onClose,
        dismissAction = onClose,
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "success",
                modifier = Modifier.size(36.dp)
            )
        },
        iconContentColor = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun AppAlertDialog(
    title: (@Composable () -> Unit)?,
    text: (@Composable () -> Unit)?,
    confirmText: String,
    confirmAction: () -> Unit,
    dismissText: String? = null,
    dismissAction: () -> Unit = {},
    icon: @Composable (() -> Unit)? = null,
    iconContentColor: Color = LocalContentColor.current
) {
    AlertDialog(
        onDismissRequest = { dismissAction() },
        title = {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.titleLarge) {
                title?.invoke()
            }
        },
        text = {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                text?.invoke()
            }
        },
        confirmButton = {
            TextButton(onClick = confirmAction) {
                Text(
                    confirmText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        dismissButton = dismissText?.let {
            {
                TextButton(onClick = { dismissAction() }) {
                    Text(
                        dismissText,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        },
        icon = icon,
        iconContentColor = iconContentColor
    )
}