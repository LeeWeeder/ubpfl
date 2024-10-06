package com.leeweeder.yluxthenics.api_program.asset

import com.leeweeder.yluxthenics.api_program.util.WorkoutWrapper

enum class Period(val statusName: String, val cycles: List<Cycle>) {
    Start(
        statusName = "Start",
        cycles = listOf(
            Cycle.Hypertrophy,
            Cycle.Hypertrophy,
            Cycle.Skill,
            Cycle.Skill
        )
    ),
    NotProgress(
        statusName = "Not progressing",
        cycles = listOf(
            Cycle.Hypertrophy,
            Cycle.Hypertrophy,
            Cycle.Skill,
            Cycle.Skill,
            Cycle.Hypertrophy
        )
    ),
    WithProgress(
        statusName = "Progressing",
        cycles = listOf(
            Cycle.Hypertrophy,
            Cycle.Hypertrophy,
            Cycle.Skill,
            Cycle.Skill,
            Cycle.Skill
        )
    )
}

fun Period.flatten() = this.cycles.flatMapIndexed { cycleOrder, cycle ->
    cycle.plan.flatMapIndexed { weekNumber, plan ->
        plan.mapIndexed { workoutOrder, workout ->
            WorkoutWrapper(
                cycle = cycle,
                weekNumber = weekNumber + 1,
                workout = workout,
                name = workout.type.name + " " + workout.count,
                cycleOrder = cycleOrder,
                workoutOrder = workoutOrder + 1,
                period = this
            )
        }
    }
}
