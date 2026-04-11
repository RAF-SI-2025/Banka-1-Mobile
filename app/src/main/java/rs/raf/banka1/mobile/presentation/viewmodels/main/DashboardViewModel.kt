package rs.raf.banka1.mobile.presentation.viewmodels.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import rs.raf.banka1.mobile.data.apis.AccountApi
import rs.raf.banka1.mobile.data.apis.ExchangeApi
import rs.raf.banka1.mobile.data.local.ExchangeRateDao
import rs.raf.banka1.mobile.data.local.ExchangeRateEntity
import rs.raf.banka1.mobile.data.local.VerificationCodeDao
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.responses.AccountDetailsResponseDto
import rs.raf.banka1.mobile.data.remote.responses.ExchangeRateDto
import rs.raf.banka1.mobile.data.repository.UserPreferencesRepository
import rs.raf.banka1.mobile.presentation.components.ErrorData
import rs.raf.banka1.mobile.presentation.viewmodels.BaseMviViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val accountApi: AccountApi,
    private val exchangeApi: ExchangeApi,
    private val exchangeRateDao: ExchangeRateDao,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val verificationCodeDao: VerificationCodeDao
) : BaseMviViewModel<DashboardContract.UiState, DashboardContract.UiEvent, DashboardContract.SideEffect>(
    DashboardContract.UiState()
) {

    init {
        loadData()

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

            val clientData = try {
                userPreferencesRepository.readClientData().firstOrNull()
            } catch (_: Exception) {
                null
            }
            setState { copy(clientName = clientData?.name ?: "") }

            // Load exchange rates (try network first, fall back to cached)
            val rates = loadExchangeRates()
            setState { copy(exchangeRates = rates) }

            // Load accounts
            when (val result = accountApi.getMyAccounts(page = 0, size = 10)) {
                is NetworkResult.Success -> {
                    val accounts = result.data.content
                    val totalBalanceRsd = computeTotalBalanceRsd(accounts, rates)
                    val primaryCurrency = accounts.firstOrNull()?.currency ?: "RSD"
                    setState {
                        copy(
                            isLoading = false,
                            accounts = accounts,
                            totalBalanceRsd = totalBalanceRsd,
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

    private suspend fun loadExchangeRates(): List<ExchangeRateDto> {
        // Try fetching from network
        when (val result = exchangeApi.getRates()) {
            is NetworkResult.Success -> {
                val rates = result.data
                // Persist to local DB
                val entities = rates.mapNotNull { dto ->
                    val code = dto.currencyCode ?: return@mapNotNull null
                    ExchangeRateEntity(
                        currencyCode = code,
                        buyingRate = dto.buyingRate ?: 0.0,
                        sellingRate = dto.sellingRate ?: 0.0,
                        date = dto.date ?: ""
                    )
                }
                exchangeRateDao.insertAll(entities)
                return rates
            }
            else -> {
                // Fall back to cached rates
                return exchangeRateDao.getAll().map { entity ->
                    ExchangeRateDto(
                        currencyCode = entity.currencyCode,
                        buyingRate = entity.buyingRate,
                        sellingRate = entity.sellingRate,
                        date = entity.date
                    )
                }
            }
        }
    }

    private fun computeTotalBalanceRsd(
        accounts: List<AccountDetailsResponseDto>,
        rates: List<ExchangeRateDto>
    ): Double {
        val rateMap = rates.associate { (it.currencyCode ?: "") to (it.sellingRate ?: 1.0) }
        return accounts.sumOf { account ->
            val balance = account.raspolozivoStanje ?: 0.0
            val currency = account.currency ?: "RSD"
            if (currency == "RSD") balance
            else balance * (rateMap[currency] ?: 1.0)
        }
    }
}

interface DashboardContract {
    data class UiState(
        val isLoading: Boolean = false,
        val clientName: String = "",
        val accounts: List<AccountDetailsResponseDto> = emptyList(),
        val totalBalanceRsd: Double = 0.0,
        val primaryCurrency: String = "RSD",
        val error: ErrorData? = null,
        val activeVerificationCount: Int = 0,
        val exchangeRates: List<ExchangeRateDto> = emptyList()
    )

    sealed interface UiEvent {
        data object Refresh : UiEvent
        data object ClearError : UiEvent
    }

    sealed interface SideEffect {
        data object NavigateToVerification : SideEffect
    }
}