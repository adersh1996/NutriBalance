package com.smad.nutribalance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.smad.nutribalance.data.repository.UserProfileRepository
import com.smad.nutribalance.ui.navigation.NutriNavGraph
import com.smad.nutribalance.ui.navigation.Screen
import com.smad.nutribalance.ui.theme.NutriBalanceTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NutriBalanceTheme {
                // Determine start destination based on onboarding state
                val isOnboardingComplete by userProfileRepository.isOnboardingComplete
                    .collectAsState(initial = null)

                when (isOnboardingComplete) {
                    null -> {
                        // Still loading — show a spinner
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    true -> {
                        NutriNavGraph(startDestination = Screen.Dashboard.route)
                    }
                    false -> {
                        NutriNavGraph(startDestination = Screen.Welcome.route)
                    }
                }
            }
        }
    }
}