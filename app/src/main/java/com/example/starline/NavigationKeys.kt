package com.example.starline

import kotlinx.serialization.Serializable

// These are kept for backward compatibility but the app now uses
// AppRoute enum-based navigation in Navigation.kt
@Serializable
data object Login

@Serializable
data object Register

@Serializable
data object Main

@Serializable
data class PlanetDetail(val name: String)

@Serializable
data class SatelliteDetail(val name: String)

@Serializable
data class NewsDetail(val id: String)

@Serializable
data object Settings

@Serializable
data object Profile
