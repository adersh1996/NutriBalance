package com.smad.nutribalance.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nutri_prefs")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val HAS_SCALE_KEY = booleanPreferencesKey("has_scale")
        val ONBOARDING_COMPLETE_KEY = booleanPreferencesKey("onboarding_complete")
        val PROFILE_SETUP_DATE_KEY = stringPreferencesKey("profileSetupDate")
        val LAST_WEIGHT_UPDATE_DATE_KEY = stringPreferencesKey("lastWeightUpdateDate")
        val LAST_REMINDER_DATE_KEY = stringPreferencesKey("lastReminderDate")
    }

    val hasScale: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[HAS_SCALE_KEY] ?: false
    }

    val isOnboardingComplete: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ONBOARDING_COMPLETE_KEY] ?: false
    }

    val profileSetupDate: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[PROFILE_SETUP_DATE_KEY] ?: ""
    }

    val lastWeightUpdateDate: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LAST_WEIGHT_UPDATE_DATE_KEY] ?: ""
    }

    val lastReminderDate: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LAST_REMINDER_DATE_KEY] ?: ""
    }

    suspend fun setHasScale(hasScale: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[HAS_SCALE_KEY] = hasScale
        }
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETE_KEY] = complete
        }
    }

    suspend fun setProfileSetupDate(date: String) {
        context.dataStore.edit { prefs ->
            prefs[PROFILE_SETUP_DATE_KEY] = date
        }
    }

    suspend fun setLastWeightUpdateDate(date: String) {
        context.dataStore.edit { prefs ->
            prefs[LAST_WEIGHT_UPDATE_DATE_KEY] = date
        }
    }

    suspend fun setLastReminderDate(date: String) {
        context.dataStore.edit { prefs ->
            prefs[LAST_REMINDER_DATE_KEY] = date
        }
    }
}
