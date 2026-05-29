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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*
import com.example.starline.data.SpaceDataRepository
import com.example.starline.data.NasaMediaData
import coil.compose.AsyncImage
import com.example.starline.data.SettingsManager
import com.example.starline.theme.*

@Composable
fun SatelliteDetailScreen(
    satelliteName: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember(context) { SpaceDataRepository(context) }
    val settingsManager = remember(context) { SettingsManager(context) }
    val initialSatellite = remember(satelliteName) { repository.satellites.find { it.name == satelliteName } }

    if (initialSatellite == null) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Satellite not found", color = TextSecondary)
        }
        return
    }

    var satellite by remember { mutableStateOf(initialSatellite) }
    var nasaMedia by remember { mutableStateOf<NasaMediaData?>(null) }
    var isLoadingNasaMedia by remember { mutableStateOf(true) }

    LaunchedEffect(satelliteName) {
        isLoadingNasaMedia = true
        val media = repository.fetchNasaImageAndDescription(satelliteName)
        nasaMedia = media
        isLoadingNasaMedia = false
    }

    LaunchedEffect(nasaMedia) {
        if (nasaMedia?.isRateLimited == true) {
            android.widget.Toast.makeText(context, "NASA API rate limit reached. Displaying cached information.", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    if (satelliteName.equals("ISS", ignoreCase = true)) {
        LaunchedEffect(Unit) {
            while (true) {
                val updated = repository.fetchIssTelemetry()
                if (updated != null) {
                    satellite = updated
                }
                kotlinx.coroutines.delay(10000) // 10 seconds telemetry polling loop
            }
        }
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.statusBarsPadding()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = StarWhite)
            }
            Text("Back to satellites", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }

        Spacer(Modifier.height(16.dp))

        if (isLoadingNasaMedia) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SpaceSurface)
                    .border(1.dp, SpaceBorder, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NeonSecondary)
            }
            Spacer(Modifier.height(16.dp))
        } else if (nasaMedia?.imageUrl != null) {
            AsyncImage(
                model = nasaMedia?.imageUrl,
                contentDescription = "${satellite.name} NASA Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, SpaceBorder, RoundedCornerShape(20.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Spacer(Modifier.height(16.dp))
        }

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
            SatDetailCard("Altitude", settingsManager.formatText(satellite.altitude), Modifier.weight(1f))
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
            Text(settingsManager.formatText(satellite.description), style = MaterialTheme.typography.bodyMedium, color = TextSecondary, lineHeight = 22.sp)
        }

        // NASA Archives Log Section
        if (nasaMedia?.description != null) {
            Spacer(Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("NASA Archives Log", style = MaterialTheme.typography.titleMedium, color = StarWhite, fontWeight = FontWeight.Bold)
                if (nasaMedia?.isFromCache == true) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GlowAmber.copy(alpha = 0.15f))
                            .border(1.dp, GlowAmber.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (nasaMedia?.isRateLimited == true) "Cached (Rate Limit)" else "Cached Log",
                            style = MaterialTheme.typography.labelSmall,
                            color = GlowAmber
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SpaceSurface)
                    .border(1.dp, SpaceBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = nasaMedia?.description ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = 22.sp
                )
            }
        }

        // NASA Media Collage
        if (nasaMedia?.collageUrls?.isNotEmpty() == true) {
            Spacer(Modifier.height(24.dp))
            Text(
                "NASA Image Collage",
                style = MaterialTheme.typography.titleMedium,
                color = StarWhite,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))

            val urls = nasaMedia!!.collageUrls
            urls.chunked(2).forEach { rowUrls ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (url in rowUrls) {
                        AsyncImage(
                            model = url,
                            contentDescription = "NASA Collage Image",
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier
                                .weight(1f)
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, SpaceBorder, RoundedCornerShape(12.dp))
                        )
                    }
                    if (rowUrls.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
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
