package com.leeweeder.luxthenics.feature_progression.data

import com.leeweeder.luxthenics.api_program.asset.ExerciseCategory
import com.leeweeder.luxthenics.feature_progression.data.source.Progression
import com.leeweeder.luxthenics.feature_progression.data.source.ProgressionDao
import kotlinx.coroutines.flow.Flow

interface ProgressionRepository {
    fun getProgressionsStream(exerciseCategory: ExerciseCategory): Flow<List<Progression>>

    fun getProgressionStream(id: Int): Flow<Progression?>

    suspend fun getProgressions(exerciseCategory: ExerciseCategory): List<Progression>

    suspend fun getProgressionByLevel(exerciseCategory: ExerciseCategory, level: Int): Progression

    suspend fun getById(id: Int): Progression?

    suspend fun insert(progression: Progression)

    suspend fun insertAllProgressions(progressions: List<Progression>)

    suspend fun updateLevel(id: Int, newLevel: Int)

    suspend fun updateName(id: Int, newName: String)

    suspend fun deleteById(id: Int)
}

class DefaultProgressionRepository(private val dao: ProgressionDao) : ProgressionRepository {
    override fun getProgressionsStream(exerciseCategory: ExerciseCategory): Flow<List<Progression>> {
        return dao.observeAll(exerciseCategory)
    }

    override fun getProgressionStream(id: Int): Flow<Progression?> {
        TODO("Not yet implemented")
    }

    override suspend fun getProgressionByLevel(
        exerciseCategory: ExerciseCategory,
        level: Int
    ): Progression {
        return dao.getByLevel(exerciseCategory, level)
    }

    override suspend fun getProgressions(exerciseCategory: ExerciseCategory): List<Progression> {
        return dao.getAll(exerciseCategory)
    }

    override suspend fun getById(id: Int): Progression? {
        TODO("Not yet implemented")
    }

    override suspend fun insert(progression: Progression) {
        TODO("Not yet implemented")
    }

    override suspend fun insertAllProgressions(progressions: List<Progression>) {
        dao.insertAll(progressions)
    }

    override suspend fun updateLevel(id: Int, newLevel: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun updateName(id: Int, newName: String) {
        dao.updateName(id = id, newName = newName)
    }

    override suspend fun deleteById(id: Int) {
        TODO("Not yet implemented")
    }
}