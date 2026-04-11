package rs.raf.banka1.mobile.presentation.viewmodels.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import rs.raf.banka1.mobile.data.local.VerificationCodeDao
import rs.raf.banka1.mobile.data.local.VerificationCodeEntity
import rs.raf.banka1.mobile.presentation.viewmodels.BaseMviViewModel
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val verificationCodeDao: VerificationCodeDao
) : BaseMviViewModel<VerificationContract.UiState, VerificationContract.UiEvent, VerificationContract.SideEffect>(
    VerificationContract.UiState()
) {

    init {
        // Observe codes from Room
        verificationCodeDao.observeAll()
            .onEach { entities ->
                val now = System.currentTimeMillis()
                val codes = entities.map { it.toUiModel(now) }
                setState { copy(codes = codes, isLoading = false) }
            }
            .launchIn(viewModelScope)

        // Ticker for countdown updates (every second)
        viewModelScope.launch {
            while (true) {
                delay(1000)
                val now = System.currentTimeMillis()
                setState {
                    copy(codes = codes.map { code ->
                        val remaining = ((code.expiresAt - now) / 1000).coerceAtLeast(0)
                        code.copy(remainingSeconds = remaining)
                    })
                }
            }
        }
    }

    override fun setEvent(event: VerificationContract.UiEvent) {
        when (event) {
            is VerificationContract.UiEvent.MarkUsed -> {
                viewModelScope.launch {
                    verificationCodeDao.markUsed(event.id)
                }
            }
            is VerificationContract.UiEvent.Delete -> {
                viewModelScope.launch {
                    verificationCodeDao.deleteById(event.id)
                }
            }
            is VerificationContract.UiEvent.Refresh -> {
                viewModelScope.launch {
                    verificationCodeDao.deleteExpired(System.currentTimeMillis())
                }
            }
        }
    }
}

private fun VerificationCodeEntity.toUiModel(now: Long) = VerificationContract.VerificationCodeUiModel(
    id = id,
    code = code,
    operationType = operationType,
    sessionId = sessionId,
    receivedAt = receivedAt,
    expiresAt = expiresAt,
    isUsed = isUsed,
    remainingSeconds = ((expiresAt - now) / 1000).coerceAtLeast(0)
)

interface VerificationContract {

    data class UiState(
        val codes: List<VerificationCodeUiModel> = emptyList(),
        val isLoading: Boolean = true
    )

    sealed interface UiEvent {
        data class MarkUsed(val id: Long) : UiEvent
        data class Delete(val id: Long) : UiEvent
        data object Refresh : UiEvent
    }

    sealed interface SideEffect

    data class VerificationCodeUiModel(
        val id: Long,
        val code: String,
        val operationType: String,
        val sessionId: String,
        val receivedAt: Long,
        val expiresAt: Long,
        val isUsed: Boolean,
        val remainingSeconds: Long
    ) {
        val isExpired: Boolean get() = remainingSeconds <= 0 && !isUsed
        val isActive: Boolean get() = remainingSeconds > 0 && !isUsed

        val operationLabel: String get() = when (operationType) {
            "PAYMENT" -> "Placanje"
            "TRANSFER" -> "Transfer"
            "LIMIT_CHANGE" -> "Promena limita"
            "CARD_REQUEST" -> "Zahtev za karticu"
            "LOAN_REQUEST" -> "Zahtev za kredit"
            else -> operationType
        }

        val progress: Float get() {
            val totalDuration = 5 * 60L // 5 minutes in seconds
            return if (isActive) (remainingSeconds.toFloat() / totalDuration).coerceIn(0f, 1f) else 0f
        }
    }
}