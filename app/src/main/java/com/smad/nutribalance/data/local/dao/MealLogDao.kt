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

    @Query("""
        SELECT COUNT(DISTINCT date) FROM meal_log
        WHERE date IN (
            SELECT DISTINCT date FROM meal_log
            GROUP BY date
            HAVING COUNT(DISTINCT mealType) >= 3
        )
        AND date >= date('now', '-30 days')
    """)
    fun getStreakDays(): Flow<Int>

    @Query("SELECT * FROM meal_log ORDER BY date DESC, timestamp DESC")
    fun getAllMealLogs(): Flow<List<MealLogEntity>>

    @Query("""
        SELECT * FROM meal_log 
        WHERE date = :date AND mealType = :mealType 
        LIMIT 1
    """)
    suspend fun getMealForDateAndType(date: String, mealType: String): MealLogEntity?
}
