package com.example.starline.ui.planets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.starline.data.Planet
import com.example.starline.data.SpaceDataRepository
import com.example.starline.theme.*

@Composable
fun PlanetariumScreen(
    onPlanetClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val repository = remember { SpaceDataRepository() }

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 16.dp)) {
        Text("Planetarium", style = MaterialTheme.typography.headlineMedium, color = StarWhite, fontWeight = FontWeight.Bold)
        Text("Explore our solar system", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(Modifier.height(20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(repository.planets) { planet ->
                PlanetCard(planet = planet, onClick = { onPlanetClick(planet.name) })
            }
        }
    }
}

@Composable
private fun PlanetCard(planet: Planet, onClick: () -> Unit) {
    val primaryColor = remember(planet.primaryColorHex) { Color(android.graphics.Color.parseColor(planet.primaryColorHex)) }
    val secondaryColor = remember(planet.secondaryColorHex) { Color(android.graphics.Color.parseColor(planet.secondaryColorHex)) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SpaceSurface)
            .border(1.dp, SpaceBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            // Planet orb
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(primaryColor, secondaryColor))),
                contentAlignment = Alignment.Center
            ) {
                // Planet symbol
                val symbol = when (planet.name) {
                    "Mercury" -> "☿"
                    "Venus" -> "♀"
                    "Earth" -> "⊕"
                    "Mars" -> "♂"
                    "Jupiter" -> "♃"
                    "Saturn" -> "♄"
                    "Uranus" -> "♅"
                    "Neptune" -> "♆"
                    else -> "★"
                }
                Text(symbol, fontSize = 24.sp, color = StarWhite)
            }

            Spacer(Modifier.height(12.dp))
            Text(planet.name, style = MaterialTheme.typography.titleMedium, color = StarWhite, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            Text(planet.type, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            Text("Distance: ${planet.distance}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text("Moons: ${planet.moons}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}
