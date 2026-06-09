package com.smad.nutribalance.data.repository

import com.smad.nutribalance.data.local.dao.WeightHistoryDao
import com.smad.nutribalance.data.local.entity.WeightHistoryEntity
import com.smad.nutribalance.domain.model.GoalType
import com.smad.nutribalance.domain.model.WeightHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeightHistoryRepository @Inject constructor(
    private val weightHistoryDao: WeightHistoryDao
) {
    val allEntries: Flow<List<WeightHistory>> = weightHistoryDao.getAllEntries().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun insertEntry(entry: WeightHistory) {
        weightHistoryDao.insertEntry(entry.toEntity())
    }

    suspend fun getLatestEntry(): WeightHistory? {
        return weightHistoryDao.getLatestEntry()?.toDomain()
    }

    suspend fun getEntryCount(): Int = weightHistoryDao.getEntryCount()

    // Mapper: Entity → Domain
    private fun WeightHistoryEntity.toDomain(): WeightHistory {
        return WeightHistory(
            id = id,
            date = date,
            weightKg = weightKg,
            tdee = tdee,
            targetCalories = targetCalories,
            goalType = GoalType.valueOf(goalType)
        )
    }

    // Mapper: Domain → Entity
    private fun WeightHistory.toEntity(): WeightHistoryEntity {
        return WeightHistoryEntity(
            id = id,
            date = date,
            weightKg = weightKg,
            tdee = tdee,
            targetCalories = targetCalories,
            goalType = goalType.name
        )
    }
}
