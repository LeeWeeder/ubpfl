@file:JvmName("MacrocycleKt")

package com.leeweeder.ubpfl.api_program.asset

import com.leeweeder.ubpfl.api_program.util.WorkoutWrapper

enum class Macrocycle(val statusName: String, val mesocycles: List<Mesocycle>) {
    Start(
        statusName = "Start",
        mesocycles = listOf(
            Mesocycle.Hypertrophy,
            Mesocycle.Hypertrophy,
            Mesocycle.Skill,
            Mesocycle.Skill
        )
    ),
    NotProgress(
        statusName = "Not Progressing",
        mesocycles = listOf(
            Mesocycle.Hypertrophy,
            Mesocycle.Hypertrophy,
            Mesocycle.Skill,
            Mesocycle.Skill,
            Mesocycle.Hypertrophy
        )
    ),
    WithProgress(
        statusName = "Progressing",
        mesocycles = listOf(
            Mesocycle.Hypertrophy,
            Mesocycle.Hypertrophy,
            Mesocycle.Skill,
            Mesocycle.Skill,
            Mesocycle.Skill
        )
    )
}

fun Macrocycle.flatten() = this.mesocycles.flatMapIndexed { cycleOrder, cycle ->
    cycle.plan.flatMapIndexed { weekNumber, plan ->
        plan.mapIndexed { workoutOrder, workout ->
            WorkoutWrapper(
                mesocycle = cycle,
                weekNumber = weekNumber + 1,
                workout = workout,
                name = workout.type.name + " " + workout.count,
                mesocycleNumber = cycleOrder + 1,
                workoutNumber = workoutOrder + 1,
                macrocycle = this
            )
        }
    }
}
