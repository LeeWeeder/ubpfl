package com.leeweeder.ubpfl.feature_progression.data.source

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.leeweeder.ubpfl.api_program.asset.ProgressiveExercise

@Entity(indices = [
    Index(
        value = ["progressiveExercise", "level"],
        unique = true
    ),
    Index(
        value = ["progressiveExercise", "name"],
        unique = true
    )
])
data class Progression(
    val progressiveExercise: ProgressiveExercise,
    val level: Int,
    val name: String,
    val isMileStone: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}
