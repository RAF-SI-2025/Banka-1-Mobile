package rs.raf.banka1.mobile.presentation.viewmodels.auth

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.domain.repository.AuthRepository
import rs.raf.banka1.mobile.presentation.components.ErrorData
import rs.raf.banka1.mobile.presentation.viewmodels.BaseMviViewModel
import rs.raf.banka1.mobile.presentation.viewmodels.auth.ForgotPasswordContract.*
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseMviViewModel<UiState, UiEvent, SideEffect>(
    UiState()
) {

    override fun setEvent(event: UiEvent) {
        viewModelScope.launch {
            when (event) {
                is UiEvent.Submit -> sendResetEmail(event.email.trim())
                is UiEvent.ClearError -> setState { copy(error = null) }
            }
        }
    }

    private suspend fun sendResetEmail(email: String) {
        if (state.value.isLoading) return

        setState { copy(isLoading = true, error = null) }

        when (val result = authRepository.forgotPassword(email)) {
            is NetworkResult.Success -> {
                setState { copy(isLoading = false) }
                sendEffect { SideEffect.NavigateToConfirmation(email) }
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

interface ForgotPasswordContract {
    data class UiState(
        val isLoading: Boolean = false,
        val error: ErrorData? = null
    )

    sealed interface UiEvent {
        data class Submit(val email: String) : UiEvent
        data object ClearError : UiEvent
    }

    sealed interface SideEffect {
        data class NavigateToConfirmation(val email: String) : SideEffect
    }
}