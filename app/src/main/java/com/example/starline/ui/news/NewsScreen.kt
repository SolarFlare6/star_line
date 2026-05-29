package com.example.starline.ui.news

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.starline.data.NewsArticle
import com.example.starline.data.SpaceDataRepository
import com.example.starline.theme.*

@Composable
fun NewsScreen(
    onNewsClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repository = remember(context) { SpaceDataRepository(context) }
    var newsList by remember { mutableStateOf(repository.news) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = true
        repository.fetchSpaceNews()
        newsList = repository.news
        isLoading = false
    }

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 16.dp)) {
        Text("Space News & Facts", style = MaterialTheme.typography.headlineMedium, color = StarWhite, fontWeight = FontWeight.Bold)
        Text("Latest discoveries and updates", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(Modifier.height(20.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonPrimary)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(newsList) { article ->
                    NewsCard(article = article, onClick = { onNewsClick(article.id) })
                }
            }
        }
    }
}

@Composable
private fun NewsCard(article: NewsArticle, onClick: () -> Unit) {
    val categoryColor = when (article.category) {
        "Discovery" -> GlowGreen
        "Physics" -> NeonSecondary
        "Exploration" -> NeonPrimary
        else -> NeonTertiary
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SpaceSurface)
            .border(1.dp, SpaceBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(categoryColor.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(article.category, style = MaterialTheme.typography.labelSmall, color = categoryColor, fontSize = 10.sp)
                }
                Spacer(Modifier.width(10.dp))
                Text(article.date, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Spacer(Modifier.weight(1f))
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TextSecondary)
            }
            Spacer(Modifier.height(10.dp))
            Text(article.title, style = MaterialTheme.typography.titleMedium, color = StarWhite, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(6.dp))
            Text(article.summary, style = MaterialTheme.typography.bodySmall, color = TextSecondary, lineHeight = 18.sp, maxLines = 2)
        }
    }
}
