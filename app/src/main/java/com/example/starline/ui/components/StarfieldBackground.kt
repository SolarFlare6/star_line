package com.example.starline.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.starline.theme.SpaceBackground
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.sin
import kotlin.random.Random

data class Star(
    val x: Float,
    val y: Float,
    val size: Float,
    val maxAlpha: Float,
    val phaseOffset: Float,
    val speedFactor: Float
)

@Composable
fun StarfieldBackground(modifier: Modifier = Modifier) {
    // Generate a fixed set of stars so they don't reposition on recomposition
    val stars = remember {
        List(120) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 2.5f + 0.5f, // Size between 0.5dp and 3.0dp
                maxAlpha = Random.nextFloat() * 0.7f + 0.3f, // Alpha peak between 0.3 and 1.0
                phaseOffset = Random.nextFloat() * 2f * PI.toFloat(),
                speedFactor = Random.nextFloat() * 0.8f + 0.2f // Speeds varying for asynchronous twinkling
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "StarfieldTwinkle")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Time"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceBackground)
    ) {
        stars.forEach { star ->
            // Calculate organic asynchronous twinkling using sine wave and individual phase/speed
            val currentAlpha = (sin(time * star.speedFactor + star.phaseOffset).absoluteValue * star.maxAlpha)
                .coerceIn(0.1f, 1f)

            drawCircle(
                color = Color.White.copy(alpha = currentAlpha),
                radius = star.size,
                center = Offset(
                    x = star.x * size.width,
                    y = star.y * size.height
                )
            )
        }
    }
}
