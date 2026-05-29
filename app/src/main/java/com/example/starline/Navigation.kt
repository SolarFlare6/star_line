package com.example.starline

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.starline.data.FavoritesManager
import com.example.starline.data.SettingsManager
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
fun MainNavigation() {
    val context = LocalContext.current
    val favoritesManager = remember(context) { FavoritesManager(context) }
    val settingsManager = remember(context) { SettingsManager(context) }

    val authViewModel: AuthViewModel = viewModel()
    val currentUser by authViewModel.currentUser.collectAsState()

    // Route stack — start at Login unless already authenticated
    var appState by remember {
        mutableStateOf(AppState(route = if (currentUser != null) AppRoute.Main else AppRoute.Login))
    }

    // If user becomes authenticated (e.g. persistent session), jump to Main and sync favorites and settings
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            favoritesManager.syncFromFirebase()
            settingsManager.syncFromFirebase()
            if (appState.route in listOf(AppRoute.Login, AppRoute.Register)) {
                appState = appState.copy(route = AppRoute.Main)
            }
        }
    }

    // ── Back-press handler ───────────────────────────────────────────────
    // Enabled whenever the system back should do something other than close the app.
    val backEnabled = when (appState.route) {
        AppRoute.Login -> false                                   // let system handle (exit)
        AppRoute.Register -> true                                  // go back to Login
        AppRoute.Main -> appState.selectedTab != BottomTab.Home   // non-Home tab → go to Home
        else -> true                                               // detail / profile / settings
    }

    BackHandler(enabled = backEnabled) {
        when (appState.route) {
            AppRoute.Register ->
                appState = appState.copy(route = AppRoute.Login)
            AppRoute.Main ->
                appState = appState.copy(selectedTab = BottomTab.Home)
            AppRoute.PlanetDetail,
            AppRoute.SatelliteDetail,
            AppRoute.NewsDetail,
            AppRoute.Profile,
            AppRoute.Settings ->
                appState = appState.copy(route = AppRoute.Main)
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpaceBackground)
    ) {
        when (appState.route) {
            AppRoute.Login -> LoginScreen(
                onLoginSuccess = { appState = appState.copy(route = AppRoute.Main) },
                onNavigateToRegister = { appState = appState.copy(route = AppRoute.Register) },
                viewModel = authViewModel
            )

            AppRoute.Register -> RegisterScreen(
                onRegisterSuccess = { appState = appState.copy(route = AppRoute.Main) },
                onNavigateToLogin = { appState = appState.copy(route = AppRoute.Login) },
                viewModel = authViewModel
            )

            AppRoute.Main -> AppScaffold(
                selectedTab = appState.selectedTab,
                onTabChange = { tab -> appState = appState.copy(selectedTab = tab) },
                onNavigateToPlanetDetail = { name ->
                    appState = appState.copy(route = AppRoute.PlanetDetail, planetName = name)
                },
                onNavigateToSatelliteDetail = { name ->
                    appState = appState.copy(route = AppRoute.SatelliteDetail, satelliteName = name)
                },
                onNavigateToNewsDetail = { id ->
                    appState = appState.copy(route = AppRoute.NewsDetail, newsId = id)
                },
                onNavigateToProfile = { appState = appState.copy(route = AppRoute.Profile) },
                onNavigateToSettings = { appState = appState.copy(route = AppRoute.Settings) }
            )

            AppRoute.PlanetDetail -> PlanetDetailScreen(
                planetName = appState.planetName,
                onBack = { appState = appState.copy(route = AppRoute.Main) }
            )

            AppRoute.SatelliteDetail -> SatelliteDetailScreen(
                satelliteName = appState.satelliteName,
                onBack = { appState = appState.copy(route = AppRoute.Main) }
            )

            AppRoute.NewsDetail -> NewsDetailScreen(
                newsId = appState.newsId,
                onBack = { appState = appState.copy(route = AppRoute.Main) }
            )

            AppRoute.Profile -> ProfileScreen(
                onBack = { appState = appState.copy(route = AppRoute.Main) },
                onNavigateToSettings = { appState = appState.copy(route = AppRoute.Settings) },
                onLogout = {
                    appState = AppState(route = AppRoute.Login)
                },
                viewModel = authViewModel
            )

            AppRoute.Settings -> SettingsScreen(
                onBack = { appState = appState.copy(route = AppRoute.Main) }
            )
        }
    }
}
