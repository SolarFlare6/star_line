package com.example.starline.data

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class SettingsManager(private val context: Context) {

    private val sharedPrefs = context.getSharedPreferences("starline_settings_prefs", Context.MODE_PRIVATE)

    private val auth: FirebaseAuth?
        get() = try { FirebaseAuth.getInstance() } catch (e: Exception) { null }

    private val database: FirebaseDatabase?
        get() = try { FirebaseDatabase.getInstance() } catch (e: Exception) { null }

    var measurementSystem: String
        get() = sharedPrefs.getString("measurement_system", "Metric") ?: "Metric"
        set(value) {
            sharedPrefs.edit().putString("measurement_system", value).apply()
        }

    val isMetric: Boolean
        get() = measurementSystem == "Metric"

    fun updateMeasurementSystem(system: String) {
        measurementSystem = system
        val uid = auth?.currentUser?.uid
        val dbRef = database
        if (uid != null && dbRef != null) {
            try {
                dbRef.getReference("users")
                    .child(uid)
                    .child("settings")
                    .child("measurementSystem")
                    .setValue(system)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun syncFromFirebase(): Boolean {
        val uid = auth?.currentUser?.uid ?: return false
        val dbRef = database ?: return false
        return try {
            val snapshot = dbRef.getReference("users")
                .child(uid)
                .child("settings")
                .child("measurementSystem")
                .get()
                .await()
            if (snapshot.exists()) {
                val system = snapshot.value as? String
                if (system == "Metric" || system == "Imperial") {
                    measurementSystem = system
                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun formatText(text: String): String {
        if (isMetric) return text

        return try {
            var result = text

            // 1. Replace speed "%,.0f km/h" -> "%,.0f mph"
            val speedRegex = Regex("""\b([0-9,]+(?:\.[0-9]+)?)\s*km/h\b""")
            result = speedRegex.replace(result) { match ->
                val numStr = match.groupValues[1].replace(",", "")
                val doubleVal = numStr.toDoubleOrNull()
                if (doubleVal != null) {
                    val converted = doubleVal * 0.621371
                    String.format(java.util.Locale.US, "%,.0f mph", converted)
                } else {
                    match.value
                }
            }

            // 2. Replace distance "%,.1fM km" -> "%,.1fM miles"
            val distanceMRegex = Regex("""\b([0-9,]+(?:\.[0-9]+)?)\s*M\s*km\b""")
            result = distanceMRegex.replace(result) { match ->
                val numStr = match.groupValues[1].replace(",", "")
                val doubleVal = numStr.toDoubleOrNull()
                if (doubleVal != null) {
                    val converted = doubleVal * 0.621371
                    String.format(java.util.Locale.US, "%,.1fM miles", converted)
                } else {
                    match.value
                }
            }

            // 3. Replace distance "%,.1fB km" -> "%,.2fB miles"
            val distanceBRegex = Regex("""\b([0-9,]+(?:\.[0-9]+)?)\s*B\s*km\b""")
            result = distanceBRegex.replace(result) { match ->
                val numStr = match.groupValues[1].replace(",", "")
                val doubleVal = numStr.toDoubleOrNull()
                if (doubleVal != null) {
                    val converted = doubleVal * 0.621371
                    String.format(java.util.Locale.US, "%,.2fB miles", converted)
                } else {
                    match.value
                }
            }

            // 4. Replace distance/altitude/diameter "%,.0f km" -> "%,.0f miles"
            val kmRegex = Regex("""\b([0-9,]+(?:\.[0-9]+)?)\s*km\b""")
            result = kmRegex.replace(result) { match ->
                val numStr = match.groupValues[1].replace(",", "")
                val doubleVal = numStr.toDoubleOrNull()
                if (doubleVal != null) {
                    val converted = doubleVal * 0.621371
                    String.format(java.util.Locale.US, "%,.0f miles", converted)
                } else {
                    match.value
                }
            }

            // 5. Replace gravity "gravity m/s²" -> "ft/s²"
            val gravityRegex = Regex("""\b([0-9,]+(?:\.[0-9]+)?)\s*m/s²\b""")
            result = gravityRegex.replace(result) { match ->
                val numStr = match.groupValues[1].replace(",", "")
                val doubleVal = numStr.toDoubleOrNull()
                if (doubleVal != null) {
                    val converted = doubleVal * 3.28084
                    String.format(java.util.Locale.US, "%.1f ft/s²", converted)
                } else {
                    match.value
                }
            }

            result
        } catch (e: Exception) {
            text
        }
    }
}
