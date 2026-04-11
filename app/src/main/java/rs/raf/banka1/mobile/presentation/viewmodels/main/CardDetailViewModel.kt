package rs.raf.banka1.mobile.presentation.viewmodels.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rs.raf.banka1.mobile.data.apis.AccountApi
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.responses.AccountDetailsResponseDto
import rs.raf.banka1.mobile.data.remote.responses.CardResponseDto
import rs.raf.banka1.mobile.presentation.components.ErrorData
import rs.raf.banka1.mobile.presentation.navigation.Routes
import rs.raf.banka1.mobile.presentation.viewmodels.BaseMviViewModel
import javax.inject.Inject

@HiltViewModel
class CardDetailViewModel @Inject constructor(
    private val accountApi: AccountApi,
    savedStateHandle: SavedStateHandle
) : BaseMviViewModel<CardDetailContract.UiState, CardDetailContract.UiEvent, CardDetailContract.SideEffect>(
    CardDetailContract.UiState()
) {

    private val route = savedStateHandle.toRoute<Routes.MainFlow.CardDetail>()

    init {
        loadCard()
    }

    override fun setEvent(event: CardDetailContract.UiEvent) {
        when (event) {
            is CardDetailContract.UiEvent.Refresh -> loadCard()
            is CardDetailContract.UiEvent.ClearError -> setState { copy(error = null) }
        }
    }

    private fun loadCard() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            when (val result = accountApi.getAccountDetailsByNumber(route.accountNumber)) {
                is NetworkResult.Success -> {
                    val account = result.data
                    val card = account.cards?.firstOrNull { it.cardNumber == route.cardNumber }
                    setState {
                        copy(
                            isLoading = false,
                            card = card,
                            account = account
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

interface CardDetailContract {
    data class UiState(
        val isLoading: Boolean = false,
        val card: CardResponseDto? = null,
        val account: AccountDetailsResponseDto? = null,
        val error: ErrorData? = null
    )

    sealed interface UiEvent {
        data object Refresh : UiEvent
        data object ClearError : UiEvent
    }

    sealed interface SideEffect
}
