package com.leeweeder.ubpfl.util

import com.leeweeder.ubpfl.api_program.asset.ProgressiveExercise
import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data class ProgressionSelection(val progressiveExercise: ProgressiveExercise) : Screen


    sealed interface BottomBarItemScreen : Screen {
        @Serializable
        data object RoutineBottomBarItem : BottomBarItemScreen

        @Serializable
        data object StatisticsBottomBarItem : BottomBarItemScreen

        @Serializable
        data object SettingsBottomBarItem : BottomBarItemScreen

        @Serializable
        data object Companion : Screen
    }

    sealed interface WorkoutPlayThroughScreen : Screen {
        @Serializable
        data object WarmUpContent : Screen

        @Serializable
        data object ExerciseContent : Screen

        @Serializable
        data object Companion: Screen
    }
}