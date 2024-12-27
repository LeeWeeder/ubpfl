package com.leeweeder.ubpfl.api_program.util

import com.leeweeder.ubpfl.api_program.asset.Macrocycle
import com.leeweeder.ubpfl.api_program.asset.Mesocycle
import com.leeweeder.ubpfl.api_program.asset.WeekNumber
import com.leeweeder.ubpfl.api_program.asset.Workout

data class WorkoutWrapper(
    val mesocycle: Mesocycle,
    val mesocycleNumber: Int,
    val weekNumber: WeekNumber,
    val workout: Workout,
    val workoutNumber: Int,
    val name: String,
    val macrocycle: Macrocycle
)