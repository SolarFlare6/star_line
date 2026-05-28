package com.example.starline.data

import kotlinx.serialization.Serializable

@Serializable
data class Planet(
    val name: String,
    val type: String,
    val distance: String,
    val diameter: String,
    val moons: Int,
    val orbitPeriod: String,
    val description: String,
    val primaryColorHex: String,
    val secondaryColorHex: String
)

@Serializable
data class Satellite(
    val name: String,
    val status: String,
    val launchDate: String,
    val altitude: String,
    val missionType: String,
    val description: String,
    val mainInstrument: String
)

@Serializable
data class NewsArticle(
    val id: String,
    val title: String,
    val category: String,
    val date: String,
    val summary: String,
    val fullText: String,
    val readTime: String
)

@Serializable
data class UserSession(
    val uid: String,
    val email: String,
    val displayName: String,
    val isLoggedIn: Boolean
)

@Serializable
data class ApodData(
    val title: String,
    val explanation: String,
    val url: String,
    val date: String
)
