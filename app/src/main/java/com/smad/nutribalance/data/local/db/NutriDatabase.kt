package com.smad.nutribalance.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.smad.nutribalance.data.local.dao.MealLogDao
import com.smad.nutribalance.data.local.dao.UserProfileDao
import com.smad.nutribalance.data.local.dao.WeightHistoryDao
import com.smad.nutribalance.data.local.entity.MealLogEntity
import com.smad.nutribalance.data.local.entity.UserProfileEntity
import com.smad.nutribalance.data.local.entity.WeightHistoryEntity

@Database(
    entities = [UserProfileEntity::class, MealLogEntity::class, WeightHistoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class NutriDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun mealLogDao(): MealLogDao
    abstract fun weightHistoryDao(): WeightHistoryDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `weight_history` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `date` TEXT NOT NULL,
                        `weightKg` REAL NOT NULL,
                        `tdee` REAL NOT NULL,
                        `targetCalories` REAL NOT NULL,
                        `goalType` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
