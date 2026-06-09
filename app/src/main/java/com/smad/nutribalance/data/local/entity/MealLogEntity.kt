package com.smad.nutribalance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_log")
data class MealLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,           // Format: "yyyy-MM-dd"
    val mealType: String,       // MealType enum name
    val foodsEntered: String,
    val aiResponse: String,
    val caloriesConsumed: Double,
    val timestamp: Long
)
