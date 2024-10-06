package com.leeweeder.yluxthenics.data.source

import androidx.room.TypeConverter
import com.leeweeder.yluxthenics.api_program.asset.ExerciseCategory

class Converters {
    @TypeConverter
    fun fromExerciseCategory(exerciseCategory: ExerciseCategory) = exerciseCategory.ordinal

    @TypeConverter
    fun toExerciseCategory(value: Int): ExerciseCategory {
        return ExerciseCategory.entries[value]
    }
}