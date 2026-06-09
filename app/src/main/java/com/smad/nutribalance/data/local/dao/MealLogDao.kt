package com.smad.nutribalance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smad.nutribalance.data.local.entity.MealLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealLog(mealLog: MealLogEntity)

    @Query("SELECT * FROM meal_log WHERE date = :date ORDER BY timestamp ASC")
    fun getMealsForDate(date: String): Flow<List<MealLogEntity>>

    @Query("SELECT * FROM meal_log WHERE date = :date ORDER BY timestamp ASC")
    suspend fun getMealsForDateOnce(date: String): List<MealLogEntity>

    @Query("SELECT SUM(caloriesConsumed) FROM meal_log WHERE date = :date")
    fun getTotalCaloriesForDate(date: String): Flow<Double?>

    @Query("SELECT SUM(caloriesConsumed) FROM meal_log WHERE date = :date")
    suspend fun getTotalCaloriesForDateOnce(date: String): Double?

    @Query("SELECT * FROM meal_log ORDER BY date DESC, timestamp DESC")
    fun getAllMealLogs(): Flow<List<MealLogEntity>>

    @Query("""
        SELECT * FROM meal_log 
        WHERE date = :date AND mealType = :mealType 
        LIMIT 1
    """)
    suspend fun getMealForDateAndType(date: String, mealType: String): MealLogEntity?

    /**
     * Returns all distinct dates (yyyy-MM-dd) that have at least one meal log, newest first.
     */
    @Query("SELECT DISTINCT date FROM meal_log ORDER BY date DESC")
    fun getDistinctDates(): Flow<List<String>>

    /**
     * Returns all distinct dates where ALL 3 meal types were logged on that date.
     * Used for consecutive-day streak calculation in the ViewModel.
     */
    @Query("""
        SELECT DISTINCT date FROM meal_log
        GROUP BY date
        HAVING COUNT(DISTINCT mealType) >= 3
        ORDER BY date DESC
    """)
    fun getDatesWithAllMealsLogged(): Flow<List<String>>
}
