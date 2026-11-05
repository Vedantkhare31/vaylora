package com.vaylora.app.domain.model

/**
 * A single scan event stored in the user's history.
 */
data class ScanRecord(
    val id: String = "",
    val uid: String = "",
    val materialLabel: String = "",
    val normalizedMaterial: String = "",
    val binType: String = "",
    val binColorHex: String = "#2196F3",
    val instruction: String = "",
    val carbonSavedKg: Float = 0f,
    val confidence: Float = 0f,
    val timestamp: Long = System.currentTimeMillis()
)
