package com.smad.nutribalance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_history")
data class WeightHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,           // Format: "yyyy-MM-dd"
    val weightKg: Double,
    val tdee: Double,
    val targetCalories: Double,
    val goalType: String        // GoalType enum name
)
