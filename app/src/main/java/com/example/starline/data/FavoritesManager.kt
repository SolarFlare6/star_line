package com.example.starline.data

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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
}
