package com.example.starline.ui.news

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.starline.data.FavoritesManager
import com.example.starline.data.SpaceDataRepository
import com.example.starline.theme.*

@Composable
fun NewsDetailScreen(
    newsId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val repository = remember { SpaceDataRepository(context) }
    val favoritesManager = remember { FavoritesManager(context) }

    // First look in the live newsList; if not found, fall back to cached article
    val liveArticle = remember(newsId) { repository.news.find { it.id == newsId } }
    val cachedArticle = remember(newsId) { favoritesManager.getCachedArticle(newsId) }
    val article = liveArticle ?: cachedArticle

    val clipboardManager = LocalClipboardManager.current

    var isFavorite by remember(newsId) { mutableStateOf(favoritesManager.isArticleFavorite(newsId)) }

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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().statusBarsPadding()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = StarWhite)
            }
            Text("Back to news", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Spacer(Modifier.weight(1f))

            // Bookmark / favorite button
            IconButton(onClick = {
                isFavorite = favoritesManager.toggleArticleFavorite(article)
                val msg = if (isFavorite) "Article saved to favorites" else "Removed from favorites"
                android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
            }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Save to favorites",
                    tint = if (isFavorite) NeonPrimary else StarWhite
                )
            }

            // Copy URL button
            if (article.url.isNotBlank()) {
                IconButton(onClick = {
                    clipboardManager.setText(AnnotatedString(article.url))
                    android.widget.Toast.makeText(context, "URL copied to clipboard", android.widget.Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy URL", tint = StarWhite)
                }
            }
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
        
        if (article.imageUrl.isNotBlank()) {
            coil.compose.AsyncImage(
                model = article.imageUrl,
                contentDescription = article.title,
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, SpaceBorder, RoundedCornerShape(16.dp))
            )
            Spacer(Modifier.height(16.dp))
        }

        Text(article.title, style = MaterialTheme.typography.headlineMedium, color = StarWhite, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(12.dp))

        Text(article.summary, style = MaterialTheme.typography.bodyLarge, color = NeonSecondary, lineHeight = 24.sp)

        Spacer(Modifier.height(24.dp))

        if (article.url.isNotBlank()) {
            val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
            Button(
                onClick = { uriHandler.openUri(article.url) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonPrimary.copy(alpha = 0.8f))
            ) {
                Text("Read Full Article", color = StarWhite, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}
