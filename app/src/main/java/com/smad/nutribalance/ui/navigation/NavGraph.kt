package com.smad.nutribalance.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smad.nutribalance.ui.screens.dashboard.DashboardScreen
import com.smad.nutribalance.ui.screens.meal.MealInputScreen
import com.smad.nutribalance.ui.screens.profile.ProfileScreen
import com.smad.nutribalance.ui.screens.scale.ScaleCheckScreen
import com.smad.nutribalance.ui.screens.summary.DailySummaryScreen
import com.smad.nutribalance.ui.screens.welcome.WelcomeScreen

// Screens that show the bottom navigation bar
private val screensWithBottomNav = setOf(
    Screen.Dashboard.route,
    Screen.Summary.route
)

@Composable
fun NutriNavGraph(startDestination: String) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in screensWithBottomNav

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
                    NavigationBarItem(
                        selected = currentRoute == Screen.Dashboard.route,
                        onClick = {
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Dashboard.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Home") },
                        label = { Text("Home", fontWeight = FontWeight.Medium) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Summary.route,
                        onClick = {
                            navController.navigate(Screen.Summary.route) {
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(Icons.Default.History, contentDescription = "Summary") },
                        label = { Text("Summary", fontWeight = FontWeight.Medium) }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            composable(Screen.Welcome.route) {
                WelcomeScreen(
                    onGetStarted = {
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onCalculated = {
                        navController.navigate(Screen.ScaleCheck.route)
                    }
                )
            }

            composable(Screen.ScaleCheck.route) {
                val scaleViewModel: ScaleViewModel = hiltViewModel()
                ScaleCheckScreen(
                    onHasScale = {
                        scaleViewModel.setScale(true)
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNoScale = {
                        scaleViewModel.setScale(false)
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onLogMeal = { mealType ->
                        navController.navigate(Screen.MealInput.createRoute(mealType))
                    }
                )
            }

            composable(
                route = Screen.MealInput.route,
                arguments = listOf(navArgument("mealType") { type = NavType.StringType })
            ) {
                MealInputScreen(
                    onBack = { navController.popBackStack() },
                    onMealSaved = { navController.popBackStack() }
                )
            }

            composable(Screen.Summary.route) {
                DailySummaryScreen()
            }
        }
    }
}
