package rs.raf.banka1.mobile.presentation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import rs.raf.banka1.mobile.presentation.screens.auth.EmailSentScreen
import rs.raf.banka1.mobile.presentation.screens.auth.ForgotPasswordScreen
import rs.raf.banka1.mobile.presentation.screens.auth.LoginScreen
import rs.raf.banka1.mobile.presentation.screens.dashboard.DashboardScreen
import rs.raf.banka1.mobile.presentation.screens.main.VerificationScreen

fun NavGraphBuilder.authNavGraph(navController: NavController) {
    navigation<Routes.AuthGraph>(startDestination = Routes.AuthFlow.Login) {
        composable<Routes.AuthFlow.Login> {
            LoginScreen(
                viewModel = hiltViewModel(),
                onNavigateToDashboard = {
                    navController.navigate(Routes.MainGraph) {
                        popUpTo(Routes.AuthGraph) { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.AuthFlow.ForgotPassword)
                },
            )
        }

        composable<Routes.AuthFlow.ForgotPassword> {
            ForgotPasswordScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() },
                onNavigateToConfirmation = { email ->
                    navController.navigate(Routes.AuthFlow.EmailSent(email)) {
                        popUpTo(Routes.AuthFlow.ForgotPassword) { inclusive = true }
                    }
                }
            )
        }

        composable<Routes.AuthFlow.EmailSent> { backStackEntry ->
            val route = backStackEntry.toRoute<Routes.AuthFlow.EmailSent>()
            EmailSentScreen(
                email = route.email,
                onNavigateToLogin = {
                    navController.navigate(Routes.AuthFlow.Login) {
                        popUpTo(Routes.AuthGraph) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

fun NavGraphBuilder.mainNavGraph(navController: NavController) {
    navigation<Routes.MainGraph>(startDestination = Routes.MainFlow.Dashboard) {
        composable<Routes.MainFlow.Dashboard> {
            DashboardScreen(
                viewModel = hiltViewModel(),
                onNavigateToVerification = {
                    navController.navigate(Routes.MainFlow.Verification) {
                        launchSingleTop = true
                    }
                },
                onNavigateToTransfers = {
                    navController.navigate(Routes.MainFlow.Transfers) {
                        launchSingleTop = true
                    }
                },
                onNavigateToPayments = {
                    navController.navigate(Routes.MainFlow.Payments) {
                        launchSingleTop = true
                    }
                },
                onNavigateToExchange = {
                    navController.navigate(Routes.MainFlow.Exchange) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<Routes.MainFlow.Accounts> {
            // TODO: AccountsListScreen
        }

        composable<Routes.MainFlow.AccountDetail> {
            // TODO: AccountDetailScreen
        }

        composable<Routes.MainFlow.CardDetail> {
            // TODO: CardDetailScreen
        }

        composable<Routes.MainFlow.Transfers> {
            // TODO: TransferScreen
        }

        composable<Routes.MainFlow.Payments> {
            // TODO: PaymentScreen
        }

        composable<Routes.MainFlow.History> {
            // TODO: HistoryScreen
        }

        composable<Routes.MainFlow.Exchange> {
            // TODO: ExchangeScreen
        }

        composable<Routes.MainFlow.Verification> {
            VerificationScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Routes.MainFlow.Profile> {
            // TODO: ProfileScreen
        }
    }
}