package rs.raf.banka1.mobile.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import rs.raf.banka1.mobile.data.repository.ClientData
import rs.raf.banka1.mobile.data.repository.UserPreferencesRepository
import rs.raf.banka1.mobile.presentation.components.BankaSideMenuLayout
import rs.raf.banka1.mobile.presentation.components.DrawerMenuItem
import rs.raf.banka1.mobile.presentation.components.SideMenuState

/**
 * Shared scroll progress (0f..1f) that the dashboard publishes as the user scrolls
 * past the greeting header, so the top bar title can fade between "Pocetna" and the
 * client's full name without the dashboard having to own the top bar.
 */
val LocalDashboardTopBarProgress = compositionLocalOf<MutableFloatState> {
    error("LocalDashboardTopBarProgress not provided")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootNavGraph(
    navController: NavHostController,
    startDestination: Route,
    userPreferencesRepository: UserPreferencesRepository? = null
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val isInMainFlow by remember {
        derivedStateOf {
            val currentRoute = navBackStackEntry?.destination?.route
            if (currentRoute == null) {
                // Before the first back-stack entry is available, fall back to startDestination
                startDestination is Routes.MainGraph || startDestination is Routes.MainFlow
            } else {
                currentRoute.startsWith(
                    Routes.MainGraph::class.qualifiedName?.substringBefore("$") ?: ""
                ) || DrawerMenuItem.entries.any { item ->
                    currentRoute.contains(
                        item.route::class.qualifiedName?.substringBefore("$") ?: ""
                    )
                }
            }
        }
    }

    val selectedDrawerItem by remember {
        derivedStateOf {
            val currentRoute = navBackStackEntry?.destination?.route
            DrawerMenuItem.entries.firstOrNull { item ->
                currentRoute?.contains(
                    item.route::class.qualifiedName?.substringBefore("$") ?: ""
                ) == true
            }
        }
    }

    // Load sidebar user data from preferences
    val sideMenuState by remember(userPreferencesRepository) {
        userPreferencesRepository?.readClientData()?.map { clientData ->
            if (clientData != null) {
                SideMenuState.Content(clientData)
            } else {
                SideMenuState.Error
            }
        } ?: kotlinx.coroutines.flow.flowOf(SideMenuState.Loading)
    }.collectAsState(initial = SideMenuState.Loading)

    val dashboardScrollProgress = remember { mutableFloatStateOf(0f) }
    val clientFullName = (sideMenuState as? SideMenuState.Content)
        ?.let { "${it.clientData.name} ${it.clientData.lastName}".trim() }
        ?.takeIf { it.isNotBlank() }
    val isOnDashboard = selectedDrawerItem == DrawerMenuItem.DASHBOARD

    if (isInMainFlow) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                BankaSideMenuLayout(
                    selectedItem = selectedDrawerItem,
                    onItemClick = { item ->
                        navController.navigate(item.route) {
                            popUpTo(Routes.MainFlow.Dashboard) { inclusive = false }
                            launchSingleTop = true
                        }
                        scope.launch { drawerState.close() }
                    },
                    sideMenuState = sideMenuState
                )
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            val defaultLabel = selectedDrawerItem?.label ?: "Banka 1"
                            if (isOnDashboard && clientFullName != null) {
                                val progress = dashboardScrollProgress.floatValue

                                // Fades OUT "Početna" during the first 40% of the scroll progress
                                val titleAlpha = (1f - (progress / 0.4f)).coerceIn(0f, 1f)

                                // Fades IN the Name during the last 40% of the scroll progress
                                // (Leaving a clean 20% gap in the middle where neither is visible)
                                val nameAlpha = ((progress - 0.6f) / 0.4f).coerceIn(0f, 1f)

                                Box {
                                    // Only draw them if they are actually visible to save composition power
                                    if (titleAlpha > 0f) {
                                        Text(
                                            text = defaultLabel,
                                            modifier = Modifier.alpha(titleAlpha)
                                        )
                                    }
                                    if (nameAlpha > 0f) {
                                        Text(
                                            text = clientFullName,
                                            modifier = Modifier.alpha(nameAlpha)
                                        )
                                    }
                                }
                            } else {
                                Text(defaultLabel)
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Meni")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    CompositionLocalProvider(
                        LocalDashboardTopBarProgress provides dashboardScrollProgress
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = startDestination
                        ) {
                            authNavGraph(navController)
                            mainNavGraph(navController)
                        }
                    }
                }
            }
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            authNavGraph(navController)
            mainNavGraph(navController)
        }
    }
}
