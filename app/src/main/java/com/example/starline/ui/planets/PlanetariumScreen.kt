package com.example.starline.ui.planets

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.starline.data.Planet
import com.example.starline.data.SpaceDataRepository
import com.example.starline.data.FavoritesManager
import com.example.starline.theme.*
import kotlinx.coroutines.launch

@Composable
fun PlanetariumScreen(
    onPlanetClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember(context) { SpaceDataRepository(context) }
    val scope = rememberCoroutineScope()

    var planetsList by remember { mutableStateOf(repository.planets) }
    var isRefreshing by remember { mutableStateOf(false) }

    var rotationAngle by remember { mutableStateOf(0f) }
    val animatedRotation by animateFloatAsState(
        targetValue = rotationAngle,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "planetSyncRotation"
    )

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Planetarium", style = MaterialTheme.typography.headlineMedium, color = StarWhite, fontWeight = FontWeight.Bold)
                Text("Explore our solar system", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }

            IconButton(
                enabled = !isRefreshing,
                onClick = {
                    rotationAngle += 360f
                    scope.launch {
                        isRefreshing = true
                        repository.refreshPlanets()
                        planetsList = repository.planets
                        isRefreshing = false
                    }
                }
            ) {
                Icon(
                    Icons.Default.Sync,
                    contentDescription = "Sync Solar System",
                    tint = NeonPrimary,
                    modifier = Modifier.size(24.dp).rotate(animatedRotation)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Pulse Live Sync Indicator Row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isRefreshing) GlowAmber else GlowGreen)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                if (isRefreshing) "Syncing Solar System OpenData..." else "Live Solar System Feeds Active",
                style = MaterialTheme.typography.labelSmall,
                color = if (isRefreshing) GlowAmber else GlowGreen
            )
        }

        Spacer(Modifier.height(20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(planetsList) { planet ->
                PlanetCard(planet = planet, onClick = { onPlanetClick(planet.name) })
            }
        }
    }
}

@Composable
private fun PlanetCard(planet: Planet, onClick: () -> Unit) {
    val context = LocalContext.current
    val favoritesManager = remember(context) { FavoritesManager(context) }
    var isFavorite by remember { mutableStateOf(favoritesManager.isPlanetFavorite(planet.name)) }

    val primaryColor = remember(planet.primaryColorHex) { Color(android.graphics.Color.parseColor(planet.primaryColorHex)) }
    val secondaryColor = remember(planet.secondaryColorHex) { Color(android.graphics.Color.parseColor(planet.secondaryColorHex)) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SpaceSurface)
            .border(1.dp, SpaceBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // Planet orb
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(primaryColor, secondaryColor)))
            )

            Spacer(Modifier.height(12.dp))
            Text(planet.name, style = MaterialTheme.typography.titleMedium, color = StarWhite, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            Text(planet.type, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            Text("Distance: ${planet.distance}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text("Moons: ${planet.moons}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
        
        IconButton(
            onClick = { isFavorite = favoritesManager.togglePlanetFavorite(planet.name) },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isFavorite) NeonPrimary else TextSecondary
            )
        }
    }
}
