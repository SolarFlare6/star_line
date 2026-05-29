package com.example.starline.ui.satellites

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.starline.data.Satellite
import com.example.starline.data.FavoritesManager
import androidx.compose.ui.platform.LocalContext
import com.example.starline.data.SpaceDataRepository
import com.example.starline.data.SettingsManager
import com.example.starline.theme.*

@Composable
fun SatellitesScreen(
    onSatelliteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember(context) { SpaceDataRepository(context) }
    var satellitesList by remember { mutableStateOf(repository.satellites) }
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isRefreshing = true
        repository.refreshSatellites()
        satellitesList = repository.satellites
        isRefreshing = false
    }

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Satellites & Missions", style = MaterialTheme.typography.headlineMedium, color = StarWhite, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            if (isRefreshing) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = NeonSecondary, strokeWidth = 2.dp)
            }
        }
        Text("Track active space missions", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(satellitesList) { satellite ->
                SatelliteCard(satellite = satellite, onClick = { onSatelliteClick(satellite.name) })
            }
        }
    }
}

@Composable
private fun SatelliteCard(satellite: Satellite, onClick: () -> Unit) {
    val context = LocalContext.current
    val favoritesManager = remember(context) { FavoritesManager(context) }
    val settingsManager = remember(context) { SettingsManager(context) }
    var isFavorite by remember { mutableStateOf(favoritesManager.isSatelliteFavorite(satellite.name)) }

    val statusColor = when {
        satellite.status.contains("Active", ignoreCase = true) -> GlowGreen
        satellite.status.contains("Operational", ignoreCase = true) -> NeonSecondary
        else -> GlowAmber
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SpaceSurface)
            .border(1.dp, SpaceBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Status indicator
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(statusColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(satellite.name, style = MaterialTheme.typography.titleMedium, color = StarWhite, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(satellite.status, style = MaterialTheme.typography.labelSmall, color = statusColor, fontSize = 10.sp)
                }
                Spacer(Modifier.height(4.dp))
                Text(satellite.missionType, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Text("Altitude: ${settingsManager.formatText(satellite.altitude)}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }

            IconButton(
                onClick = { isFavorite = favoritesManager.toggleSatelliteFavorite(satellite.name) }
            ) {
                Icon(
                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) NeonPrimary else TextSecondary
                )
            }

            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TextSecondary)
        }
    }
}
