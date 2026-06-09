package com.smad.nutribalance.domain.model

data class WeightHistory(
    val id: Long = 0,
    val date: String,           // "yyyy-MM-dd"
    val weightKg: Double,
    val tdee: Double,
    val targetCalories: Double,
    val goalType: GoalType
)
