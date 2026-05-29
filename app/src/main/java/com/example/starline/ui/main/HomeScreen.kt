package com.example.starline.ui.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.starline.data.ApodData
import com.example.starline.data.SpaceDataRepository
import com.example.starline.data.FavoritesManager
import com.example.starline.theme.*
import com.example.starline.ui.components.StarfieldBackground
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onNavigateToPlanets: () -> Unit,
    onNavigateToSatellites: () -> Unit,
    onNavigateToNews: () -> Unit,
    onNavigateToPlanetDetail: (String) -> Unit,
    onNavigateToSatelliteDetail: (String) -> Unit,
    onNavigateToNewsDetail: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember(context) { SpaceDataRepository(context) }
    val scope = rememberCoroutineScope()

    var currentFact by remember { mutableStateOf("Loading cosmic truth...") }
    var isFactLoading by remember { mutableStateOf(false) }

    var apodData by remember { mutableStateOf<ApodData?>(null) }
    var isApodLoading by remember { mutableStateOf(false) }

    val favoritesManager = remember(context) { FavoritesManager(context) }
    var favoritePlanets by remember { mutableStateOf(favoritesManager.getFavoritePlanets()) }
    var favoriteSatellites by remember { mutableStateOf(favoritesManager.getFavoriteSatellites()) }
    var favoriteArticles by remember { mutableStateOf(favoritesManager.getFavoriteArticles()) }

    var rotationAngle by remember { mutableStateOf(0f) }
    val animatedRotation by animateFloatAsState(
        targetValue = rotationAngle,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "syncRotation"
    )

    LaunchedEffect(Unit) {
        isFactLoading = true
        currentFact = repository.getNextFact()
        isFactLoading = false

        isApodLoading = true
        apodData = repository.fetchAstronomyPictureOfTheDay()
        isApodLoading = false
    }
    
    // Update favorites when returning to the screen and sync from Firebase
    LaunchedEffect(favoritesManager) {
        favoritePlanets = favoritesManager.getFavoritePlanets()
        favoriteSatellites = favoritesManager.getFavoriteSatellites()
        favoriteArticles = favoritesManager.getFavoriteArticles()

        val synced = favoritesManager.syncFromFirebase()
        if (synced) {
            favoritePlanets = favoritesManager.getFavoritePlanets()
            favoriteSatellites = favoritesManager.getFavoriteSatellites()
            favoriteArticles = favoritesManager.getFavoriteArticles()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Welcome header row with animated Sync button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Welcome to the Cosmos",
                        style = MaterialTheme.typography.headlineMedium,
                        color = StarWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Explore the wonders of our solar system and beyond",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                IconButton(
                    onClick = {
                        rotationAngle += 360f
                        scope.launch {
                            isFactLoading = true
                            currentFact = repository.getNextFact()
                            isFactLoading = false

                            isApodLoading = true
                            apodData = repository.fetchAstronomyPictureOfTheDay()
                            isApodLoading = false
                            
                            favoritesManager.syncFromFirebase()

                            favoritePlanets = favoritesManager.getFavoritePlanets()
                            favoriteSatellites = favoritesManager.getFavoriteSatellites()
                            favoriteArticles = favoritesManager.getFavoriteArticles()
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Sync,
                        contentDescription = "Sync Cosmos",
                        tint = NeonPrimary,
                        modifier = Modifier.size(24.dp).rotate(animatedRotation)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Random Fact Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(NeonPrimary.copy(alpha = 0.2f), NeonTertiary.copy(alpha = 0.15f))
                        )
                    )
                    .border(1.dp, NeonPrimary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Space Fact",
                            style = MaterialTheme.typography.titleMedium,
                            color = StarWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        if (isFactLoading) {
                            CircularProgressIndicator(
                                color = NeonPrimary,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                currentFact,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                lineHeight = 20.sp
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(NeonPrimary.copy(alpha = 0.3f))
                            .clickable(enabled = !isFactLoading) {
                                scope.launch {
                                    isFactLoading = true
                                    currentFact = repository.getNextFact()
                                    isFactLoading = false
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, null, tint = NeonPrimary, modifier = Modifier.size(28.dp))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Astronomy Picture of the Day (APOD) Section
            if (isApodLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(SpaceSurface)
                        .border(1.dp, SpaceBorder, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = NeonSecondary, modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(10.dp))
                        Text("Retrieving NASA Image...", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }
                Spacer(Modifier.height(24.dp))
            } else {
                apodData?.let { apod ->
                    Text("Picture of the Day", style = MaterialTheme.typography.titleMedium, color = StarWhite, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(SpaceSurface)
                            .border(1.dp, SpaceBorder, RoundedCornerShape(20.dp))
                    ) {
                        Column {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(apod.url)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = apod.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    apod.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = StarWhite,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    apod.date,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonSecondary
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    apod.explanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    lineHeight = 16.sp,
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
            
            if (favoritePlanets.isNotEmpty() || favoriteSatellites.isNotEmpty() || favoriteArticles.isNotEmpty()) {
                Text("Your Favorites", style = MaterialTheme.typography.titleMedium, color = StarWhite, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    favoritePlanets.forEach { planetName ->
                        FavoriteChip(name = planetName, icon = Icons.Default.RocketLaunch, onClick = { onNavigateToPlanetDetail(planetName) })
                    }
                    favoriteSatellites.forEach { satName ->
                        FavoriteChip(name = satName, icon = Icons.Default.Satellite, onClick = { onNavigateToSatelliteDetail(satName) })
                    }
                    favoriteArticles.forEach { article ->
                        FavoriteChip(
                            name = article.title.take(28) + if (article.title.length > 28) "…" else "",
                            icon = Icons.Default.Bookmark,
                            accentColor = NeonTertiary,
                            onClick = { onNavigateToNewsDetail(article.id) }
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            // Section cards row
            Text("Explore", style = MaterialTheme.typography.titleMedium, color = StarWhite, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickNavCard(
                    title = "Planetarium",
                    subtitle = "Explore all 8 planets in our solar system",
                    icon = Icons.Default.RocketLaunch,
                    accentColor = NeonPrimary,
                    onClick = onNavigateToPlanets,
                    modifier = Modifier.weight(1f)
                )
                QuickNavCard(
                    title = "Satellites",
                    subtitle = "Track 6 active space missions and satellites",
                    icon = Icons.Default.Satellite,
                    accentColor = NeonSecondary,
                    onClick = onNavigateToSatellites,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            QuickNavCard(
                title = "Space News & Facts",
                subtitle = "Latest discoveries and space exploration updates",
                icon = Icons.Default.Newspaper,
                accentColor = NeonTertiary,
                onClick = onNavigateToNews,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            // Stats overview
            Text("Quick Stats", style = MaterialTheme.typography.titleMedium, color = StarWhite, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatBadge("8", "Planets", NeonPrimary, Modifier.weight(1f))
                StatBadge("6", "Missions", NeonSecondary, Modifier.weight(1f))
                StatBadge("30+", "Facts", NeonTertiary, Modifier.weight(1f))
            }

            Spacer(Modifier.height(80.dp)) // Bottom nav padding
        }
    }
}

@Composable
private fun QuickNavCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SpaceSurface)
            .border(1.dp, SpaceBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = accentColor, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, color = StarWhite, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun StatBadge(value: String, label: String, accentColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SpaceSurface)
            .border(1.dp, SpaceBorder, RoundedCornerShape(14.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = accentColor)
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}

@Composable
private fun FavoriteChip(
    name: String,
    icon: ImageVector,
    onClick: () -> Unit,
    accentColor: Color = NeonPrimary
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SpaceSurface)
            .border(1.dp, SpaceBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = accentColor, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text(name, style = MaterialTheme.typography.bodyMedium, color = StarWhite, fontWeight = FontWeight.SemiBold)
        }
    }
}
