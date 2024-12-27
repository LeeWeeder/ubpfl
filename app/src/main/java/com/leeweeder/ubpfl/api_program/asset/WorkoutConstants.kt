package com.leeweeder.ubpfl.api_program.asset

object WorkoutConstants {
    private val wristWarmUpRoutine = FixedExercise(
        name = "GMB Wrist Warm-up",
        volume = 10,
        type = ExerciseType.Repetition
    )

    val warmUp = listOf(
        FixedExercise(
            name = "Jump Rope",
            volume = 60,
            type = ExerciseType.Timed
        ),
        wristWarmUpRoutine,
        FixedExercise(
            name = "Scapula Rotations",
            volume = 10,
            type = ExerciseType.Repetition
        ),
        FixedExercise(
            name = "Yuri's Band Warm-up Routine",
            volume = 5,
            type = ExerciseType.Repetition
        ),
        FixedExercise(
            name = "Banded External Rotations",
            volume = 10,
            type = ExerciseType.Repetition
        ),
        FixedExercise(
            name = "Trap-3 Raise",
            volume = 10,
            type = ExerciseType.Repetition
        ),
        FixedExercise(
            name = "Shoulder Dislocates",
            volume = 10,
            type = ExerciseType.Repetition
        ),
        FixedExercise(
            name = "Scapula Push-up",
            volume = 10,
            type = ExerciseType.Repetition
        ),
        FixedExercise(
            name = "Scapula Pull-up",
            volume = 10,
            type = ExerciseType.Repetition
        ),
        FixedExercise(
            name = "Wall Slides",
            volume = 10,
            type = ExerciseType.Repetition
        ),
        FixedExercise(
            name = "Deadbugs",
            volume = 30,
            type = ExerciseType.Timed
        )
    )

    val skill = FixedExercise(
        name = "Freestanding Handstand Practice",
        volume = 10 * 60,
        type = ExerciseType.Timed
    )

    private fun toCoolDownExercise(name: String) =
        FixedExercise(name = name, volume = 30, type = ExerciseType.Timed)

    val coolDown = listOf(
        wristWarmUpRoutine,
        toCoolDownExercise("Reverse Shoulder Stretch"),
        toCoolDownExercise("Butcher Block"),
        toCoolDownExercise("Arm Raise"),
        toCoolDownExercise("Chest Stretch"),
        toCoolDownExercise("Tricep Stretch"),
        toCoolDownExercise("Shoulder Stretch"),
        toCoolDownExercise("Cobra Stretch"),
        toCoolDownExercise("Baby Pose"),
        FixedExercise(
            name = "Dead Hang",
            volume = 60,
            type = ExerciseType.Timed
        )
    )
}

data class FixedExercise(
    val name: String,
    val volume: Int,
    val type: ExerciseType
)
