package rs.raf.banka1.mobile.presentation.viewmodels.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import rs.raf.banka1.mobile.data.apis.ClientApi
import rs.raf.banka1.mobile.data.local.VerificationCodeDao
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.responses.ClientInfoResponseDto
import rs.raf.banka1.mobile.data.repository.UserPreferencesRepository
import rs.raf.banka1.mobile.presentation.components.ErrorData
import rs.raf.banka1.mobile.presentation.viewmodels.BaseMviViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val clientApi: ClientApi,
    private val verificationCodeDao: VerificationCodeDao,
    private val userPreferencesRepository: UserPreferencesRepository
) : BaseMviViewModel<ProfileContract.UiState, ProfileContract.UiEvent, ProfileContract.SideEffect>(
    ProfileContract.UiState()
) {

    init {
        loadProfile()
    }

    override fun setEvent(event: ProfileContract.UiEvent) {
        when (event) {
            ProfileContract.UiEvent.Refresh -> loadProfile()
            ProfileContract.UiEvent.ClearError -> setState { copy(error = null) }
            ProfileContract.UiEvent.Logout -> logout()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            val clientId = userPreferencesRepository.readClientData().firstOrNull()?.id
            if (clientId == null) {
                setState {
                    copy(
                        isLoading = false,
                        error = ErrorData(
                            title = "Greska",
                            message = "Nije moguce ucitati profil."
                        )
                    )
                }
                return@launch
            }

            when (val result = clientApi.getClientById(clientId)) {
                is NetworkResult.Success -> setState {
                    copy(isLoading = false, client = result.data)
                }
                is NetworkResult.Error -> setState {
                    copy(isLoading = false, error = result.toErrorData())
                }
                is NetworkResult.Exception -> setState {
                    copy(isLoading = false, error = result.toErrorData())
                }
                is NetworkResult.Ignored -> setState {
                    copy(isLoading = false)
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            verificationCodeDao.deleteAll()
            userPreferencesRepository.clearAll()
            sendEffect { ProfileContract.SideEffect.NavigateToLogin }
        }
    }
}

interface ProfileContract {
    data class UiState(
        val isLoading: Boolean = false,
        val client: ClientInfoResponseDto? = null,
        val error: ErrorData? = null
    )

    sealed interface UiEvent {
        data object Refresh : UiEvent
        data object ClearError : UiEvent
        data object Logout : UiEvent
    }

    sealed interface SideEffect {
        data object NavigateToLogin : SideEffect
    }
}