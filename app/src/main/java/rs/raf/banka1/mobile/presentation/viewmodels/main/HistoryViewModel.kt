package rs.raf.banka1.mobile.presentation.viewmodels.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import rs.raf.banka1.mobile.data.apis.AccountApi
import rs.raf.banka1.mobile.data.apis.TransactionApi
import rs.raf.banka1.mobile.data.apis.TransferApi
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.responses.TransactionResponseDto
import rs.raf.banka1.mobile.data.remote.responses.TransferResponseDto
import rs.raf.banka1.mobile.data.repository.UserPreferencesRepository
import rs.raf.banka1.mobile.presentation.components.ErrorData
import rs.raf.banka1.mobile.presentation.viewmodels.BaseMviViewModel
import javax.inject.Inject
import kotlin.collections.flatten

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val transferApi: TransferApi,
    private val transactionApi: TransactionApi,
    private val accountApi: AccountApi, // Add AccountApi here
    private val userPreferencesRepository: UserPreferencesRepository
) : BaseMviViewModel<HistoryContract.UiState, HistoryContract.UiEvent, HistoryContract.SideEffect>(
    HistoryContract.UiState()
) {

    init {
        loadData()
    }

    override fun setEvent(event: HistoryContract.UiEvent) {
        when (event) {
            is HistoryContract.UiEvent.SelectTab -> setState { copy(selectedTab = event.tab) }
            is HistoryContract.UiEvent.Refresh -> loadData()
            is HistoryContract.UiEvent.ClearError -> setState { copy(error = null) }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            val clientId = userPreferencesRepository.readClientData().firstOrNull()?.id
            if (clientId == null) {
                setState {
                    copy(
                        isLoading = false,
                        error = ErrorData(
                            title = "Greska",
                            message = "Nije moguce ucitati istoriju."
                        )
                    )
                }
                return@launch
            }

            // 1. Fetch Transfers
            val transfers = when (val result = transferApi.getTransfers(clientId = clientId, page = 0, size = 100)) {
                is NetworkResult.Success -> result.data.content
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false, error = result.toErrorData()) }
                    return@launch
                }
                is NetworkResult.Exception -> {
                    setState { copy(isLoading = false, error = result.toErrorData()) }
                    return@launch
                }
                is NetworkResult.Ignored -> {
                    setState { copy(isLoading = false) }
                    return@launch
                }
            }

            val accountsResult = accountApi.getMyAccounts(page = 0, size = 100)
            val accountCurrencies = mutableMapOf<String, String>()

            val transactions = when (accountsResult) {
                is NetworkResult.Success -> {
                    val accountNumbers = accountsResult.data.content.mapNotNull { account ->
                        // Populate the currency map for later use!
                        if (account.brojRacuna != null && account.currency != null) {
                            accountCurrencies[account.brojRacuna] = account.currency
                        }
                        account.brojRacuna
                    }

                    // 3. Fetch Transactions for all accounts concurrently
                    val deferredTransactions = accountNumbers.map { accountNumber ->
                        async {
                            transactionApi.getTransactionsForAccount(
                                accountNumber = accountNumber,
                                page = 0,
                                size = 100
                            )
                        }
                    }

                    // Await all and flatten the successful results
                    deferredTransactions.awaitAll().mapNotNull { result ->
                        if (result is NetworkResult.Success) result.data.content else null
                    }.flatten().distinctBy { it.orderNumber }
                }
                is NetworkResult.Error -> {
                    setState { copy(isLoading = false, error = accountsResult.toErrorData()) }
                    return@launch
                }
                is NetworkResult.Exception -> {
                    setState { copy(isLoading = false, error = accountsResult.toErrorData()) }
                    return@launch
                }
                is NetworkResult.Ignored -> {
                    setState { copy(isLoading = false) }
                    return@launch
                }
            }

            setState {
                copy(
                    isLoading = false,
                    transfers = transfers.sortedByDescending { it.timestamp ?: "" },
                    transactions = transactions.sortedByDescending { it.createdAt ?: "" },
                    accountCurrencies = accountCurrencies // Save the map to state!
                )
            }
        }
    }
}

enum class HistoryTab { TRANSFERS, TRANSACTIONS }

interface HistoryContract {
    data class UiState(
        val isLoading: Boolean = false,
        val selectedTab: HistoryTab = HistoryTab.TRANSFERS,
        val transfers: List<TransferResponseDto> = emptyList(),
        val transactions: List<TransactionResponseDto> = emptyList(),
        val accountCurrencies: Map<String, String> = emptyMap(),
        val error: ErrorData? = null
    )

    sealed interface UiEvent {
        data class SelectTab(val tab: HistoryTab) : UiEvent
        data object Refresh : UiEvent
        data object ClearError : UiEvent
    }

    sealed interface SideEffect
}