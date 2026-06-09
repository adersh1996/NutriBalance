package com.smad.nutribalance.ui.screens.meal

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smad.nutribalance.data.repository.MealLogRepository
import com.smad.nutribalance.data.repository.UserProfileRepository
import com.smad.nutribalance.domain.model.MealLog
import com.smad.nutribalance.domain.model.MealType
import com.smad.nutribalance.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class MealInputUiState(
    val mealType: MealType = MealType.BREAKFAST,
    val userProfile: UserProfile? = null,
    val foodInput: String = "",
    val isLoading: Boolean = false,
    val aiResponse: String? = null,
    val error: String? = null,
    val isSaved: Boolean = false,
    val estimatedCalories: Double = 0.0
)

@HiltViewModel
class MealInputViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mealLogRepository: MealLogRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealInputUiState())
    val uiState: StateFlow<MealInputUiState> = _uiState.asStateFlow()

    init {
        val mealTypeArg = savedStateHandle.get<String>("mealType") ?: MealType.BREAKFAST.name
        val mealType = MealType.valueOf(mealTypeArg)
        _uiState.value = _uiState.value.copy(mealType = mealType)

        viewModelScope.launch {
            val profile = userProfileRepository.userProfile.first()
            _uiState.value = _uiState.value.copy(userProfile = profile)
        }
    }

    fun onFoodInputChanged(value: String) {
        _uiState.value = _uiState.value.copy(foodInput = value, error = null)
    }

    fun onGetMealSuggestion() {
        val state = _uiState.value
        val profile = state.userProfile ?: return

        if (state.foodInput.isBlank()) {
            _uiState.value = state.copy(error = "Please enter the foods you have available")
            return
        }

        _uiState.value = state.copy(isLoading = true, error = null, aiResponse = null)

        viewModelScope.launch {
            val mealCalories = when (state.mealType) {
                MealType.BREAKFAST -> profile.breakfastCalories
                MealType.LUNCH -> profile.lunchCalories
                MealType.DINNER -> profile.dinnerCalories
            }

            // Calculate previously consumed calories today
            val todaysMeals = mealLogRepository.getTodaysMeals().first()
            val previousCalories = todaysMeals
                .filter { it.mealType != state.mealType }
                .sumOf { it.caloriesConsumed }

            val result = mealLogRepository.getMealSuggestion(
                userProfile = profile,
                mealType = state.mealType,
                mealCalories = mealCalories,
                foodList = state.foodInput,
                previouslyConsumedToday = previousCalories
            )

            result.fold(
                onSuccess = { response ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        aiResponse = response,
                        estimatedCalories = mealCalories // Use target as estimate
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.localizedMessage ?: "Unknown error occurred"
                    )
                }
            )
        }
    }

    fun onConfirmMeal() {
        val state = _uiState.value
        val profile = state.userProfile ?: return
        val aiResponse = state.aiResponse ?: return

        val mealCalories = when (state.mealType) {
            MealType.BREAKFAST -> profile.breakfastCalories
            MealType.LUNCH -> profile.lunchCalories
            MealType.DINNER -> profile.dinnerCalories
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())

        viewModelScope.launch {
            val mealLog = MealLog(
                date = today,
                mealType = state.mealType,
                foodsEntered = state.foodInput,
                aiResponse = aiResponse,
                caloriesConsumed = mealCalories,
                timestamp = System.currentTimeMillis()
            )
            mealLogRepository.insertMealLog(mealLog)
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }

    fun onSavedHandled() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }
}
