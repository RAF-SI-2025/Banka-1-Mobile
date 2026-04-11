package rs.raf.banka1.mobile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import rs.raf.banka1.mobile.core.network.managers.AuthSessionManager
import rs.raf.banka1.mobile.data.repository.UserPreferencesRepository
import rs.raf.banka1.mobile.presentation.navigation.RootNavGraph
import rs.raf.banka1.mobile.presentation.navigation.Routes
import rs.raf.banka1.mobile.presentation.viewmodels.SplashViewModel
import rs.raf.banka1.mobile.ui.theme.Banka1MobileTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModels()

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    @Inject
    lateinit var authSessionManager: AuthSessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition {
            viewModel.state.value is SplashViewModel.SplashUiState.Loading
        }

        // Request notification permission (required for Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001
                )
            }
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

                            // Handle FCM notification tap
                            LaunchedEffect(intent) {
                                val navigateTo = intent?.getStringExtra("navigate_to")
                                if (navigateTo == "verification" && state.destination is Routes.MainGraph) {
                                    navController.navigate(Routes.MainFlow.Verification) {
                                        launchSingleTop = true
                                    }
                                }
                            }

                            RootNavGraph(
                                navController = navController,
                                startDestination = state.destination,
                                userPreferencesRepository = userPreferencesRepository,
                                authSessionManager = authSessionManager
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }


}
