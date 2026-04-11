package rs.raf.banka1.mobile.presentation.viewmodels.auth

import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import rs.raf.banka1.mobile.data.apis.NotificationApi
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.requests.FcmTokenRequest
import rs.raf.banka1.mobile.data.repository.ClientData
import rs.raf.banka1.mobile.data.repository.UserPreferencesRepository
import rs.raf.banka1.mobile.domain.repository.AuthRepository
import rs.raf.banka1.mobile.presentation.components.ErrorData
import rs.raf.banka1.mobile.presentation.viewmodels.BaseMviViewModel
import rs.raf.banka1.mobile.presentation.viewmodels.auth.LoginContract.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val notificationApi: NotificationApi,
    private val userPreferencesRepository: UserPreferencesRepository
) : BaseMviViewModel<UiState, UiEvent, SideEffect>(
    UiState()
) {

    override fun setEvent(event: UiEvent) {
        viewModelScope.launch {
            when (event) {
                is UiEvent.SendCredentials -> performLogin(event.email.trim(), event.password)
                is UiEvent.ClearError -> setState { copy(error = null) }
            }
        }
    }

    private suspend fun registerFcmToken(clientId: Long) {
        try {
            val token = FirebaseMessaging.getInstance().token.await()
            userPreferencesRepository.saveFcmToken(token)
            notificationApi.registerFcmToken(FcmTokenRequest(clientId = clientId, fcmToken = token))
        } catch (e: Exception) {
            // best-effort; login still succeeds
        }
    }

    private suspend fun performLogin(email: String, password: String) {
        if (state.value.isLoading) return

        setState { copy(isLoading = true, error = null) }

        when (val result = authRepository.login(email, password)) {
            is NetworkResult.Success -> {
                val response = result.data
                userPreferencesRepository.saveAuthToken(response.token)
                userPreferencesRepository.saveClientData(
                    ClientData(
                        id = response.id,
                        name = response.ime,
                        lastName = response.prezime,
                        email = response.email
                    )
                )
                registerFcmToken(response.id)
                setState { copy(isLoading = false) }
                sendEffect { SideEffect.NavigateToDashboard }
            }
            is NetworkResult.Error -> {
                setState { copy(isLoading = false, error = result.toErrorData()) }
            }
            is NetworkResult.Exception -> {
                setState { copy(isLoading = false, error = result.toErrorData()) }
            }
            is NetworkResult.Ignored -> {
                setState { copy(isLoading = false) }
            }
        }
    }
}

interface LoginContract {
    data class UiState(
        val isLoading: Boolean = false,
        val error: ErrorData? = null
    )

    sealed interface UiEvent {
        data class SendCredentials(val email: String, val password: String) : UiEvent
        data object ClearError : UiEvent
    }

    sealed interface SideEffect {
        data object NavigateToDashboard : SideEffect
    }
}