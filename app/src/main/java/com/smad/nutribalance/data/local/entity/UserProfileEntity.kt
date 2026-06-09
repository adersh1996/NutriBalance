package com.smad.nutribalance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1, // Single user app
    val weight: Double,
    val height: Double,
    val age: Int,
    val isMale: Boolean,
    val idealWeight: Double,
    val bmr: Double,
    val tdee: Double,
    val goalType: String, // Store as String name of GoalType enum
    val targetCalories: Double,
    val breakfastCalories: Double,
    val lunchCalories: Double,
    val dinnerCalories: Double,
    val hasScale: Boolean = false
)
