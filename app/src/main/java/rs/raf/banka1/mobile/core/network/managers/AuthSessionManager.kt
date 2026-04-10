package rs.raf.banka1.mobile.core.network.managers

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthSessionManager @Inject constructor() {

    private val _events = Channel<AuthSessionEvent>(
        capacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events = _events.receiveAsFlow()

    fun triggerForceLogout() {
        _events.trySend(AuthSessionEvent.ForceLogout)
    }

    sealed interface AuthSessionEvent {
        data object ForceLogout : AuthSessionEvent
    }
}