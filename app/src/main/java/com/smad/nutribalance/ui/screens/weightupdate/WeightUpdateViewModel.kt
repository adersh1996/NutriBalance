package com.smad.nutribalance.ui.screens.weightupdate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smad.nutribalance.data.repository.UserProfileRepository
import com.smad.nutribalance.data.repository.WeightHistoryRepository
import com.smad.nutribalance.domain.model.GoalType
import com.smad.nutribalance.domain.model.UserProfile
import com.smad.nutribalance.domain.model.WeightHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class WeightUpdateUiState(
    val currentProfile: UserProfile? = null,
    val lastUpdateDate: String = "",
    val daysSinceSetup: Int = 0,
    val weightInput: String = "",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val result: WeightUpdateResult? = null,
    val navigateToDashboard: Boolean = false
)

data class WeightUpdateResult(
    val oldWeight: Double,
    val newWeight: Double,
    val oldCalories: Double,
    val newCalories: Double,
    val oldGoalType: GoalType,
    val newGoalType: GoalType
)

@HiltViewModel
class WeightUpdateViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val weightHistoryRepository: WeightHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeightUpdateUiState())
    val uiState: StateFlow<WeightUpdateUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    init {
        viewModelScope.launch {
            val profile = userProfileRepository.userProfile.first()
            val setupDate = userProfileRepository.profileSetupDate.first()
            val lastUpdateDate = userProfileRepository.lastWeightUpdateDate.first()

            val today = dateFormat.format(Date())
            val daysSinceSetup = if (setupDate.isNotEmpty()) {
                val start = dateFormat.parse(setupDate) ?: Date()
                val diff = Date().time - start.time
                TimeUnit.MILLISECONDS.toDays(diff).toInt()
            } else 0

            _uiState.value = _uiState.value.copy(
                currentProfile = profile,
                lastUpdateDate = lastUpdateDate,
                daysSinceSetup = daysSinceSetup,
                isLoading = false
            )
        }
    }

    fun onWeightInputChanged(value: String) {
        _uiState.value = _uiState.value.copy(weightInput = value, error = null)
    }

    fun onUpdateWeight() {
        val state = _uiState.value
        val newWeight = state.weightInput.toDoubleOrNull()
        val oldProfile = state.currentProfile ?: return

        if (newWeight == null || newWeight <= 0 || newWeight > 300) {
            _uiState.value = state.copy(error = "Please enter a valid weight between 1 and 300 kg")
            return
        }

        _uiState.value = state.copy(isSaving = true, error = null)

        viewModelScope.launch {
            try {
                val newProfile = userProfileRepository.updateWeight(newWeight) ?: return@launch

                // Insert weight history entry
                val today = dateFormat.format(Date())
                weightHistoryRepository.insertEntry(
                    WeightHistory(
                        date = today,
                        weightKg = newWeight,
                        tdee = newProfile.tdee,
                        targetCalories = newProfile.targetCalories,
                        goalType = newProfile.goalType
                    )
                )

                val result = WeightUpdateResult(
                    oldWeight = oldProfile.weight,
                    newWeight = newWeight,
                    oldCalories = oldProfile.targetCalories,
                    newCalories = newProfile.targetCalories,
                    oldGoalType = oldProfile.goalType,
                    newGoalType = newProfile.goalType
                )

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    result = result,
                    currentProfile = newProfile
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Failed to update weight: ${e.localizedMessage}"
                )
            }
        }
    }

    fun onNavigateToDashboard() {
        _uiState.value = _uiState.value.copy(navigateToDashboard = true)
    }

    fun onNavigatedHandled() {
        _uiState.value = _uiState.value.copy(navigateToDashboard = false)
    }
}
