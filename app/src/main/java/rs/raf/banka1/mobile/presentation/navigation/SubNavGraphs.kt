package rs.raf.banka1.mobile.presentation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import rs.raf.banka1.mobile.presentation.screens.accounts.AccountDetailScreen
import rs.raf.banka1.mobile.presentation.screens.accounts.AccountsCardsScreen
import rs.raf.banka1.mobile.presentation.screens.auth.EmailSentScreen
import rs.raf.banka1.mobile.presentation.screens.auth.ForgotPasswordScreen
import rs.raf.banka1.mobile.presentation.screens.auth.LoginScreen
import rs.raf.banka1.mobile.presentation.screens.cards.CardDetailScreen
import rs.raf.banka1.mobile.presentation.screens.dashboard.DashboardScreen
import rs.raf.banka1.mobile.presentation.screens.exchange.ExchangeScreen
import rs.raf.banka1.mobile.presentation.screens.main.VerificationScreen
import rs.raf.banka1.mobile.presentation.screens.profile.ProfileScreen

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
                onNavigateToExchange = {
                    navController.navigate(Routes.MainFlow.Exchange) {
                        launchSingleTop = true
                    }
                },
                onNavigateToAccounts = {
                    navController.navigate(Routes.MainFlow.Accounts) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<Routes.MainFlow.Accounts> {
            AccountsCardsScreen(
                viewModel = hiltViewModel(),
                onNavigateToAccountDetail = { accountNumber ->
                    navController.navigate(Routes.MainFlow.AccountDetail(accountNumber)) {
                        launchSingleTop = true
                    }
                },
                onNavigateToCardDetail = { accountNumber, cardNumber ->
                    navController.navigate(
                        Routes.MainFlow.CardDetail(accountNumber, cardNumber)
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<Routes.MainFlow.AccountDetail> {
            AccountDetailScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCardDetail = { cardNumber ->
                    val route = it.toRoute<Routes.MainFlow.AccountDetail>()
                    navController.navigate(
                        Routes.MainFlow.CardDetail(
                            accountNumber = route.accountNumber,
                            cardNumber = cardNumber
                        )
                    ) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<Routes.MainFlow.CardDetail> {
            CardDetailScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
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
            ExchangeScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Routes.MainFlow.Verification> {
            VerificationScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Routes.MainFlow.Profile> {
            ProfileScreen(
                viewModel = hiltViewModel(),
                onNavigateToLogin = {
                    navController.navigate(Routes.AuthGraph) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}