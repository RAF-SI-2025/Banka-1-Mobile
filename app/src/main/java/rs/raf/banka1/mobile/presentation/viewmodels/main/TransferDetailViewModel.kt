package rs.raf.banka1.mobile.presentation.viewmodels.main

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rs.raf.banka1.mobile.data.apis.TransferApi
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.responses.TransferResponseDto
import rs.raf.banka1.mobile.presentation.components.ErrorData
import rs.raf.banka1.mobile.presentation.navigation.Routes
import rs.raf.banka1.mobile.presentation.viewmodels.BaseMviViewModel
import javax.inject.Inject

@HiltViewModel
class TransferDetailViewModel @Inject constructor(
    private val transferApi: TransferApi,
    savedStateHandle: SavedStateHandle
) : BaseMviViewModel<TransferDetailContract.UiState, TransferDetailContract.UiEvent, TransferDetailContract.SideEffect>(
    TransferDetailContract.UiState()
) {

    private val route = savedStateHandle.toRoute<Routes.MainFlow.TransferDetail>()
    private val orderNumber = route.orderNumber

    init {
        setState { copy(fromCurrency = route.fromCurrency, toCurrency = route.toCurrency) }
        loadTransfer()
    }

    override fun setEvent(event: TransferDetailContract.UiEvent) {
        when (event) {
            is TransferDetailContract.UiEvent.Refresh -> loadTransfer()
            is TransferDetailContract.UiEvent.ClearError -> setState { copy(error = null) }
        }
    }

    private fun loadTransfer() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            when (val result = transferApi.getTransferByOrderNumber(orderNumber)) {
                is NetworkResult.Success -> setState {
                    copy(isLoading = false, transfer = result.data)
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
}

interface TransferDetailContract {
    data class UiState(
        val isLoading: Boolean = false,
        val transfer: TransferResponseDto? = null,
        val fromCurrency: String = "",
        val toCurrency: String = "",
        val error: ErrorData? = null
    )

    sealed interface UiEvent {
        data object Refresh : UiEvent
        data object ClearError : UiEvent
    }

    sealed interface SideEffect
}