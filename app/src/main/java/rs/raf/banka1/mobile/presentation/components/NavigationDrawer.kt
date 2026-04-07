package rs.raf.banka1.mobile.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import rs.raf.banka1.mobile.presentation.navigation.Route
import rs.raf.banka1.mobile.presentation.navigation.Routes

enum class DrawerMenuItem(
    val icon: ImageVector,
    val label: String,
    val route: Route
) {
    DASHBOARD(Icons.Default.Home, "Dashboard", Routes.MainFlow.Dashboard),
    ACCOUNTS(Icons.Default.AccountBalance, "Accounts & Cards", Routes.MainFlow.Accounts),
    TRANSFERS(Icons.Default.SwapHoriz, "Transfers", Routes.MainFlow.Transfers),
    PAYMENTS(Icons.Default.Payment, "Payments", Routes.MainFlow.Payments),
    HISTORY(Icons.Default.Receipt, "History", Routes.MainFlow.History),
    EXCHANGE(Icons.Default.CurrencyExchange, "Exchange", Routes.MainFlow.Exchange),
    VERIFICATION(Icons.Default.VerifiedUser, "Verification", Routes.MainFlow.Verification),
    PROFILE(Icons.Default.Person, "Profile", Routes.MainFlow.Profile);
}

@Composable
fun BankaSideMenuLayout(
    selectedItem: DrawerMenuItem?,
    onItemClick: (DrawerMenuItem) -> Unit,
    drawerState: DrawerState,
    scope: CoroutineScope,
    clientName: String
) {
    ModalDrawerSheet {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Banka 1",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Text(
            text = clientName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(8.dp))

        DrawerMenuItem.entries.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = item == selectedItem,
                onClick = {
                    onItemClick(item)
                    scope.launch { drawerState.close() }
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}