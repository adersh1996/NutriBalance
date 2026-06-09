package com.smad.nutribalance.domain.model

data class UserProfile(
    val weight: Double,
    val height: Double,
    val age: Int,
    val isMale: Boolean,
    val idealWeight: Double,
    val bmr: Double,
    val tdee: Double,
    val goalType: GoalType,
    val targetCalories: Double,
    val breakfastCalories: Double,
    val lunchCalories: Double,
    val dinnerCalories: Double,
    val hasScale: Boolean = false
)
