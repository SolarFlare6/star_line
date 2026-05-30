package com.example.starline

import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.starline.data.FavoritesManager
import com.example.starline.data.SettingsManager
import com.example.starline.data.NetworkMonitor
import com.example.starline.theme.SpaceBackground
import com.example.starline.ui.auth.AuthViewModel
import com.example.starline.ui.auth.LoginScreen
import com.example.starline.ui.auth.RegisterScreen
import com.example.starline.ui.main.AppScaffold
import com.example.starline.ui.main.BottomTab
import com.example.starline.ui.planets.PlanetDetailScreen
import com.example.starline.ui.satellites.SatelliteDetailScreen
import com.example.starline.ui.news.NewsDetailScreen
import com.example.starline.ui.profile.ProfileScreen
import com.example.starline.ui.settings.SettingsScreen

enum class AppRoute {
    Login, Register, Main, PlanetDetail, SatelliteDetail, NewsDetail, Profile, Settings
}

data class AppState(
    val route: AppRoute = AppRoute.Login,
    val planetName: String = "",
    val satelliteName: String = "",
    val newsId: String = "",
    val selectedTab: BottomTab = BottomTab.Home
)

@Composable
fun MainNavigation(navigationViewModel: NavigationViewModel = viewModel()) {
    val context = LocalContext.current
    val favoritesManager = remember(context) { FavoritesManager(context) }
    val settingsManager = remember(context) { SettingsManager(context) }

    val authViewModel: AuthViewModel = viewModel()
    val currentUser by authViewModel.currentUser.collectAsState()

    val networkMonitor = remember(context) { NetworkMonitor(context) }
    val isOnline by networkMonitor.isOnlineFlow.collectAsState(initial = networkMonitor.isOnline())
    var wasOfflineAtStart by rememberSaveable { mutableStateOf(!networkMonitor.isOnline()) }
    var offlineToastShown by remember { mutableStateOf(false) }

    val appState = navigationViewModel.appState

    // Helper so each screen can navigate without touching the ViewModel directly
    fun navigate(newState: AppState) = navigationViewModel.updateState(newState)

    // Jump to Main when user logs in; sync cloud data
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            if (!networkMonitor.isOnline() && !offlineToastShown) {
                android.widget.Toast.makeText(context, "Logged in offline", android.widget.Toast.LENGTH_SHORT).show()
                offlineToastShown = true
            }
            if (networkMonitor.isOnline()) {
                favoritesManager.syncFromFirebase()
                settingsManager.syncFromFirebase()
            }
            if (appState.route == AppRoute.Login || appState.route == AppRoute.Register) {
                navigate(appState.copy(route = AppRoute.Main))
            }
        }
    }

    LaunchedEffect(isOnline) {
        if (isOnline && wasOfflineAtStart) {
            authViewModel.verifySession { isValid ->
                if (isValid) {
                    android.widget.Toast.makeText(context, "Connection restored. Reloading cosmos...", android.widget.Toast.LENGTH_LONG).show()
                    wasOfflineAtStart = false
                    context.findActivity()?.recreate()
                } else {
                    android.widget.Toast.makeText(context, "Session expired. Please log in again.", android.widget.Toast.LENGTH_LONG).show()
                    wasOfflineAtStart = false
                    navigate(AppState(route = AppRoute.Login))
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceBackground)
    ) {
        when (appState.route) {
            AppRoute.Login -> LoginScreen(
                onLoginSuccess   = { navigate(appState.copy(route = AppRoute.Main)) },
                onNavigateToRegister = { navigate(appState.copy(route = AppRoute.Register)) },
                viewModel = authViewModel
            )

            AppRoute.Register -> RegisterScreen(
                onRegisterSuccess = { navigate(appState.copy(route = AppRoute.Main)) },
                onNavigateToLogin = { navigate(appState.copy(route = AppRoute.Login)) },
                viewModel = authViewModel
            )

            AppRoute.Main -> AppScaffold(
                selectedTab = appState.selectedTab,
                onTabChange = { tab -> navigate(appState.copy(selectedTab = tab)) },
                onNavigateToPlanetDetail = { name ->
                    navigate(appState.copy(route = AppRoute.PlanetDetail, planetName = name))
                },
                onNavigateToSatelliteDetail = { name ->
                    navigate(appState.copy(route = AppRoute.SatelliteDetail, satelliteName = name))
                },
                onNavigateToNewsDetail = { id ->
                    navigate(appState.copy(route = AppRoute.NewsDetail, newsId = id))
                },
                onNavigateToProfile  = { navigate(appState.copy(route = AppRoute.Profile)) },
                onNavigateToSettings = { navigate(appState.copy(route = AppRoute.Settings)) }
            )

            AppRoute.PlanetDetail -> PlanetDetailScreen(
                planetName = appState.planetName,
                onBack = { navigate(appState.copy(route = AppRoute.Main)) }
            )

            AppRoute.SatelliteDetail -> SatelliteDetailScreen(
                satelliteName = appState.satelliteName,
                onBack = { navigate(appState.copy(route = AppRoute.Main)) }
            )

            AppRoute.NewsDetail -> NewsDetailScreen(
                newsId = appState.newsId,
                onBack = { navigate(appState.copy(route = AppRoute.Main)) }
            )

            AppRoute.Profile -> ProfileScreen(
                onBack = { navigate(appState.copy(route = AppRoute.Main)) },
                onNavigateToSettings = { navigate(appState.copy(route = AppRoute.Settings)) },
                onLogout = { navigate(AppState(route = AppRoute.Login)) },
                viewModel = authViewModel
            )

            AppRoute.Settings -> SettingsScreen(
                onBack = { navigate(appState.copy(route = AppRoute.Main)) }
            )
        }
    }
}

fun android.content.Context.findActivity(): ComponentActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is ComponentActivity) return context
        context = context.baseContext
    }
    return null
}
