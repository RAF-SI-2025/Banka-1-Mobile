package rs.raf.banka1.mobile.presentation.navigation
import kotlinx.serialization.Serializable

sealed interface Route

object Routes {
    @Serializable
    data object AuthGraph : Route

    sealed interface AuthFlow : Route {
        @Serializable
        data object Login : AuthFlow

        @Serializable
        data object ForgotPassword : AuthFlow

        @Serializable
        data class Activate(val token: String) : AuthFlow

        @Serializable
        data class ResetPassword(val token: String) : AuthFlow
    }

    @Serializable
    data object MainGraph : Route

    sealed interface MainFlow : Route {
        @Serializable
        data object Dashboard : MainFlow

        @Serializable
        data object Accounts : MainFlow

        @Serializable
        data class AccountDetail(val accountId: Long) : MainFlow

        @Serializable
        data class CardDetail(val cardNumber: String) : MainFlow

        @Serializable
        data object Transfers : MainFlow

        @Serializable
        data object Payments : MainFlow

        @Serializable
        data object History : MainFlow

        @Serializable
        data object Exchange : MainFlow

        @Serializable
        data object Verification : MainFlow

        @Serializable
        data object Profile : MainFlow
    }
}