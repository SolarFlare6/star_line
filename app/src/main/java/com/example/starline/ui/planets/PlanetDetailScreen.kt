package com.example.starline.ui.planets

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.shape.CircleShape
import coil.compose.AsyncImage
import com.example.starline.data.NasaMediaData
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.starline.data.SpaceDataRepository
import com.example.starline.data.SettingsManager
import com.example.starline.theme.*

@Composable
fun PlanetDetailScreen(
    planetName: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val repository = remember(context) { SpaceDataRepository(context) }
    val settingsManager = remember(context) { SettingsManager(context) }
    var planet by remember { mutableStateOf(repository.planets.find { it.name == planetName }) }

    var nasaMedia by remember { mutableStateOf<NasaMediaData?>(null) }
    var isLoadingNasaMedia by remember { mutableStateOf(true) }

    LaunchedEffect(planetName) {
        // Fetch extended planet live stats from Solar System API
        val updatedPlanet = repository.fetchPlanetDetailsFromApi(planetName)
        if (updatedPlanet != null) {
            planet = updatedPlanet
        }
        // Fetch NASA image and description
        isLoadingNasaMedia = true
        val media = repository.fetchNasaImageAndDescription(planetName)
        nasaMedia = media
        isLoadingNasaMedia = false
    }

    LaunchedEffect(nasaMedia) {
        if (nasaMedia?.isRateLimited == true) {
            android.widget.Toast.makeText(context, "NASA API rate limit reached. Displaying cached information.", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    val activePlanet = planet
    if (activePlanet == null) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Planet not found", color = TextSecondary)
        }
        return
    }

    val primaryColor = remember(activePlanet) { Color(android.graphics.Color.parseColor(activePlanet.primaryColorHex)) }
    val secondaryColor = remember(activePlanet) { Color(android.graphics.Color.parseColor(activePlanet.secondaryColorHex)) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // Back button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.statusBarsPadding()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = StarWhite)
            }
            Text("Back to planets", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }

        Spacer(Modifier.height(16.dp))

        // Planet hero card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(SpaceSurface)
                .border(1.dp, SpaceBorder, RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Planet Canvas / NASA Image Orb
                Box(
                    modifier = Modifier.size(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer glow is always rendered in background for professional futuristic aesthetic
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(primaryColor.copy(alpha = 0.4f), Color.Transparent),
                                center = center,
                                radius = size.minDimension / 2
                            ),
                            radius = size.minDimension / 2,
                            center = center
                        )
                    }

                    if (isLoadingNasaMedia) {
                        CircularProgressIndicator(
                            color = primaryColor,
                            modifier = Modifier.size(40.dp)
                        )
                    } else if (nasaMedia?.imageUrl != null) {
                        AsyncImage(
                            model = nasaMedia?.imageUrl,
                            contentDescription = "${activePlanet.name} NASA Image",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(2.dp, primaryColor.copy(alpha = 0.8f), CircleShape),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        // Fallback planet body drawing
                        Canvas(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    listOf(primaryColor, secondaryColor),
                                    center = Offset(center.x - size.minDimension * 0.1f, center.y - size.minDimension * 0.1f),
                                    radius = size.minDimension / 2.8f
                                ),
                                radius = size.minDimension / 2.8f,
                                center = center
                            )
                        }
                    }
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(activePlanet.name, style = MaterialTheme.typography.headlineMedium, color = StarWhite, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(activePlanet.type, style = MaterialTheme.typography.bodySmall, color = TextSecondary, lineHeight = 16.sp)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Stats grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailStatCard("Type", activePlanet.type, Modifier.weight(1f))
            DetailStatCard("Distance from Sun", settingsManager.formatText(activePlanet.distance), Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailStatCard("Diameter", settingsManager.formatText(activePlanet.diameter), Modifier.weight(1f))
            DetailStatCard("Moons", "${activePlanet.moons}", Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailStatCard("Orbit Period", activePlanet.orbitPeriod, Modifier.weight(1f))
        }

        Spacer(Modifier.height(20.dp))

        // Description
        Text("About ${activePlanet.name}", style = MaterialTheme.typography.titleMedium, color = StarWhite, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SpaceSurface)
                .border(1.dp, SpaceBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(settingsManager.formatText(activePlanet.description), style = MaterialTheme.typography.bodyMedium, color = TextSecondary, lineHeight = 22.sp)
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
            Text("NASA Image Collage", style = MaterialTheme.typography.titleMedium, color = StarWhite, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            
            val urls = nasaMedia!!.collageUrls
            urls.chunked(2).forEach { rowUrls ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
private fun DetailStatCard(label: String, value: String, modifier: Modifier = Modifier) {
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
