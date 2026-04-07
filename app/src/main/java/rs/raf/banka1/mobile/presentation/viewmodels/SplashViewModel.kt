package rs.raf.banka1.mobile.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import rs.raf.banka1.mobile.data.repository.UserPreferencesRepository
import rs.raf.banka1.mobile.data.repository.UserPreferencesRepository.UserPreferencesKeys
import rs.raf.banka1.mobile.presentation.navigation.Route
import rs.raf.banka1.mobile.presentation.navigation.Routes
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    sealed interface SplashUiState {
        data object Loading : SplashUiState
        data class Navigate(val destination: Route) : SplashUiState
    }

    private val _state = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val state: StateFlow<SplashUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val token = userPreferencesRepository.read(UserPreferencesKeys.AUTH_TOKEN_KEY, "").first()

            _state.value = if (token.isNotEmpty()) {
                SplashUiState.Navigate(Routes.MainGraph)
            } else {
                SplashUiState.Navigate(Routes.AuthGraph)
            }
        }
    }
}