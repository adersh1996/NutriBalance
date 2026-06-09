package com.smad.nutribalance.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smad.nutribalance.data.repository.MealLogRepository
import com.smad.nutribalance.data.repository.UserProfileRepository
import com.smad.nutribalance.domain.model.MealLog
import com.smad.nutribalance.domain.model.MealType
import com.smad.nutribalance.domain.model.UserProfile
import com.smad.nutribalance.util.StreakCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val userProfile: UserProfile? = null,
    val todaysMeals: List<MealLog> = emptyList(),
    val streakDays: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val mealLogRepository: MealLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                userProfileRepository.userProfile,
                mealLogRepository.getTodaysMeals(),
                mealLogRepository.getDatesWithAllMealsLogged()
            ) { profile, meals, fullLogDates ->
                DashboardUiState(
                    userProfile = profile,
                    todaysMeals = meals,
                    streakDays = StreakCalculator.compute(fullLogDates),
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun isMealLogged(mealType: MealType): Boolean {
        return _uiState.value.todaysMeals.any { it.mealType == mealType }
    }
}
