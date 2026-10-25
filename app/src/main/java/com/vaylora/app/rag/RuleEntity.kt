package com.vaylora.app.rag

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "waste_rules")
data class RuleEntity(
    @PrimaryKey val id: Int,
    val material: String,
    val category: String,
    val binType: String,
    val binColorHex: String,
    val instruction: String,
    val preparationSteps: String,
    val upcyclingIdea: String,
    val carbonSavedKg: Float
)
