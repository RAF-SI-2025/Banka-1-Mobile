package rs.raf.banka1.mobile.presentation.viewmodels.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rs.raf.banka1.mobile.data.apis.AccountApi
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.responses.AccountDetailsResponseDto
import rs.raf.banka1.mobile.data.remote.responses.CardResponseDto
import rs.raf.banka1.mobile.presentation.components.ErrorData
import rs.raf.banka1.mobile.presentation.viewmodels.BaseMviViewModel
import javax.inject.Inject

@HiltViewModel
class AccountsCardsViewModel @Inject constructor(
    private val accountApi: AccountApi
) : BaseMviViewModel<AccountsCardsContract.UiState, AccountsCardsContract.UiEvent, AccountsCardsContract.SideEffect>(
    AccountsCardsContract.UiState()
) {

    init {
        loadData()
    }

    override fun setEvent(event: AccountsCardsContract.UiEvent) {
        when (event) {
            is AccountsCardsContract.UiEvent.SelectTab -> setState { copy(selectedTab = event.tab) }
            is AccountsCardsContract.UiEvent.Refresh -> loadData()
            is AccountsCardsContract.UiEvent.ClearError -> setState { copy(error = null) }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            when (val result = accountApi.getMyAccounts(page = 0, size = 100)) {
                is NetworkResult.Success -> {
                    val summaries = result.data.content

                    // List endpoint returns AccountResponseDto (no cards).
                    // Fetch full details (with cards) per account.
                    val accounts = summaries.map { summary ->
                        val number = summary.brojRacuna ?: return@map summary
                        when (val detail = accountApi.getAccountDetailsByNumber(number)) {
                            is NetworkResult.Success -> detail.data
                            else -> summary
                        }
                    }

                    val cards = accounts.flatMap { account ->
                        (account.cards ?: emptyList()).map { card ->
                            CardWithAccount(
                                card = card,
                                accountName = account.nazivRacuna ?: account.brojRacuna ?: "",
                                accountId = account.vlasnik
                            )
                        }
                    }
                    setState {
                        copy(
                            isLoading = false,
                            accounts = accounts,
                            cards = cards
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

data class CardWithAccount(
    val card: CardResponseDto,
    val accountName: String,
    val accountId: Long?
)

enum class AccountsCardsTab { ACCOUNTS, CARDS }

interface AccountsCardsContract {
    data class UiState(
        val isLoading: Boolean = false,
        val selectedTab: AccountsCardsTab = AccountsCardsTab.ACCOUNTS,
        val accounts: List<AccountDetailsResponseDto> = emptyList(),
        val cards: List<CardWithAccount> = emptyList(),
        val error: ErrorData? = null
    )

    sealed interface UiEvent {
        data class SelectTab(val tab: AccountsCardsTab) : UiEvent
        data object Refresh : UiEvent
        data object ClearError : UiEvent
    }

    sealed interface SideEffect
}
