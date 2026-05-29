package com.example.starline.data

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FavoritesManager(private val context: Context) {

    private val sharedPrefs = context.getSharedPreferences("starline_favorites_cache", Context.MODE_PRIVATE)
    
    private val auth: FirebaseAuth?
        get() = try { FirebaseAuth.getInstance() } catch (e: Exception) { null }

    private val database: FirebaseDatabase?
        get() = try { FirebaseDatabase.getInstance() } catch (e: Exception) { null }

    val currentUserId: String
        get() = auth?.currentUser?.uid ?: "mock_explorer"

    fun isPlanetFavorite(name: String): Boolean {
        val key = "${currentUserId}_planet_$name"
        return sharedPrefs.getBoolean(key, false)
    }

    fun isSatelliteFavorite(name: String): Boolean {
        val key = "${currentUserId}_satellite_$name"
        return sharedPrefs.getBoolean(key, false)
    }

    fun togglePlanetFavorite(name: String): Boolean {
        val key = "${currentUserId}_planet_$name"
        val isFav = !sharedPrefs.getBoolean(key, false)
        sharedPrefs.edit().putBoolean(key, isFav).apply()

        // Sync to Firebase Realtime Database
        val uid = auth?.currentUser?.uid
        val dbRef = database
        if (uid != null && dbRef != null) {
            try {
                dbRef.getReference("users")
                    .child(uid)
                    .child("favorites")
                    .child("planets")
                    .child(name)
                    .setValue(isFav)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return isFav
    }

    fun toggleSatelliteFavorite(name: String): Boolean {
        val key = "${currentUserId}_satellite_$name"
        val isFav = !sharedPrefs.getBoolean(key, false)
        sharedPrefs.edit().putBoolean(key, isFav).apply()

        // Sync to Firebase Realtime Database
        val uid = auth?.currentUser?.uid
        val dbRef = database
        if (uid != null && dbRef != null) {
            try {
                dbRef.getReference("users")
                    .child(uid)
                    .child("favorites")
                    .child("satellites")
                    .child(name)
                    .setValue(isFav)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return isFav
    }

    fun getFavoritePlanets(): List<String> {
        val prefix = "${currentUserId}_planet_"
        return sharedPrefs.all
            .filter { it.key.startsWith(prefix) && it.value == true }
            .map { it.key.removePrefix(prefix) }
    }

    fun getFavoriteSatellites(): List<String> {
        val prefix = "${currentUserId}_satellite_"
        return sharedPrefs.all
            .filter { it.key.startsWith(prefix) && it.value == true }
            .map { it.key.removePrefix(prefix) }
    }

    // ─────────────────────────────────────────────────────────────
    // Article Favorites + Offline Cache
    // ─────────────────────────────────────────────────────────────

    fun isArticleFavorite(articleId: String): Boolean {
        val key = "${currentUserId}_article_fav_$articleId"
        return sharedPrefs.getBoolean(key, false)
    }

    /**
     * Toggle article favorite. When favoriting, cache the full article so it
     * is readable offline. When un-favoriting, the cached data is kept (cheap)
     * because the user may re-add it later; only the flag is flipped.
     */
    fun toggleArticleFavorite(article: NewsArticle): Boolean {
        val isFav = !isArticleFavorite(article.id)
        val key = "${currentUserId}_article_fav_${article.id}"
        val editor = sharedPrefs.edit().putBoolean(key, isFav)

        if (isFav) {
            // Cache article fields for offline reading
            editor
                .putString("${currentUserId}_article_title_${article.id}", article.title)
                .putString("${currentUserId}_article_summary_${article.id}", article.summary)
                .putString("${currentUserId}_article_fullText_${article.id}", article.fullText)
                .putString("${currentUserId}_article_url_${article.id}", article.url)
                .putString("${currentUserId}_article_imageUrl_${article.id}", article.imageUrl)
                .putString("${currentUserId}_article_category_${article.id}", article.category)
                .putString("${currentUserId}_article_date_${article.id}", article.date)
                .putString("${currentUserId}_article_readTime_${article.id}", article.readTime)
        }
        editor.apply()

        // Sync to Firebase
        val uid = auth?.currentUser?.uid
        val dbRef = database
        if (uid != null && dbRef != null) {
            try {
                dbRef.getReference("users")
                    .child(uid)
                    .child("favorites")
                    .child("articles")
                    .child(article.id)
                    .setValue(if (isFav) article.title else null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return isFav
    }

    /** Returns IDs of all favorited articles for the current user. */
    fun getFavoriteArticleIds(): List<String> {
        val prefix = "${currentUserId}_article_fav_"
        return sharedPrefs.all
            .filter { it.key.startsWith(prefix) && it.value == true }
            .map { it.key.removePrefix(prefix) }
    }

    /** Reconstructs a cached NewsArticle from SharedPreferences for the given id. */
    fun getCachedArticle(articleId: String): NewsArticle? {
        val uid = currentUserId
        val title = sharedPrefs.getString("${uid}_article_title_$articleId", null) ?: return null
        return NewsArticle(
            id = articleId,
            title = title,
            summary = sharedPrefs.getString("${uid}_article_summary_$articleId", "") ?: "",
            fullText = sharedPrefs.getString("${uid}_article_fullText_$articleId", "") ?: "",
            url = sharedPrefs.getString("${uid}_article_url_$articleId", "") ?: "",
            imageUrl = sharedPrefs.getString("${uid}_article_imageUrl_$articleId", "") ?: "",
            category = sharedPrefs.getString("${uid}_article_category_$articleId", "News") ?: "News",
            date = sharedPrefs.getString("${uid}_article_date_$articleId", "") ?: "",
            readTime = sharedPrefs.getString("${uid}_article_readTime_$articleId", "2 min read") ?: "2 min read"
        )
    }

    /** Returns all favorited articles (reconstructed from cache). */
    fun getFavoriteArticles(): List<NewsArticle> =
        getFavoriteArticleIds().mapNotNull { getCachedArticle(it) }

    suspend fun syncFromFirebase(): Boolean {
        val uid = auth?.currentUser?.uid ?: return false
        val dbRef = database ?: return false
        return try {
            val snapshot = dbRef.getReference("users")
                .child(uid)
                .child("favorites")
                .get()
                .await()

            if (snapshot.exists()) {
                val editor = sharedPrefs.edit()

                // 1. Sync Planets
                val planetsSnapshot = snapshot.child("planets")
                val fbPlanets = if (planetsSnapshot.exists()) {
                    planetsSnapshot.children.associate { (it.key ?: "") to (it.value as? Boolean ?: false) }
                } else {
                    emptyMap()
                }
                
                // Clear local planet favorites not in Firebase or set to false
                getFavoritePlanets().forEach { name ->
                    if (fbPlanets[name] != true) {
                        editor.putBoolean("${uid}_planet_$name", false)
                    }
                }
                // Set Firebase planet favorites to true locally
                fbPlanets.forEach { (name, isFav) ->
                    if (isFav && name.isNotEmpty()) {
                        editor.putBoolean("${uid}_planet_$name", true)
                    }
                }

                // 2. Sync Satellites
                val satellitesSnapshot = snapshot.child("satellites")
                val fbSatellites = if (satellitesSnapshot.exists()) {
                    satellitesSnapshot.children.associate { (it.key ?: "") to (it.value as? Boolean ?: false) }
                } else {
                    emptyMap()
                }

                // Clear local satellite favorites not in Firebase or set to false
                getFavoriteSatellites().forEach { name ->
                    if (fbSatellites[name] != true) {
                        editor.putBoolean("${uid}_satellite_$name", false)
                    }
                }
                // Set Firebase satellite favorites to true locally
                fbSatellites.forEach { (name, isFav) ->
                    if (isFav && name.isNotEmpty()) {
                        editor.putBoolean("${uid}_satellite_$name", true)
                    }
                }

                // 3. Sync Articles
                val articlesSnapshot = snapshot.child("articles")
                val fbArticles = if (articlesSnapshot.exists()) {
                    articlesSnapshot.children.associate { (it.key ?: "") to (it.value as? String ?: "") }
                } else {
                    emptyMap()
                }

                // Clear local article favorites not in Firebase
                getFavoriteArticleIds().forEach { articleId ->
                    if (!fbArticles.containsKey(articleId)) {
                        editor.putBoolean("${uid}_article_fav_$articleId", false)
                    }
                }
                // Set Firebase article favorites to true locally
                fbArticles.forEach { (articleId, title) ->
                    if (articleId.isNotEmpty()) {
                        editor.putBoolean("${uid}_article_fav_$articleId", true)
                        val titleKey = "${uid}_article_title_$articleId"
                        if (!sharedPrefs.contains(titleKey)) {
                            editor.putString(titleKey, title)
                        }
                    }
                }

                editor.apply()
                true
            } else {
                // If the entire favorites node is missing, we clear local favorites for this logged-in user
                val editor = sharedPrefs.edit()
                getFavoritePlanets().forEach { editor.putBoolean("${uid}_planet_$it", false) }
                getFavoriteSatellites().forEach { editor.putBoolean("${uid}_satellite_$it", false) }
                getFavoriteArticleIds().forEach { editor.putBoolean("${uid}_article_fav_$it", false) }
                editor.apply()
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
