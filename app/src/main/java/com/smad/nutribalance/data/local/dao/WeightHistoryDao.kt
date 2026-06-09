package com.smad.nutribalance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smad.nutribalance.data.local.entity.WeightHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: WeightHistoryEntity)

    @Query("SELECT * FROM weight_history ORDER BY date ASC")
    fun getAllEntries(): Flow<List<WeightHistoryEntity>>

    @Query("SELECT * FROM weight_history ORDER BY date DESC LIMIT 1")
    suspend fun getLatestEntry(): WeightHistoryEntity?

    @Query("SELECT COUNT(*) FROM weight_history")
    suspend fun getEntryCount(): Int
}
