package com.smad.nutribalance.data.repository

import android.content.Context
import com.smad.nutribalance.BuildConfig
import com.smad.nutribalance.data.local.dao.MealLogDao
import com.smad.nutribalance.data.local.entity.MealLogEntity
import com.smad.nutribalance.data.remote.GeminiApiService
import com.smad.nutribalance.data.remote.GeminiRequest
import com.smad.nutribalance.domain.model.MealLog
import com.smad.nutribalance.domain.model.MealType
import com.smad.nutribalance.domain.model.UserProfile
import com.smad.nutribalance.util.NetworkUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealLogRepository @Inject constructor(
    private val mealLogDao: MealLogDao,
    private val geminiApiService: GeminiApiService,
    @ApplicationContext private val context: Context
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getTodaysMeals(): Flow<List<MealLog>> {
        val today = dateFormat.format(Date())
        return mealLogDao.getMealsForDate(today).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getTotalCaloriesToday(): Flow<Double?> {
        val today = dateFormat.format(Date())
        return mealLogDao.getTotalCaloriesForDate(today)
    }

    fun getStreakDays(): Flow<Int> = mealLogDao.getStreakDays()

    fun getAllMealLogs(): Flow<List<MealLog>> {
        return mealLogDao.getAllMealLogs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun isMealLoggedToday(mealType: MealType): Boolean {
        val today = dateFormat.format(Date())
        return mealLogDao.getMealForDateAndType(today, mealType.name) != null
    }

    suspend fun insertMealLog(mealLog: MealLog) {
        mealLogDao.insertMealLog(mealLog.toEntity())
    }

    /**
     * Call Gemini API to get a meal plan for the given foods and profile.
     * Returns Result<String> with the AI response text.
     */
    suspend fun getMealSuggestion(
        userProfile: UserProfile,
        mealType: MealType,
        mealCalories: Double,
        foodList: String,
        previouslyConsumedToday: Double
    ): Result<String> {
        if (!NetworkUtil.isNetworkAvailable(context)) {
            return Result.failure(Exception("No internet connection. Please check your network and try again."))
        }

        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            return Result.failure(Exception("Gemini API key not configured. Please add your key to local.properties."))
        }

        val prompt = buildPrompt(userProfile, mealType, mealCalories, foodList, previouslyConsumedToday)
        val request = GeminiRequest(
            contents = listOf(
                GeminiRequest.Content(
                    parts = listOf(GeminiRequest.Part(text = prompt)),
                    role = "user"
                )
            )
        )

        return try {
            val response = geminiApiService.generateContent(apiKey, request)
            if (response.isSuccessful) {
                val text = response.body()?.extractText()
                    ?: "Could not parse AI response."
                Result.success(text)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("API Error (${response.code()}): $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.localizedMessage}"))
        }
    }

    private fun buildPrompt(
        userProfile: UserProfile,
        mealType: MealType,
        mealCalories: Double,
        foodList: String,
        previouslyConsumedToday: Double
    ): String {
        val measureUnit = if (userProfile.hasScale) "grams" else "common Indian household measures (cups, tablespoons, pieces, servings)"
        val goalDescription = when (userProfile.goalType.displayName) {
            "Calorie Deficit" -> "weight loss (calorie deficit)"
            "Calorie Surplus" -> "weight gain (calorie surplus)"
            else -> "weight maintenance"
        }

        return """
You are a nutrition assistant helping an Indian user plan their meal portions.

User profile:
- Current weight: ${String.format("%.1f", userProfile.weight)}kg
- Ideal weight: ${String.format("%.1f", userProfile.idealWeight)}kg
- Goal: $goalDescription
- Meal: ${mealType.displayName}
- Calorie target for this meal: ${String.format("%.0f", mealCalories)} kcal
- Has weighing scale: ${userProfile.hasScale}
- Total daily calorie goal: ${String.format("%.0f", userProfile.targetCalories)} kcal
- Calories already consumed today (other meals): ${String.format("%.0f", previouslyConsumedToday)} kcal

Foods available: $foodList

Instructions:
- Calculate how much of each food the user should eat to hit their calorie target as closely as possible.
- Give amounts in $measureUnit.
- Use accurate Indian food calorie values (e.g. cooked white rice ~130 kcal/100g, dal ~100 kcal/100g, egg ~70 kcal each, chapati ~80 kcal each, idli ~40 kcal each, dosa ~170 kcal each, sambar ~50 kcal/100ml, fish curry ~120 kcal/100g, chicken curry ~150 kcal/100g, puttu ~150 kcal/100g, appam ~100 kcal each, Kerala parotta ~260 kcal each).
- Format your response as a clean meal plan with:
  * Each food name with its portion and calories
  * Total calories for this meal
  * Remaining calories for the rest of the day (calculate: ${String.format("%.0f", userProfile.targetCalories)} - ${String.format("%.0f", previouslyConsumedToday)} - [this meal's calories])
- End with one short motivational tip.
- Keep the tone friendly and simple.
- Use emojis sparingly to make it readable.
        """.trimIndent()
    }

    // Mapper: Entity → Domain
    private fun MealLogEntity.toDomain(): MealLog {
        return MealLog(
            id = id,
            date = date,
            mealType = MealType.valueOf(mealType),
            foodsEntered = foodsEntered,
            aiResponse = aiResponse,
            caloriesConsumed = caloriesConsumed,
            timestamp = timestamp
        )
    }

    // Mapper: Domain → Entity
    private fun MealLog.toEntity(): MealLogEntity {
        return MealLogEntity(
            id = id,
            date = date,
            mealType = mealType.name,
            foodsEntered = foodsEntered,
            aiResponse = aiResponse,
            caloriesConsumed = caloriesConsumed,
            timestamp = timestamp
        )
    }
}
