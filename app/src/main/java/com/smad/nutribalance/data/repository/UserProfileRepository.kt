package com.smad.nutribalance.data.repository

import com.smad.nutribalance.data.local.dao.UserProfileDao
import com.smad.nutribalance.data.local.entity.UserProfileEntity
import com.smad.nutribalance.data.local.preferences.UserPreferencesDataStore
import com.smad.nutribalance.domain.model.GoalType
import com.smad.nutribalance.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
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

    suspend fun saveProfile(profile: UserProfile) {
        userProfileDao.upsertProfile(profile.toEntity())
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
