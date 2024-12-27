package com.leeweeder.ubpfl.assets

import com.leeweeder.ubpfl.api_program.asset.ProgressiveExercise
import com.leeweeder.ubpfl.feature_progression.data.source.Progression

private fun buildProgressionList(
    names: List<String>,
    category: ProgressiveExercise,
    milestoneIndex: Int = -1
): List<Progression> {
    return names.mapIndexed { index, s ->
        Progression(
            progressiveExercise = category,
            level = index + 1,
            name = s,
            isMileStone = if (milestoneIndex == -1) false else milestoneIndex == index
        )
    }
}

val initialData = listOf(
    buildProgressionList(
        names = listOf(
            "Tuck Front Lever",
            "Advanced Tuck Front Lever",
            "45 Degree Advanced Tuck Front Lever",
            "Half-lay Front Lever",
            "Half Straddle Front Lever",
            "Straddle Front Lever",
            "Full Front Lever"
        ), category = ProgressiveExercise.UnsupportedStatic, milestoneIndex = 6
    ),
    buildProgressionList(
        names = listOf(
            "Black Band Assisted Advanced Tuck Front Lever",
            "Red Band Assisted Advanced Tuck Front Lever"
        ), category = ProgressiveExercise.SupportedStatic
    ),
    buildProgressionList(
        names = listOf(
            "Tuck Front Lever Pull",
            "Advanced Tuck Front Lever Pull",
            "45 Degree Advanced Tuck Front Lever Pull",
            "Half-lay Front Lever Pull",
            "Straddle Front Lever Pull",
            "Full Front Lever Pull"
        ), category = ProgressiveExercise.StraightArmDynamic, milestoneIndex = 5
    ),
    buildProgressionList(
        names = listOf(
            "Tuck Front Lever Row",
            "Advanced Tuck Front Lever Row",
            "45 Degree Advanced Tuck Front Lever Row",
            "Half-lay Front Lever Row",
            "Straddle Front Lever Row",
            "Full Front Lever Row"
        ), category = ProgressiveExercise.BentArmDynamic, milestoneIndex = 5
    ),
    buildProgressionList(
        names = listOf(
            "Bodyweight Pull-up",
            "Weighted 5kg Pull-up",
        ), category = ProgressiveExercise.VerticalPull, milestoneIndex = 0
    ),
    buildProgressionList(
        names = listOf(
            "Inverted Bodyweight Row"
        ), category = ProgressiveExercise.HorizontalPull, milestoneIndex = 0
    ),
    buildProgressionList(
        names = listOf(
            "One Unit Lean Pseudo Planche Push-up"
        ), category = ProgressiveExercise.HorizontalPush
    ),
    buildProgressionList(
        names = listOf(
            "Weighted 5kg Push-up",
        ), category = ProgressiveExercise.WeightedHorizontalPush
    ),
    buildProgressionList(
        names = listOf(
            "Pike Push-up",
            "Advanced Pike Push-up",
            "Ring Dips"
        ), category = ProgressiveExercise.VerticalPush
    )
)
