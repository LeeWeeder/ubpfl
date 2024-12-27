package com.leeweeder.ubpfl.data.source

import androidx.room.TypeConverter
import com.leeweeder.ubpfl.api_program.asset.ProgressiveExercise

class Converters {
    @TypeConverter
    fun fromExerciseCategory(progressiveExercise: ProgressiveExercise) = progressiveExercise.ordinal

    @TypeConverter
    fun toExerciseCategory(value: Int): ProgressiveExercise {
        return ProgressiveExercise.entries[value]
    }
}