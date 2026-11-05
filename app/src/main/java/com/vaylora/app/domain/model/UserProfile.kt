package com.vaylora.app.domain.model

/**
 * Domain model for a Vaylora user profile.
 */
data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val totalItemsScanned: Int = 0,
    val totalCarbonSavedKg: Float = 0f,
    val ecoScore: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
