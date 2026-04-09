package rs.raf.banka1.mobile.presentation.viewmodels.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import rs.raf.banka1.mobile.data.apis.ExchangeApi
import rs.raf.banka1.mobile.data.local.ExchangeRateDao
import rs.raf.banka1.mobile.data.local.ExchangeRateEntity
import rs.raf.banka1.mobile.data.remote.NetworkResult
import rs.raf.banka1.mobile.data.remote.responses.ExchangeRateDto
import rs.raf.banka1.mobile.presentation.components.ErrorData
import rs.raf.banka1.mobile.presentation.viewmodels.BaseMviViewModel
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val exchangeApi: ExchangeApi,
    private val exchangeRateDao: ExchangeRateDao
) : BaseMviViewModel<ExchangeContract.UiState, ExchangeContract.UiEvent, ExchangeContract.SideEffect>(
    ExchangeContract.UiState()
) {

    init {
        loadRates()
    }

    override fun setEvent(event: ExchangeContract.UiEvent) {
        when (event) {
            is ExchangeContract.UiEvent.Refresh -> loadRates()
        }
    }

    private fun loadRates() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            when (val result = exchangeApi.getRates()) {
                is NetworkResult.Success -> {
                    val rates = result.data.filter { it.currencyCode != "RSD" }
                    // Cache locally
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
                    setState { copy(isLoading = false, rates = rates) }
                }
                is NetworkResult.Error -> {
                    // Try cached
                    val cached = loadCached()
                    setState { copy(isLoading = false, rates = cached, error = result.toErrorData()) }
                }
                is NetworkResult.Exception -> {
                    val cached = loadCached()
                    setState { copy(isLoading = false, rates = cached, error = result.toErrorData()) }
                }
                is NetworkResult.Ignored -> {
                    setState { copy(isLoading = false) }
                }
            }
        }
    }

    private suspend fun loadCached(): List<ExchangeRateDto> {
        return exchangeRateDao.getAll()
            .filter { it.currencyCode != "RSD" }
            .map { entity ->
                ExchangeRateDto(
                    currencyCode = entity.currencyCode,
                    buyingRate = entity.buyingRate,
                    sellingRate = entity.sellingRate,
                    date = entity.date
                )
            }
    }
}

interface ExchangeContract {
    data class UiState(
        val isLoading: Boolean = false,
        val rates: List<ExchangeRateDto> = emptyList(),
        val error: ErrorData? = null
    )

    sealed interface UiEvent {
        data object Refresh : UiEvent
    }

    sealed interface SideEffect
}