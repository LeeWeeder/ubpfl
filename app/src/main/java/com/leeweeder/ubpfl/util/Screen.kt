package com.leeweeder.ubpfl.util

import com.leeweeder.ubpfl.api_program.asset.ProgressiveExercise
import kotlinx.serialization.Serializable

sealed interface Screen {

    @Serializable
    data object Routine: Screen

    @Serializable
    data object Statistics: Screen

    @Serializable
    data object Settings: Screen

    @Serializable
    data class ProgressionSelection(val progressiveExercise: ProgressiveExercise): Screen

    @Serializable
    data object WarmUp: Screen
}