package com.example.starline.ui.news

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.starline.data.SpaceDataRepository
import com.example.starline.theme.*

@Composable
fun NewsDetailScreen(
    newsId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val repository = remember { SpaceDataRepository() }
    val article = remember(newsId) { repository.news.find { it.id == newsId } }

    if (article == null) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Article not found", color = TextSecondary)
        }
        return
    }

    val categoryColor = when (article.category) {
        "Discovery" -> GlowGreen
        "Physics" -> NeonSecondary
        "Exploration" -> NeonPrimary
        else -> NeonTertiary
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
            Text("Back to news", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }

        Spacer(Modifier.height(16.dp))

        // Category and date header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(categoryColor.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(article.category, style = MaterialTheme.typography.labelSmall, color = categoryColor)
            }
            Spacer(Modifier.width(12.dp))
            Text(article.date, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Spacer(Modifier.width(12.dp))
            Text(article.readTime, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }

        Spacer(Modifier.height(16.dp))

        Text(article.title, style = MaterialTheme.typography.headlineMedium, color = StarWhite, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(12.dp))

        Text(article.summary, style = MaterialTheme.typography.bodyLarge, color = NeonSecondary, lineHeight = 24.sp)

        Spacer(Modifier.height(20.dp))

        // Full text
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SpaceSurface)
                .border(1.dp, SpaceBorder, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Text(article.fullText, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, lineHeight = 24.sp)
        }

        Spacer(Modifier.height(80.dp))
    }
}
