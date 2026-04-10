package rs.raf.banka1.mobile.presentation.viewmodels.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rs.raf.banka1.mobile.data.apis.AccountApi
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.responses.AccountDetailsResponseDto
import rs.raf.banka1.mobile.presentation.components.ErrorData
import rs.raf.banka1.mobile.presentation.navigation.Routes
import rs.raf.banka1.mobile.presentation.viewmodels.BaseMviViewModel
import javax.inject.Inject

@HiltViewModel
class AccountDetailViewModel @Inject constructor(
    private val accountApi: AccountApi,
    savedStateHandle: SavedStateHandle
) : BaseMviViewModel<AccountDetailContract.UiState, AccountDetailContract.UiEvent, AccountDetailContract.SideEffect>(
    AccountDetailContract.UiState()
) {

    private val accountNumber: String = savedStateHandle.toRoute<Routes.MainFlow.AccountDetail>().accountNumber

    init {
        loadAccount()
    }

    override fun setEvent(event: AccountDetailContract.UiEvent) {
        when (event) {
            is AccountDetailContract.UiEvent.Refresh -> loadAccount()
            is AccountDetailContract.UiEvent.ClearError -> setState { copy(error = null) }
        }
    }

    private fun loadAccount() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            when (val result = accountApi.getAccountDetailsByNumber(accountNumber)) {
                is NetworkResult.Success -> {
                    setState { copy(isLoading = false, account = result.data) }
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

interface AccountDetailContract {
    data class UiState(
        val isLoading: Boolean = false,
        val account: AccountDetailsResponseDto? = null,
        val error: ErrorData? = null
    )

    sealed interface UiEvent {
        data object Refresh : UiEvent
        data object ClearError : UiEvent
    }

    sealed interface SideEffect
}