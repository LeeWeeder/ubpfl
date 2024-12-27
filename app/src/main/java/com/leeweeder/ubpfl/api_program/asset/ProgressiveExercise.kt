package com.leeweeder.ubpfl.api_program.asset

enum class ProgressiveExercise(
    val volumeRange: IntRange, val type: ExerciseType
) {
    UnsupportedStatic(
        volumeRange = 3..10, type = ExerciseType.Timed
    ),
    SupportedStatic(
        volumeRange = 10..15, type = ExerciseType.Timed
    ),
    StraightArmDynamic(
        volumeRange = 1..5, type = ExerciseType.Repetition
    ),
    BentArmDynamic(
        volumeRange = 1..5, type = ExerciseType.Repetition
    ),
    VerticalPull(
        volumeRange = 8..12, type = ExerciseType.Repetition
    ),
    HorizontalPull(
        volumeRange = 8..12, type = ExerciseType.Repetition
    ),
    VerticalPush(
        volumeRange = 8..12, type = ExerciseType.Repetition
    ),
    HorizontalPush(
        volumeRange = 8..12, type = ExerciseType.Repetition
    ),
    WeightedHorizontalPush(
        volumeRange = 8..12, type = ExerciseType.Repetition
    )
}

fun ProgressiveExercise.formatName(): String {
    val word = this.name
    val result = StringBuilder()
    for (i in word.indices) {
        val char = word[i]
        result.append(char)
        if (i < word.length - 1 && char.isLowerCase() && word[i + 1].isUpperCase()) {
            result.append(" ")
        }
    }
    return result.toString()
}

/**
 * Formats the volume range of IntRange into readable format based on the type of exercise.
 *
 * `seconds` for [ExerciseType.Timed] and `reps` for [ExerciseType.Repetition]
 *
 * Examples:
 * `volumeRange` of 1-5 of type [ExerciseType.Timed] will be formatted as `1-5 seconds`
 * `volumeRange` of 1-5 of type [ExerciseType.Repetition] will be formatted as `1-5 reps`
 * */
fun ProgressiveExercise.formatVolumeRange(): String {
    val volumeRangeText = "${this.volumeRange.first}-${this.volumeRange.last}"
    return when (this.type) {
        ExerciseType.Timed -> "$volumeRangeText seconds"
        ExerciseType.Repetition -> "$volumeRangeText reps"
    }
}