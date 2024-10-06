package com.leeweeder.yluxthenics.feature_progression.data.source

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.leeweeder.yluxthenics.api_program.asset.ExerciseCategory

@Entity(indices = [
    Index(
        value = ["exerciseCategory", "level"],
        unique = true
    ),
    Index(
        value = ["exerciseCategory", "name"],
        unique = true
    )
])
data class Progression(
    val exerciseCategory: ExerciseCategory,
    val level: Int,
    val name: String,
    val isMileStone: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}
