package rs.raf.banka1.mobile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import rs.raf.banka1.mobile.data.repository.ClientData
import rs.raf.banka1.mobile.presentation.navigation.Route
import rs.raf.banka1.mobile.presentation.navigation.Routes

// --- Side Menu State ---

sealed interface SideMenuState {
    data class Content(val clientData: ClientData) : SideMenuState
    data object Loading : SideMenuState
    data object Error : SideMenuState
}

// --- Drawer Menu Items ---

enum class DrawerMenuItem(
    val icon: ImageVector,
    val label: String,
    val route: Route
) {
    DASHBOARD(Icons.Default.Home, "Pocetna", Routes.MainFlow.Dashboard),
    ACCOUNTS(Icons.Default.AccountBalance, "Racuni i kartice", Routes.MainFlow.Accounts),
    HISTORY(Icons.Default.Receipt, "Istorija", Routes.MainFlow.History),
    EXCHANGE(Icons.Default.CurrencyExchange, "Menjacnica", Routes.MainFlow.Exchange),
    VERIFICATION(Icons.Default.VerifiedUser, "Verifikacija", Routes.MainFlow.Verification),
    PROFILE(Icons.Default.Person, "Profil", Routes.MainFlow.Profile);
}

// --- Main Side Menu Layout ---

@Composable
fun BankaSideMenuLayout(
    modifier: Modifier = Modifier,
    selectedItem: DrawerMenuItem?,
    onItemClick: (DrawerMenuItem) -> Unit,
    drawerContainerColor: Color = MaterialTheme.colorScheme.surface,
    drawerShape: Shape = RectangleShape,
    sideMenuState: SideMenuState
) {
    ModalDrawerSheet(
        drawerContainerColor = drawerContainerColor,
        drawerShape = drawerShape,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(32.dp))

            // Header
            ProfileHeaderCard(sideMenuState = sideMenuState)

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Menu Items
            DrawerMenuItem.entries.forEach { item ->
                BankaDrawerItem(
                    icon = item.icon,
                    text = item.label,
                    isSelected = item == selectedItem,
                    onClick = { onItemClick(item) }
                )
            }

            // Footer
            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.padding(vertical = 20.dp, horizontal = 14.dp)
            ) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))
                BankaLogoWidget()
            }
        }
    }
}

// --- Profile Header Card ---

@Composable
fun ProfileHeaderCard(sideMenuState: SideMenuState) {
    val clientData = (sideMenuState as? SideMenuState.Content)?.clientData

    Row(
        modifier = Modifier.padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Placeholder avatar
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            when (sideMenuState) {
                is SideMenuState.Content -> {
                    Text(
                        text = "${clientData!!.name} ${clientData.lastName}",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = clientData.email,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                is SideMenuState.Loading -> {
                    Text(
                        text = "Ucitavanje...",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                is SideMenuState.Error -> {
                    Text(
                        text = "Korisnik",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Greska pri ucitavanju",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

// --- Drawer Item (Lockdrive-style) ---

@Composable
fun BankaDrawerItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val itemColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.Transparent

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(bgColor)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = itemColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = itemColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

// --- Logo Footer Widget ---

@Composable
fun BankaLogoWidget() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "B1",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "Banka 1",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
