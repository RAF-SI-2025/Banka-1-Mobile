package rs.raf.banka1.mobile.presentation.viewmodels.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import rs.raf.banka1.mobile.data.apis.AccountApi
import rs.raf.banka1.mobile.data.local.VerificationCodeDao
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.responses.AccountDetailsResponseDto
import rs.raf.banka1.mobile.data.repository.UserPreferencesRepository
import rs.raf.banka1.mobile.presentation.components.ErrorData
import rs.raf.banka1.mobile.presentation.viewmodels.BaseMviViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val accountApi: AccountApi,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val verificationCodeDao: VerificationCodeDao
) : BaseMviViewModel<DashboardContract.UiState, DashboardContract.UiEvent, DashboardContract.SideEffect>(
    DashboardContract.UiState()
) {

    init {
        loadData()

        // Observe active verification code count for the badge
        verificationCodeDao.observeActiveCount(System.currentTimeMillis())
            .onEach { count -> setState { copy(activeVerificationCount = count) } }
            .launchIn(viewModelScope)
    }

    override fun setEvent(event: DashboardContract.UiEvent) {
        when (event) {
            is DashboardContract.UiEvent.Refresh -> loadData()
            is DashboardContract.UiEvent.ClearError -> setState { copy(error = null) }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            // Load client name from preferences
            val clientData = try {
                userPreferencesRepository.readClientData().firstOrNull()
            } catch (_: Exception) {
                null
            }
            setState { copy(clientName = clientData?.name ?: "") }

            // Load accounts
            when (val result = accountApi.getMyAccounts(page = 0, size = 10)) {
                is NetworkResult.Success -> {
                    val accounts = result.data.content
                    val totalBalance = accounts.sumOf { it.raspolozivoStanje ?: 0.0 }
                    val primaryCurrency = accounts.firstOrNull()?.currency ?: "RSD"
                    setState {
                        copy(
                            isLoading = false,
                            accounts = accounts,
                            totalBalance = totalBalance,
                            primaryCurrency = primaryCurrency
                        )
                    }
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
}

interface DashboardContract {
    data class UiState(
        val isLoading: Boolean = false,
        val clientName: String = "",
        val accounts: List<AccountDetailsResponseDto> = emptyList(),
        val totalBalance: Double = 0.0,
        val primaryCurrency: String = "RSD",
        val error: ErrorData? = null,
        val activeVerificationCount: Int = 0
    )

    sealed interface UiEvent {
        data object Refresh : UiEvent
        data object ClearError : UiEvent
    }

    sealed interface SideEffect {
        data object NavigateToVerification : SideEffect
    }
}
