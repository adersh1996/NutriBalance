package com.smad.nutribalance.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smad.nutribalance.data.repository.MealLogRepository
import com.smad.nutribalance.data.repository.UserProfileRepository
import com.smad.nutribalance.data.repository.WeightHistoryRepository
import com.smad.nutribalance.domain.model.MealLog
import com.smad.nutribalance.domain.model.MealType
import com.smad.nutribalance.domain.model.UserProfile
import com.smad.nutribalance.domain.model.WeightHistory
import com.smad.nutribalance.util.StreakCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Represents one day's data in the history list.
 */
data class DayHistory(
    val date: String,                       // "yyyy-MM-dd"
    val formattedDate: String,              // "Monday, 9 June 2026"
    val meals: Map<MealType, MealLog?>,     // BREAKFAST/LUNCH/DINNER -> log or null
    val totalCalories: Double,
    val targetCalories: Double,
    val hasAllMeals: Boolean
) {
    val calorieDiff: Double get() = totalCalories - targetCalories
    val isOverGoal: Boolean get() = calorieDiff > 0
}

data class HistoryUiState(
    val userProfile: UserProfile? = null,
    val dayHistories: List<DayHistory> = emptyList(),
    val weightHistory: List<WeightHistory> = emptyList(),
    val streakDays: Int = 0,
    val totalCaloriesToday: Double = 0.0,
    val isLoading: Boolean = true
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val mealLogRepository: MealLogRepository,
    private val weightHistoryRepository: WeightHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val displayFormatter = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ENGLISH)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val today = dateFormat.format(Date())

    init {
        viewModelScope.launch {
            combine(
                userProfileRepository.userProfile,
                mealLogRepository.getAllMealLogs(),
                mealLogRepository.getDistinctDates(),
                mealLogRepository.getDatesWithAllMealsLogged(),
                weightHistoryRepository.allEntries
            ) { profile, allMealLogs, distinctDates, fullLogDates, weightHistory ->

                val targetCalories = profile?.targetCalories ?: 0.0
                val streak = StreakCalculator.compute(fullLogDates)

                // Build a map of date -> list of meal logs
                val mealsByDate: Map<String, List<MealLog>> = allMealLogs.groupBy { it.date }

                // Compute today's total
                val todayCalories = mealsByDate[today]?.sumOf { it.caloriesConsumed } ?: 0.0

                // Build DayHistory for each distinct date, newest first
                val dayHistories = distinctDates.map { date ->
                    val mealsForDay = mealsByDate[date] ?: emptyList()
                    val mealMap = mapOf(
                        MealType.BREAKFAST to mealsForDay.find { it.mealType == MealType.BREAKFAST },
                        MealType.LUNCH to mealsForDay.find { it.mealType == MealType.LUNCH },
                        MealType.DINNER to mealsForDay.find { it.mealType == MealType.DINNER }
                    )
                    val totalCal = mealsForDay.sumOf { it.caloriesConsumed }
                    val allLogged = mealMap.values.all { it != null }

                    val parsed = dateFormat.parse(date)
                    val formatted = if (parsed != null) displayFormatter.format(parsed) else date

                    DayHistory(
                        date = date,
                        formattedDate = formatted,
                        meals = mealMap,
                        totalCalories = totalCal,
                        targetCalories = targetCalories,
                        hasAllMeals = allLogged
                    )
                }

                HistoryUiState(
                    userProfile = profile,
                    dayHistories = dayHistories,
                    weightHistory = weightHistory,
                    streakDays = streak,
                    totalCaloriesToday = todayCalories,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
