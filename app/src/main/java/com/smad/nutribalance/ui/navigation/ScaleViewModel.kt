package com.smad.nutribalance.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smad.nutribalance.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScaleViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    fun setScale(hasScale: Boolean) {
        viewModelScope.launch {
            userProfileRepository.setHasScale(hasScale)
            userProfileRepository.setOnboardingComplete(true)
        }
    }
}
