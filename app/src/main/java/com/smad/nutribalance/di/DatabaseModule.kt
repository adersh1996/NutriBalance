package com.smad.nutribalance.di

import android.content.Context
import androidx.room.Room
import com.smad.nutribalance.data.local.dao.MealLogDao
import com.smad.nutribalance.data.local.dao.UserProfileDao
import com.smad.nutribalance.data.local.dao.WeightHistoryDao
import com.smad.nutribalance.data.local.db.NutriDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideNutriDatabase(@ApplicationContext context: Context): NutriDatabase {
        return Room.databaseBuilder(
            context,
            NutriDatabase::class.java,
            "nutri_balance_db"
        )
            .addMigrations(NutriDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideUserProfileDao(db: NutriDatabase): UserProfileDao = db.userProfileDao()

    @Provides
    @Singleton
    fun provideMealLogDao(db: NutriDatabase): MealLogDao = db.mealLogDao()

    @Provides
    @Singleton
    fun provideWeightHistoryDao(db: NutriDatabase): WeightHistoryDao = db.weightHistoryDao()
}
