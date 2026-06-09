package com.smad.nutribalance.ui.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Profile : Screen("profile")
    object ScaleCheck : Screen("scale_check")
    object Dashboard : Screen("dashboard")
    object MealInput : Screen("meal_input/{mealType}") {
        fun createRoute(mealType: String) = "meal_input/$mealType"
    }
    object Summary : Screen("summary")
}
