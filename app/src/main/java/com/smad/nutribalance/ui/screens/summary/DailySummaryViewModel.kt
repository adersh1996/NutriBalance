package com.smad.nutribalance.ui.screens.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smad.nutribalance.data.repository.MealLogRepository
import com.smad.nutribalance.data.repository.UserProfileRepository
import com.smad.nutribalance.domain.model.MealLog
import com.smad.nutribalance.domain.model.UserProfile
import com.smad.nutribalance.util.StreakCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SummaryUiState(
    val userProfile: UserProfile? = null,
    val todaysMeals: List<MealLog> = emptyList(),
    val totalCaloriesToday: Double = 0.0,
    val streakDays: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class DailySummaryViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val mealLogRepository: MealLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                userProfileRepository.userProfile,
                mealLogRepository.getTodaysMeals(),
                mealLogRepository.getTotalCaloriesToday(),
                mealLogRepository.getDatesWithAllMealsLogged()
            ) { profile, meals, totalCalories, fullLogDates ->
                SummaryUiState(
                    userProfile = profile,
                    todaysMeals = meals,
                    totalCaloriesToday = totalCalories ?: 0.0,
                    streakDays = StreakCalculator.compute(fullLogDates),
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
