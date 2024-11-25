package com.leeweeder.luxthenics.util

import com.leeweeder.luxthenics.api_program.asset.ExerciseCategory
import kotlinx.serialization.Serializable

sealed interface Screen {

    @Serializable
    data object Routine: Screen

    @Serializable
    data object Statistics: Screen

    @Serializable
    data object Settings: Screen

    @Serializable
    data class ProgressionSelection(val exerciseCategory: ExerciseCategory): Screen

    @Serializable
    data object WarmUp: Screen

    @Serializable
    data object Preparation: Screen
}