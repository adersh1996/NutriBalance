package com.smad.nutribalance.domain.model

data class MealLog(
    val id: Long = 0,
    val date: String,
    val mealType: MealType,
    val foodsEntered: String,
    val aiResponse: String,
    val caloriesConsumed: Double,
    val timestamp: Long
)
