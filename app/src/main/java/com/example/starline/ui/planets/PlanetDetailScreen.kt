package com.example.starline.ui.planets

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.starline.data.SpaceDataRepository
import com.example.starline.theme.*

@Composable
fun PlanetDetailScreen(
    planetName: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val repository = remember { SpaceDataRepository() }
    val planet = remember(planetName) { repository.planets.find { it.name == planetName } }

    if (planet == null) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Planet not found", color = TextSecondary)
        }
        return
    }

    val primaryColor = remember { Color(android.graphics.Color.parseColor(planet.primaryColorHex)) }
    val secondaryColor = remember { Color(android.graphics.Color.parseColor(planet.secondaryColorHex)) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        // Back button
        Row(verticalAlignment = Alignment.CenterVertically) {
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
                // Planet canvas
                Canvas(
                    modifier = Modifier.size(140.dp)
                ) {
                    // Outer glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(primaryColor.copy(alpha = 0.3f), Color.Transparent),
                            center = center,
                            radius = size.minDimension / 2
                        ),
                        radius = size.minDimension / 2,
                        center = center
                    )
                    // Planet body
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

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(planet.name, style = MaterialTheme.typography.headlineMedium, color = StarWhite, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(planet.description.take(80) + "...", style = MaterialTheme.typography.bodySmall, color = TextSecondary, lineHeight = 16.sp)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Stats grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailStatCard("Type", planet.type, Modifier.weight(1f))
            DetailStatCard("Distance from Sun", planet.distance, Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailStatCard("Diameter", planet.diameter, Modifier.weight(1f))
            DetailStatCard("Moons", "${planet.moons}", Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailStatCard("Orbit Period", planet.orbitPeriod, Modifier.weight(1f))
        }

        Spacer(Modifier.height(20.dp))

        // Description
        Text("About ${planet.name}", style = MaterialTheme.typography.titleMedium, color = StarWhite, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SpaceSurface)
                .border(1.dp, SpaceBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(planet.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, lineHeight = 22.sp)
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
