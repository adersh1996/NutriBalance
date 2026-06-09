package com.smad.nutribalance.data.repository

import com.smad.nutribalance.data.local.dao.UserProfileDao
import com.smad.nutribalance.data.local.entity.UserProfileEntity
import com.smad.nutribalance.data.local.preferences.UserPreferencesDataStore
import com.smad.nutribalance.domain.model.GoalType
import com.smad.nutribalance.domain.model.NutritionCalculator
import com.smad.nutribalance.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val preferencesDataStore: UserPreferencesDataStore
) {
    val userProfile: Flow<UserProfile?> = combine(
        userProfileDao.getProfile(),
        preferencesDataStore.hasScale
    ) { entity, hasScale ->
        entity?.toDomain(hasScale)
    }

    val hasScale: Flow<Boolean> = preferencesDataStore.hasScale
    val isOnboardingComplete: Flow<Boolean> = preferencesDataStore.isOnboardingComplete
    val profileSetupDate: Flow<String> = preferencesDataStore.profileSetupDate
    val lastWeightUpdateDate: Flow<String> = preferencesDataStore.lastWeightUpdateDate

    suspend fun saveProfile(profile: UserProfile) {
        userProfileDao.upsertProfile(profile.toEntity())
    }

    /**
     * Called on first profile setup. Saves profile and records the setup date in DataStore.
     */
    suspend fun saveProfileAndSetupDate(profile: UserProfile) {
        userProfileDao.upsertProfile(profile.toEntity())
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        preferencesDataStore.setProfileSetupDate(today)
        preferencesDataStore.setLastWeightUpdateDate(today)
    }

    /**
     * Updates only the weight field, recalculates all nutrition values, and persists.
     * Also records the update date in DataStore.
     * Returns the new computed UserProfile for display comparison.
     */
    suspend fun updateWeight(newWeightKg: Double): UserProfile? {
        val existingEntity = userProfileDao.getProfileOnce() ?: return null
        val hasScale = existingEntity.hasScale

        val newProfile = NutritionCalculator.calculate(
            weightKg = newWeightKg,
            heightCm = existingEntity.height,
            ageYears = existingEntity.age,
            isMale = existingEntity.isMale,
            hasScale = hasScale
        )

        userProfileDao.upsertProfile(newProfile.toEntity())

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        preferencesDataStore.setLastWeightUpdateDate(today)

        return newProfile
    }

    suspend fun getProfileOnce(): UserProfile? {
        val entity = userProfileDao.getProfileOnce() ?: return null
        val hasScale = entity.hasScale
        return entity.toDomain(hasScale)
    }

    suspend fun setHasScale(hasScale: Boolean) {
        preferencesDataStore.setHasScale(hasScale)
        // Also update the entity's hasScale
        userProfileDao.getProfileOnce()?.let { entity ->
            userProfileDao.upsertProfile(entity.copy(hasScale = hasScale))
        }
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        preferencesDataStore.setOnboardingComplete(complete)
    }

    suspend fun setLastReminderDate(date: String) {
        preferencesDataStore.setLastReminderDate(date)
    }

    // Mapper: Entity → Domain
    private fun UserProfileEntity.toDomain(hasScale: Boolean): UserProfile {
        return UserProfile(
            weight = weight,
            height = height,
            age = age,
            isMale = isMale,
            idealWeight = idealWeight,
            bmr = bmr,
            tdee = tdee,
            goalType = GoalType.valueOf(goalType),
            targetCalories = targetCalories,
            breakfastCalories = breakfastCalories,
            lunchCalories = lunchCalories,
            dinnerCalories = dinnerCalories,
            hasScale = hasScale
        )
    }

    // Mapper: Domain → Entity
    private fun UserProfile.toEntity(): UserProfileEntity {
        return UserProfileEntity(
            id = 1,
            weight = weight,
            height = height,
            age = age,
            isMale = isMale,
            idealWeight = idealWeight,
            bmr = bmr,
            tdee = tdee,
            goalType = goalType.name,
            targetCalories = targetCalories,
            breakfastCalories = breakfastCalories,
            lunchCalories = lunchCalories,
            dinnerCalories = dinnerCalories,
            hasScale = hasScale
        )
    }
}
