package rs.raf.banka1.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import rs.raf.banka1.mobile.presentation.navigation.RootNavGraph
import rs.raf.banka1.mobile.presentation.viewmodels.SplashViewModel
import rs.raf.banka1.mobile.ui.theme.Banka1MobileTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition {
            viewModel.state.value is SplashViewModel.SplashUiState.Loading
        }

        setContent {
            Banka1MobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by viewModel.state.collectAsStateWithLifecycle()

                    when (val state = uiState) {
                        SplashViewModel.SplashUiState.Loading -> { /* splash screen visible */ }
                        is SplashViewModel.SplashUiState.Navigate -> {
                            val navController = rememberNavController()
                            RootNavGraph(
                                navController = navController,
                                startDestination = state.destination
                            )
                        }
                    }
                }
            }
        }
    }
}