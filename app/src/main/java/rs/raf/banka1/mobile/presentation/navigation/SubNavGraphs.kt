package rs.raf.banka1.mobile.presentation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import rs.raf.banka1.mobile.presentation.screens.auth.ForgotPasswordScreen
import rs.raf.banka1.mobile.presentation.screens.auth.LoginScreen
import rs.raf.banka1.mobile.presentation.screens.auth.ResetPasswordScreen

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
                onNavigateBack = { navController.popBackStack() }
            ) { }
        }

        composable<Routes.AuthFlow.Activate> {
            // TODO: ActivateScreen
        }

        composable<Routes.AuthFlow.ResetPassword> {
            ResetPasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            ) { }
        }
    }
}

fun NavGraphBuilder.mainNavGraph(navController: NavController) {
    navigation<Routes.MainGraph>(startDestination = Routes.MainFlow.Dashboard) {
        composable<Routes.MainFlow.Dashboard> {
            // TODO: DashboardScreen
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
            // TODO: VerificationScreen
        }

        composable<Routes.MainFlow.Profile> {
            // TODO: ProfileScreen
        }
    }
}