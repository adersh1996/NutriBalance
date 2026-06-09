package com.smad.nutribalance.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smad.nutribalance.data.repository.UserProfileRepository
import com.smad.nutribalance.domain.model.NutritionCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val weightInput: String = "",
    val heightInput: String = "",
    val ageInput: String = "",
    val isMale: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateToDashboard: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onWeightChanged(value: String) {
        _uiState.value = _uiState.value.copy(weightInput = value, error = null)
    }

    fun onHeightChanged(value: String) {
        _uiState.value = _uiState.value.copy(heightInput = value, error = null)
    }

    fun onAgeChanged(value: String) {
        _uiState.value = _uiState.value.copy(ageInput = value, error = null)
    }

    fun onGenderChanged(isMale: Boolean) {
        _uiState.value = _uiState.value.copy(isMale = isMale)
    }

    fun onCalculate() {
        val state = _uiState.value
        val weight = state.weightInput.toDoubleOrNull()
        val height = state.heightInput.toDoubleOrNull()
        val age = state.ageInput.toIntOrNull()

        if (weight == null || weight <= 0) {
            _uiState.value = state.copy(error = "Please enter a valid weight")
            return
        }
        if (height == null || height <= 0) {
            _uiState.value = state.copy(error = "Please enter a valid height")
            return
        }
        if (age == null || age <= 0 || age > 120) {
            _uiState.value = state.copy(error = "Please enter a valid age")
            return
        }
        if (height < 100 || height > 250) {
            _uiState.value = state.copy(error = "Height must be between 100cm and 250cm")
            return
        }

        _uiState.value = state.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val profile = NutritionCalculator.calculate(
                    weightKg = weight,
                    heightCm = height,
                    ageYears = age,
                    isMale = state.isMale
                )
                userProfileRepository.saveProfileAndSetupDate(profile)
                userProfileRepository.setOnboardingComplete(false) // Will complete after scale screen
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    navigateToDashboard = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error saving profile: ${e.localizedMessage}"
                )
            }
        }
    }

    fun onNavigated() {
        _uiState.value = _uiState.value.copy(navigateToDashboard = false)
    }
}
