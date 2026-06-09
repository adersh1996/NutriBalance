package com.smad.nutribalance.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smad.nutribalance.data.local.dao.MealLogDao
import com.smad.nutribalance.data.local.dao.UserProfileDao
import com.smad.nutribalance.data.local.entity.MealLogEntity
import com.smad.nutribalance.data.local.entity.UserProfileEntity

@Database(
    entities = [UserProfileEntity::class, MealLogEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NutriDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun mealLogDao(): MealLogDao
}
