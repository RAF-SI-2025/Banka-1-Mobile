package rs.raf.banka1.mobile.presentation.viewmodels.main

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rs.raf.banka1.mobile.data.apis.TransactionApi
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.responses.TransactionResponseDto
import rs.raf.banka1.mobile.presentation.components.ErrorData
import rs.raf.banka1.mobile.presentation.navigation.Routes
import rs.raf.banka1.mobile.presentation.viewmodels.BaseMviViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    moshi: Moshi // Assuming Moshi is provided in your Hilt modules.
) : BaseMviViewModel<TransactionDetailContract.UiState, TransactionDetailContract.UiEvent, TransactionDetailContract.SideEffect>(
    TransactionDetailContract.UiState(isLoading = true) // Start loading briefly while parsing
) {

    init {
        // Read the encoded JSON from the route parameters
        val route = savedStateHandle.toRoute<Routes.MainFlow.TransactionDetail>()
        val decodedJson = Uri.decode(route.transactionJson)

        // Parse the JSON back into your DTO
        val adapter = moshi.adapter(TransactionResponseDto::class.java)
        val transaction = adapter.fromJson(decodedJson)

        // Instantly set the state. No network call needed!
        setState {
            copy(isLoading = false, transaction = transaction)
        }
    }

    override fun setEvent(event: TransactionDetailContract.UiEvent) {
        when (event) {
            is TransactionDetailContract.UiEvent.Refresh -> {
                // No-op, data is already loaded statically from JSON
            }
            is TransactionDetailContract.UiEvent.ClearError -> setState { copy(error = null) }
        }
    }
}

interface TransactionDetailContract {
    data class UiState(
        val isLoading: Boolean = false,
        val transaction: TransactionResponseDto? = null,
        val error: ErrorData? = null
    )

    sealed interface UiEvent {
        data object Refresh : UiEvent
        data object ClearError : UiEvent
    }

    sealed interface SideEffect
}