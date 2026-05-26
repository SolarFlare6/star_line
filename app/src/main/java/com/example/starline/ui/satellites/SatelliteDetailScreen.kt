package com.example.starline.ui.satellites

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.starline.data.SpaceDataRepository
import com.example.starline.theme.*

@Composable
fun SatelliteDetailScreen(
    satelliteName: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val repository = remember { SpaceDataRepository() }
    val satellite = remember(satelliteName) { repository.satellites.find { it.name == satelliteName } }

    if (satellite == null) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Satellite not found", color = TextSecondary)
        }
        return
    }

    val statusColor = when {
        satellite.status.contains("Active", ignoreCase = true) -> GlowGreen
        satellite.status.contains("Operational", ignoreCase = true) -> NeonSecondary
        else -> GlowAmber
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = StarWhite)
            }
            Text("Back to satellites", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }

        Spacer(Modifier.height(16.dp))

        // Header card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(SpaceSurface)
                .border(1.dp, SpaceBorder, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(statusColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(satellite.name, style = MaterialTheme.typography.headlineMedium, color = StarWhite, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(statusColor.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(satellite.status, style = MaterialTheme.typography.labelSmall, color = statusColor)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Info grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SatDetailCard("Mission Type", satellite.missionType, Modifier.weight(1f))
            SatDetailCard("Launch Date", satellite.launchDate, Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SatDetailCard("Altitude", satellite.altitude, Modifier.weight(1f))
            SatDetailCard("Status", satellite.status, Modifier.weight(1f))
        }

        Spacer(Modifier.height(20.dp))

        // Instrumentation
        Text("Main Instruments", style = MaterialTheme.typography.titleMedium, color = StarWhite, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SpaceSurface)
                .border(1.dp, SpaceBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(satellite.mainInstrument, style = MaterialTheme.typography.bodyMedium, color = NeonSecondary, lineHeight = 22.sp)
        }

        Spacer(Modifier.height(16.dp))

        // Description
        Text("About", style = MaterialTheme.typography.titleMedium, color = StarWhite, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SpaceSurface)
                .border(1.dp, SpaceBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(satellite.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, lineHeight = 22.sp)
        }

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun SatDetailCard(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SpaceSurface)
            .border(1.dp, SpaceBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, color = StarWhite, fontWeight = FontWeight.Bold)
        }
    }
}
