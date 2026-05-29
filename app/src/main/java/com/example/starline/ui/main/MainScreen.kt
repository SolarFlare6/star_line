package com.example.starline.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.starline.theme.*
import com.example.starline.ui.components.StarfieldBackground
import com.example.starline.ui.news.NewsScreen
import com.example.starline.ui.planets.PlanetariumScreen
import com.example.starline.ui.satellites.SatellitesScreen

enum class BottomTab { Home, Planets, Satellites, News }

private data class NavItem(val tab: BottomTab, val icon: ImageVector, val label: String)

private val navItems = listOf(
    NavItem(BottomTab.Home, Icons.Default.Home, "Home"),
    NavItem(BottomTab.Planets, Icons.Default.RocketLaunch, "Planets"),
    NavItem(BottomTab.Satellites, Icons.Default.Satellite, "Satellites"),
    NavItem(BottomTab.News, Icons.Default.Newspaper, "News")
)

@Composable
fun AppScaffold(
    selectedTab: BottomTab,
    onTabChange: (BottomTab) -> Unit,
    onNavigateToPlanetDetail: (String) -> Unit,
    onNavigateToSatelliteDetail: (String) -> Unit,
    onNavigateToNewsDetail: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(modifier = modifier.fillMaxSize()) {
        StarfieldBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            // ── Top App Bar ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SpaceBackground.copy(alpha = 0.92f))
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(50))
                                .background(NeonPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✦", fontSize = 16.sp, color = NeonPrimary)
                        }
                        Text(
                            "Star Line",
                            style = MaterialTheme.typography.titleLarge,
                            color = StarWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Default.Settings, "Settings", tint = TextSecondary)
                        }
                        // Profile avatar button
                        IconButton(onClick = onNavigateToProfile) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(NeonPrimary.copy(alpha = 0.2f))
                                    .border(1.dp, NeonPrimary.copy(alpha = 0.5f), RoundedCornerShape(50)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👤", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }

            // ── Content Area ─────────────────────────────────────────────
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    BottomTab.Home -> HomeScreen(
                        onNavigateToPlanets   = { onTabChange(BottomTab.Planets) },
                        onNavigateToSatellites = { onTabChange(BottomTab.Satellites) },
                        onNavigateToNews      = { onTabChange(BottomTab.News) },
                        onNavigateToPlanetDetail = onNavigateToPlanetDetail,
                        onNavigateToSatelliteDetail = onNavigateToSatelliteDetail,
                        onNavigateToNewsDetail = onNavigateToNewsDetail
                    )
                    BottomTab.Planets    -> PlanetariumScreen(onPlanetClick = onNavigateToPlanetDetail)
                    BottomTab.Satellites -> SatellitesScreen(onSatelliteClick = onNavigateToSatelliteDetail)
                    BottomTab.News       -> NewsScreen(onNewsClick = onNavigateToNewsDetail)
                }
            }

            // ── Bottom Navigation Bar ────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SpaceBackground.copy(alpha = 0.96f))
                    .padding(vertical = 6.dp, horizontal = 8.dp)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    navItems.forEach { item ->
                        val isSelected = selectedTab == item.tab
                        val tint = if (isSelected) NeonPrimary else TextSecondary

                        IconButton(
                            onClick = { onTabChange(item.tab) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) NeonPrimary.copy(alpha = 0.15f) else Color.Transparent)
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Icon(item.icon, item.label, tint = tint, modifier = Modifier.size(22.dp))
                                }
                                Text(item.label, fontSize = 10.sp, color = tint)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Keep minimal MainScreen stub for any old references
@Composable
fun MainScreen(
    items: List<String> = emptyList(),
    onItemClick: (Any) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        items.forEach { item ->
            Text("Hello $item!")
        }
    }
}
