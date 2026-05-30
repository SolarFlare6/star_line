package com.example.starline.ui.planets

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.shape.CircleShape
import coil.compose.AsyncImage
import com.example.starline.data.NasaMediaData
import com.example.starline.data.Planet
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.starline.data.SpaceDataRepository
import com.example.starline.data.SettingsManager
import com.example.starline.theme.*
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.PI

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
    var show3DViewer by remember { mutableStateOf(false) }

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
            if (show3DViewer) {
                Planet3DSpinDialog(planet = activePlanet, onDismiss = { show3DViewer = false })
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Planet Simplistic Orb (Clickable to open 3D interactive viewer)
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .clickable { show3DViewer = true },
                    contentAlignment = Alignment.Center
                ) {
                    // Outer glow represents the atmosphere
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

                    // Inner gradient representing the simplistic planet body
                    Canvas(
                        modifier = Modifier.size(100.dp)
                    ) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(primaryColor, secondaryColor),
                                center = Offset(center.x - size.minDimension * 0.1f, center.y - size.minDimension * 0.1f),
                                radius = size.minDimension / 2
                            ),
                            radius = size.minDimension / 2,
                            center = center
                        )
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
                Column {
                    if (nasaMedia?.imageUrl != null) {
                        AsyncImage(
                            model = nasaMedia?.imageUrl,
                            contentDescription = "${activePlanet.name} NASA Archival Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, SpaceBorder, RoundedCornerShape(12.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    Text(
                        text = nasaMedia?.description ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )
                }
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
            .clip(RoundedCornerShape(16.dp))
            .background(SpaceSurface)
            .border(1.dp, SpaceBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.bodyMedium, color = StarWhite, fontWeight = FontWeight.SemiBold)
        }
    }
}

private data class Landmark3D(val lat: Float, val lon: Float, val color: Color, val size: Float = 6f)

private fun generateLandmarks(planetName: String): List<Landmark3D> {
    val list = mutableListOf<Landmark3D>()
    when (planetName.lowercase()) {
        "mercury" -> {
            val r = java.util.Random(42)
            // Large ancient basins (very dark)
            listOf(30f to -30f, -45f to 80f, 60f to 120f, -20f to -140f, 10f to 170f).forEach { (lat, lon) ->
                list.add(Landmark3D(lat, lon, Color(0xFF303030), size = 16f + r.nextFloat() * 10f))
            }
            // Medium craters with lighter ejecta rim
            for (i in 0 until 25) {
                val lat = r.nextFloat() * 160 - 80
                val lon = r.nextFloat() * 360 - 180
                val sz = 5f + r.nextFloat() * 7f
                list.add(Landmark3D(lat, lon, Color(0xFF424242), size = sz))
                list.add(Landmark3D(lat + r.nextFloat() * 1.5f - 0.75f, lon + r.nextFloat() * 1.5f - 0.75f, Color(0xFF757575), size = sz * 1.8f))
                list.add(Landmark3D(lat, lon, Color(0xFF212121), size = sz * 0.4f))
            }
            // Fine highland texture
            for (i in 0 until 40) {
                val lat = r.nextFloat() * 160 - 80
                val lon = r.nextFloat() * 360 - 180
                list.add(Landmark3D(lat, lon, Color(0xFF616161).copy(alpha = 0.6f), size = 2f + r.nextFloat() * 3f))
            }
        }
        "venus" -> {
            val r = java.util.Random(101)
            // Bright cloud wisps
            for (i in 0 until 30) {
                val lat = r.nextFloat() * 120 - 60
                val lon = r.nextFloat() * 360 - 180
                list.add(Landmark3D(lat, lon, Color(0xFFFFF9C4).copy(alpha = 0.75f), size = 10f + r.nextFloat() * 18f))
            }
            // Darker storm cells
            for (i in 0 until 20) {
                val lat = r.nextFloat() * 100 - 50
                val lon = r.nextFloat() * 360 - 180
                list.add(Landmark3D(lat, lon, Color(0xFFE65100).copy(alpha = 0.35f), size = 8f + r.nextFloat() * 14f))
            }
            // Subtle cloud texture layer
            for (i in 0 until 50) {
                val lat = r.nextFloat() * 140 - 70
                val lon = r.nextFloat() * 360 - 180
                list.add(Landmark3D(lat, lon, Color(0xFFFFE082).copy(alpha = 0.25f), size = 4f + r.nextFloat() * 8f))
            }
        }
        "earth" -> {
            // Eurasia & Africa (dense)
            for (lat in -35..75 step 5) {
                for (lon in -20..145 step 5) {
                    val inAfrica = lat in -35..37 && lon in -18..51
                    val inEurope = lat in 36..72 && lon in -10..40
                    val inAsia   = lat in 0..75  && lon in 40..145
                    if (inAfrica || inEurope || inAsia) {
                        val shade = (0xFF388E3C.toLong() + java.util.Random((lat * 1000 + lon).toLong()).nextInt(0x1A0000)).toInt()
                        list.add(Landmark3D(lat.toFloat(), lon.toFloat(), Color(0xFF2E7D32), size = 5f))
                    }
                }
            }
            // Americas
            for (lat in -55..75 step 5) {
                for (lon in -130..-35 step 5) {
                    val inNorth  = lat > 15  && lon < -65
                    val inCentral = lat in -10..15 && lon in -85..-45
                    val inSouth  = lat < -10 && lon in -80..-35
                    if (inNorth || inCentral || inSouth) {
                        list.add(Landmark3D(lat.toFloat(), lon.toFloat(), Color(0xFF388E3C), size = 5f))
                    }
                }
            }
            // Australia
            for (lat in -40..-15 step 5) {
                for (lon in 114..154 step 5) {
                    list.add(Landmark3D(lat.toFloat(), lon.toFloat(), Color(0xFF33691E), size = 5f))
                }
            }
            // Antarctica ice sheet
            for (lon in -180..180 step 8) {
                list.add(Landmark3D(-80f, lon.toFloat(), Color(0xFFECEFF1), size = 8f))
                list.add(Landmark3D(-72f, lon.toFloat(), Color(0xFFCFD8DC), size = 5f))
            }
            // Ocean sparkle patches
            val ro = java.util.Random(77)
            for (i in 0 until 20) {
                list.add(Landmark3D(ro.nextFloat() * 120 - 60, ro.nextFloat() * 360 - 180, Color(0xFF0288D1).copy(alpha = 0.3f), size = 8f + ro.nextFloat() * 10f))
            }
            // Cloud wisps
            for (i in 0 until 25) {
                list.add(Landmark3D(ro.nextFloat() * 140 - 70, ro.nextFloat() * 360 - 180, Color(0xFFFFFFFF).copy(alpha = 0.25f), size = 6f + ro.nextFloat() * 12f))
            }
        }
        "mars" -> {
            val r = java.util.Random(99)
            // Large dark highland regions
            for (i in 0 until 22) {
                list.add(Landmark3D(r.nextFloat() * 100 - 50, r.nextFloat() * 360 - 180, Color(0xFF5D4037), size = 12f + r.nextFloat() * 14f))
            }
            // Valles Marineris canyon system (dark equatorial scar)
            for (lon in -80..0 step 6) {
                val latJitter = r.nextFloat() * 4 - 2
                list.add(Landmark3D(-5f + latJitter, lon.toFloat(), Color(0xFF3E2723), size = 7f))
            }
            // Tharsis bulge (lighter reddish volcanic plateau)
            for (lon in -140..-100 step 8) {
                for (lat in 5..25 step 8) {
                    list.add(Landmark3D(lat.toFloat(), lon.toFloat(), Color(0xFF8D6E63), size = 10f))
                }
            }
            // Olympus Mons shield volcano
            list.add(Landmark3D(18f, -134f, Color(0xFF795548), size = 22f))
            list.add(Landmark3D(18f, -134f, Color(0xFF6D4C41), size = 10f))
            // Hellas Basin (giant dark depression)
            list.add(Landmark3D(-42f, 70f, Color(0xFF4E342E), size = 28f))
            // Fine dust texture
            for (i in 0 until 50) {
                list.add(Landmark3D(r.nextFloat() * 160 - 80, r.nextFloat() * 360 - 180, Color(0xFF795548).copy(alpha = 0.4f), size = 3f + r.nextFloat() * 6f))
            }
        }
        // Gas giants: landmark dots no longer used — bands rendered via drawSphereBand
        "jupiter", "saturn", "uranus", "neptune" -> { /* bands drawn directly */ }
    }
    return list
}

@Composable
fun Planet3DSpinDialog(
    planet: Planet,
    onDismiss: () -> Unit
) {
    var rotX by remember { mutableStateOf(0f) }
    var rotY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(isDragging) {
        if (!isDragging) {
            while (true) {
                rotX = (rotX + 0.6f) % 360f
                kotlinx.coroutines.delay(16)
            }
        }
    }

    val primaryColor = remember(planet) { Color(android.graphics.Color.parseColor(planet.primaryColorHex)) }
    val secondaryColor = remember(planet) { Color(android.graphics.Color.parseColor(planet.secondaryColorHex)) }
    val landmarks = remember(planet.name) { generateLandmarks(planet.name) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SpaceBackground.copy(alpha = 0.96f))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            rotX = (rotX - dragAmount.x * 0.4f) % 360f
                            rotY = (rotY - dragAmount.y * 0.4f).coerceIn(-60f, 60f)
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(planet.name, style = MaterialTheme.typography.headlineMedium, color = StarWhite, fontWeight = FontWeight.Bold)
                        Text("Interactive 3D View", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SpaceSurface)
                            .border(1.dp, SpaceBorder, CircleShape)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = StarWhite)
                    }
                }

                Spacer(Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(primaryColor.copy(alpha = 0.5f), Color.Transparent),
                                center = center,
                                radius = size.minDimension / 2
                            ),
                            radius = size.minDimension / 2,
                            center = center
                        )
                    }

                    Canvas(modifier = Modifier.size(240.dp)) {
                        val R = size.minDimension / 2
                        val centerOffset = center
                        val radX = (rotX * PI / 180f)
                        val radY = (rotY * PI / 180f)
                        val pName = planet.name.lowercase()

                        // --- Saturn back rings ---
                        if (pName == "saturn") drawSaturnRings3D(centerOffset, R, radY, isBack = true)

                        // --- Base sphere gradient ---
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(primaryColor, secondaryColor),
                                center = Offset(centerOffset.x - R * 0.2f, centerOffset.y - R * 0.2f),
                                radius = R
                            ),
                            radius = R,
                            center = centerOffset
                        )

                        val spherePath = Path().apply {
                            addOval(androidx.compose.ui.geometry.Rect(
                                centerOffset.x - R, centerOffset.y - R,
                                centerOffset.x + R, centerOffset.y + R
                            ))
                        }

                        clipPath(spherePath) {
                            // ═══════════════════════════════════════════
                            // GAS GIANT BANDS (projected latitude stripes)
                            // ═══════════════════════════════════════════
                            when (pName) {
                                "jupiter" -> {
                                    listOf(
                                        -90f to -58f to Color(0xFF4E342E),
                                        -58f to -46f to Color(0xFFD7CCC8),
                                        -46f to -34f to Color(0xFF795548),
                                        -34f to -22f to Color(0xFFBCAAA4),
                                        -22f to -10f to Color(0xFF6D4C41),
                                        -10f to  0f  to Color(0xFFD7CCC8),
                                         0f  to 10f  to Color(0xFF8D6E63),
                                        10f  to 22f  to Color(0xFFD7CCC8),
                                        22f  to 34f  to Color(0xFF6D4C41),
                                        34f  to 46f  to Color(0xFFBCAAA4),
                                        46f  to 58f  to Color(0xFF795548),
                                        58f  to 90f  to Color(0xFF4E342E)
                                    ).forEach { (range, color) ->
                                        drawSphereBand(centerOffset, R, radY, range.first, range.second, color)
                                    }
                                    // Subtle turbulence streaks
                                    listOf(-40f, -28f, -6f, 6f, 18f, 30f).forEach { lat ->
                                        drawSphereBand(centerOffset, R, radY, lat - 1f, lat + 1f, Color.White.copy(alpha = 0.06f))
                                    }
                                    // Great Red Spot
                                    run {
                                        val gLat = (-22f * PI / 180f).toFloat()
                                        val gLon = (90f  * PI / 180f).toFloat()
                                        val gx = cos(gLat) * sin(gLon)
                                        val gy = sin(gLat)
                                        val gz = cos(gLat) * cos(gLon)
                                        val gxR = (cos(radX)*gx + sin(radX)*gz).toFloat()
                                        val gzR = (-sin(radX)*gx + cos(radX)*gz).toFloat()
                                        val gxF = gxR
                                        val gyF = (gy*cos(radY) - gzR*sin(radY)).toFloat()
                                        val gzF = (gy*sin(radY) + gzR*cos(radY)).toFloat()
                                        if (gzF > 0f) {
                                            val sx = centerOffset.x + R * gxF
                                            val sy = centerOffset.y - R * gyF
                                            drawOval(Color(0xFFB71C1C).copy(alpha = 0.9f), Offset(sx - R*0.20f, sy - R*0.095f), Size(R*0.40f, R*0.19f))
                                            drawOval(Color(0xFFEF9A9A).copy(alpha = 0.45f), Offset(sx - R*0.15f, sy - R*0.07f), Size(R*0.30f, R*0.14f))
                                        }
                                    }
                                }
                                "saturn" -> {
                                    listOf(
                                        -90f to -58f to Color(0xFFC49A6C),
                                        -58f to -40f to Color(0xFFEDE0C8),
                                        -40f to -24f to Color(0xFFC49A6C),
                                        -24f to  -8f to Color(0xFFEDE0C8),
                                         -8f to   8f to Color(0xFFD4AF37),
                                          8f to  24f to Color(0xFFEDE0C8),
                                         24f to  40f to Color(0xFFC49A6C),
                                         40f to  58f to Color(0xFFEDE0C8),
                                         58f to  90f to Color(0xFFC49A6C)
                                    ).forEach { (range, color) ->
                                        drawSphereBand(centerOffset, R, radY, range.first, range.second, color)
                                    }
                                }
                                "uranus" -> {
                                    listOf(
                                        -90f to -45f to Color(0xFF4DD0E1).copy(alpha = 0.55f),
                                        -45f to  0f  to Color(0xFF80DEEA).copy(alpha = 0.45f),
                                         0f  to 45f  to Color(0xFF4DD0E1).copy(alpha = 0.55f),
                                        45f  to 90f  to Color(0xFF26C6DA).copy(alpha = 0.45f)
                                    ).forEach { (range, color) ->
                                        drawSphereBand(centerOffset, R, radY, range.first, range.second, color)
                                    }
                                }
                                "neptune" -> {
                                    listOf(
                                        -90f to -52f to Color(0xFF0D47A1).copy(alpha = 0.7f),
                                        -52f to -22f to Color(0xFF1976D2).copy(alpha = 0.5f),
                                        -22f to  10f to Color(0xFF1565C0).copy(alpha = 0.65f),
                                         10f to  40f to Color(0xFF0D47A1).copy(alpha = 0.7f),
                                         40f to  90f to Color(0xFF1565C0).copy(alpha = 0.5f)
                                    ).forEach { (range, color) ->
                                        drawSphereBand(centerOffset, R, radY, range.first, range.second, color)
                                    }
                                    // Great Dark Spot
                                    run {
                                        val gLat = (-20f * PI / 180f).toFloat()
                                        val gLon = (120f * PI / 180f).toFloat()
                                        val gx = cos(gLat) * sin(gLon)
                                        val gy = sin(gLat)
                                        val gz = cos(gLat) * cos(gLon)
                                        val gxR = (cos(radX)*gx + sin(radX)*gz).toFloat()
                                        val gzR = (-sin(radX)*gx + cos(radX)*gz).toFloat()
                                        val gxF = gxR
                                        val gyF = (gy*cos(radY) - gzR*sin(radY)).toFloat()
                                        val gzF = (gy*sin(radY) + gzR*cos(radY)).toFloat()
                                        if (gzF > 0f) {
                                            val sx = centerOffset.x + R * gxF
                                            val sy = centerOffset.y - R * gyF
                                            drawOval(Color(0xFF01579B).copy(alpha = 0.85f), Offset(sx - R*0.13f, sy - R*0.07f), Size(R*0.26f, R*0.14f))
                                        }
                                    }
                                }
                            }

                            // ═══════════════════════════════════════════
                            // ROCKY PLANET SURFACE FEATURES
                            // ═══════════════════════════════════════════
                            // North polar cap
                            if (pName == "earth") {
                                if (sin(radY) > 0) {
                                    drawCircle(Color(0xFFECEFF1), R * 0.32f, Offset(centerOffset.x, centerOffset.y - R * cos(radY).toFloat()))
                                    drawCircle(Color(0xFFB0BEC5).copy(alpha = 0.5f), R * 0.22f, Offset(centerOffset.x, centerOffset.y - R * cos(radY).toFloat()))
                                }
                            }
                            if (pName == "mars") {
                                if (sin(radY) > 0) {
                                    drawCircle(Color(0xFFECEFF1), R * 0.18f, Offset(centerOffset.x, centerOffset.y - R * cos(radY).toFloat()))
                                }
                                // South polar cap
                                if (sin(radY) < 0) {
                                    drawCircle(Color(0xFFECEFF1), R * 0.12f, Offset(centerOffset.x, centerOffset.y + R * cos(radY).toFloat()))
                                }
                            }

                            // Surface landmark dots (rocky planets only)
                            if (pName !in listOf("jupiter", "saturn", "uranus", "neptune")) {
                                landmarks.forEach { lm ->
                                    val laR = (lm.lat * PI / 180f).toFloat()
                                    val loR = (lm.lon * PI / 180f).toFloat()
                                    val x = cos(laR) * sin(loR)
                                    val y = sin(laR)
                                    val z = cos(laR) * cos(loR)
                                    val xR = (cos(radX)*x + sin(radX)*z).toFloat()
                                    val zR = (-sin(radX)*x + cos(radX)*z).toFloat()
                                    val xF = xR
                                    val yF = (y*cos(radY) - zR*sin(radY)).toFloat()
                                    val zF = (y*sin(radY) + zR*cos(radY)).toFloat()
                                    if (zF > 0f) {
                                        val scale = 0.5f + 0.5f * zF
                                        drawCircle(lm.color, lm.size * scale, Offset(centerOffset.x + R*xF, centerOffset.y - R*yF))
                                    }
                                }
                            }

                            // ═══════════════════════════════════════════
                            // LIMB DARKENING  (makes sphere feel 3-D)
                            // ═══════════════════════════════════════════
                            drawLimbDarkening(centerOffset, R)

                            // ═══════════════════════════════════════════
                            // SPECULAR HIGHLIGHT (glossy light reflection)
                            // ═══════════════════════════════════════════
                            drawSpecularHighlight(centerOffset, R)
                        }

                        // --- Rings drawn on top of sphere ---
                        if (pName == "saturn") drawSaturnRings3D(centerOffset, R, radY, isBack = false)
                        if (pName == "uranus") drawUranusRings3D(centerOffset, R, radY)

                        // --- Atmospheric rim glow ---
                        drawAtmosphericRim(centerOffset, R, primaryColor)
                    }
                }

                Spacer(Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(SpaceSurface)
                        .border(1.dp, SpaceBorder, RoundedCornerShape(16.dp))
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        "Drag in any direction to spin the planet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

private fun DrawScope.drawSaturnRings3D(
    center: Offset,
    planetRadius: Float,
    tiltRad: Double,
    isBack: Boolean
) {
    val rIn = planetRadius * 1.35f
    val rOut = planetRadius * 2.1f
    val ringHeight = (rOut * 2 * Math.abs(sin(tiltRad))).toFloat()
    
    val clipPath = Path().apply {
        val yStart = if (isBack) 0f else center.y
        val yEnd = if (isBack) center.y else size.height
        addRect(androidx.compose.ui.geometry.Rect(0f, yStart, size.width, yEnd))
    }
    
    clipPath(clipPath) {
        val colors = listOf(Color(0xFFE5C158).copy(alpha = 0.8f), Color(0xFFC5A038).copy(alpha = 0.5f), Color(0xFFE5C158).copy(alpha = 0.9f))
        val steps = 3
        for (i in 0 until steps) {
            val factor = i.toFloat() / steps
            val currentROut = rOut - (rOut - rIn) * factor * 0.8f
            val currentRIn = currentROut - (rOut - rIn) * 0.2f
            
            val hOut = (currentROut * 2 * Math.abs(sin(tiltRad))).toFloat()
            
            drawArc(
                color = colors[i % colors.size],
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - currentROut, center.y - hOut / 2),
                size = Size(currentROut * 2, hOut),
                style = Stroke(width = (currentROut - currentRIn))
            )
        }
    }
}

private fun DrawScope.drawUranusRings3D(
    center: Offset,
    planetRadius: Float,
    tiltRad: Double
) {
    val rOut = planetRadius * 1.6f
    val ringWidth = (rOut * 2 * 0.15f).toFloat()
    val ringHeight = rOut * 2
    
    drawArc(
        color = Color(0xFFB2EBF2).copy(alpha = 0.4f),
        startAngle = 0f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = Offset(center.x - ringWidth / 2, center.y - rOut),
        size = Size(ringWidth, ringHeight),
        style = Stroke(width = 2f)
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Texture & Lighting helpers
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Draws a projected latitude band as a horizontal rectangle clipped to the sphere.
 * Geometrically correct for orthographic projection at any tilt.
 */
private fun DrawScope.drawSphereBand(
    center: Offset, R: Float, tiltRad: Double,
    lat1Deg: Float, lat2Deg: Float, color: Color
) {
    val y1 = center.y - (R * sin(lat1Deg * PI / 180.0) * cos(tiltRad)).toFloat()
    val y2 = center.y - (R * sin(lat2Deg * PI / 180.0) * cos(tiltRad)).toFloat()
    val yTop = minOf(y1, y2)
    val yBot = maxOf(y1, y2)
    drawRect(color, topLeft = Offset(center.x - R, yTop), size = Size(R * 2f, (yBot - yTop).coerceAtLeast(1f)))
}

/** Radial dark vignette at the sphere limb — key realism cue. */
private fun DrawScope.drawLimbDarkening(center: Offset, R: Float) {
    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0.00f to Color.Transparent,
                0.52f to Color.Transparent,
                0.78f to Color.Black.copy(alpha = 0.18f),
                0.90f to Color.Black.copy(alpha = 0.48f),
                1.00f to Color.Black.copy(alpha = 0.78f)
            ),
            center = center,
            radius = R
        ),
        radius = R,
        center = center
    )
}

/** Offset white radial gradient simulating a light source from upper-left. */
private fun DrawScope.drawSpecularHighlight(center: Offset, R: Float) {
    val hc = Offset(center.x - R * 0.30f, center.y - R * 0.30f)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White.copy(alpha = 0.55f), Color.White.copy(alpha = 0.0f)),
            center = hc,
            radius = R * 0.44f
        ),
        radius = R * 0.44f,
        center = hc
    )
}

/** Soft colored halo just outside the sphere edge — simulates atmospheric scattering. */
private fun DrawScope.drawAtmosphericRim(center: Offset, R: Float, atmColor: Color) {
    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0.00f to Color.Transparent,
                0.86f to Color.Transparent,
                0.93f to atmColor.copy(alpha = 0.30f),
                1.00f to atmColor.copy(alpha = 0.00f)
            ),
            center = center,
            radius = R * 1.10f
        ),
        radius = R * 1.10f,
        center = center
    )
}
