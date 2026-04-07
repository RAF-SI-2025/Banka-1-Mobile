package rs.raf.banka1.mobile.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseMviViewModel<STATE, EVENT, EFFECT>(
    initialState: STATE
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<STATE> = _state.asStateFlow()

    private val _effect = Channel<EFFECT>()
    val effect = _effect.receiveAsFlow()

    protected fun setState(reducer: STATE.() -> STATE) { _state.update(reducer) }

    protected fun sendEffect(effectBuilder: () -> EFFECT) {
        viewModelScope.launch { _effect.send(effectBuilder()) }
    }

    open fun setEvent(event: EVENT) {
        Log.d("BaseMvi", "setEvent called but not overridden in ${this::class.simpleName}")
    }
}