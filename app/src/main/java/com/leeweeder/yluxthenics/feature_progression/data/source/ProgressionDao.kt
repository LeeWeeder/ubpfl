package com.leeweeder.yluxthenics.feature_progression.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.leeweeder.yluxthenics.api_program.asset.ExerciseCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressionDao {
    @Query("SELECT * FROM progression WHERE exerciseCategory = :exerciseCategory ORDER BY level")
    fun observeAll(exerciseCategory: ExerciseCategory): Flow<List<Progression>>

    @Query("SELECT * FROM progression WHERE id = :id")
    fun observeById(id: Int): Flow<Progression?>

    @Query("SELECT * FROM progression WHERE exerciseCategory = :exerciseCategory ORDER BY level")
    suspend fun getAll(exerciseCategory: ExerciseCategory): List<Progression>

    @Query("SELECT * FROM progression WHERE exerciseCategory = :exerciseCategory AND level = :level")
    suspend fun getByLevel(exerciseCategory: ExerciseCategory, level: Int): Progression

    @Query("SELECT * FROM progression WHERE id = :id")
    suspend fun getById(id: Int): Progression?

    @Insert
    suspend fun insert(progression: Progression)

    @Insert
    suspend fun insertAll(progressions: List<Progression>)

    @Query("UPDATE progression SET level = :newLevel WHERE id = :id")
    suspend fun updateLevel(id: Int, newLevel: Int)

    @Query("UPDATE progression SET name = :newName WHERE id = :id")
    suspend fun updateName(id: Int, newName: String)

    @Query("DELETE FROM progression WHERE id = :id")
    suspend fun deleteById(id: Int)
}