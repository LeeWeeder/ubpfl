package com.leeweeder.ubpfl.feature_progression.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.leeweeder.ubpfl.api_program.asset.ProgressiveExercise
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressionDao {
    @Query("SELECT * FROM progression WHERE progressiveExercise = :progressiveExercise ORDER BY level")
    fun observeAll(progressiveExercise: ProgressiveExercise): Flow<List<Progression>>

    @Query("SELECT * FROM progression WHERE id = :id")
    fun observeById(id: Int): Flow<Progression?>

    @Query("SELECT * FROM progression WHERE progressiveExercise = :progressiveExercise ORDER BY level")
    suspend fun getAll(progressiveExercise: ProgressiveExercise): List<Progression>

    @Query("SELECT * FROM progression WHERE progressiveExercise = :progressiveExercise AND level = :level")
    suspend fun getByLevel(progressiveExercise: ProgressiveExercise, level: Int): Progression

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