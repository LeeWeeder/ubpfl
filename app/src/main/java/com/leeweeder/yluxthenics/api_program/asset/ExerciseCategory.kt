package com.leeweeder.yluxthenics.api_program.asset

enum class ExerciseCategory(
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

fun ExerciseCategory.formatName(): String {
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