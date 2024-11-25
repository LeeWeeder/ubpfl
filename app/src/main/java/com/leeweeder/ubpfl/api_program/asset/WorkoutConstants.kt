package com.leeweeder.ubpfl.api_program.asset

object WorkoutConstants {
    private val wristWarmUpRoutine = Exercise(
        name = "GMB Wrist Warm-up",
        volume = 10,
        type = ExerciseType.Repetition
    )

    val warmUp = listOf(
        Exercise(
            name = "Jump Rope",
            volume = 60,
            type = ExerciseType.Timed
        ),
        wristWarmUpRoutine,
        Exercise(
            name = "Scapula Rotations",
            volume = 10,
            type = ExerciseType.Repetition
        ),
        Exercise(
            name = "Yuri's Band Warm-up Routine",
            volume = 5,
            type = ExerciseType.Repetition
        ),
        Exercise(
            name = "Banded External Rotations",
            volume = 10,
            type = ExerciseType.Repetition
        ),
        Exercise(
            name = "Trap-3 Raise",
            volume = 10,
            type = ExerciseType.Repetition
        ),
        Exercise(
            name = "Shoulder Dislocates",
            volume = 10,
            type = ExerciseType.Repetition
        ),
        Exercise(
            name = "Scapula Push-up",
            volume = 10,
            type = ExerciseType.Repetition
        ),
        Exercise(
            name = "Scapula Pull-up",
            volume = 10,
            type = ExerciseType.Repetition
        ),
        Exercise(
            name = "Wall Slides",
            volume = 10,
            type = ExerciseType.Repetition
        ),
        Exercise(
            name = "Deadbugs",
            volume = 30,
            type = ExerciseType.Timed
        )
    )

    val skill = Exercise(
        name = "Freestanding Handstand Practice",
        volume = 10 * 60,
        type = ExerciseType.Timed
    )

    private fun toCoolDownExercise(name: String) =
        Exercise(name = name, volume = 30, type = ExerciseType.Timed)

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
        Exercise(
            name = "Dead Hang",
            volume = 60,
            type = ExerciseType.Timed
        )
    )
}

data class Exercise(
    val name: String,
    val volume: Int,
    val type: ExerciseType
)
