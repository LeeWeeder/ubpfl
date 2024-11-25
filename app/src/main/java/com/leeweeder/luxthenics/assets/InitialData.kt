package com.leeweeder.luxthenics.assets

import com.leeweeder.luxthenics.api_program.asset.ExerciseCategory
import com.leeweeder.luxthenics.feature_progression.data.source.Progression

private fun buildProgressionList(
    names: List<String>,
    category: ExerciseCategory,
    milestoneIndex: Int = -1
): List<Progression> {
    return names.mapIndexed { index, s ->
        Progression(
            exerciseCategory = category,
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
        ), category = ExerciseCategory.UnsupportedStatic, milestoneIndex = 6
    ),
    buildProgressionList(
        names = listOf(
            "Black Band Assisted Advanced Tuck Front Lever",
            "Red Band Assisted Advanced Tuck Front Lever"
        ), category = ExerciseCategory.SupportedStatic
    ),
    buildProgressionList(
        names = listOf(
            "Tuck Front Lever Pull",
            "Advanced Tuck Front Lever Pull",
            "45 Degree Advanced Tuck Front Lever Pull",
            "Half-lay Front Lever Pull",
            "Straddle Front Lever Pull",
            "Full Front Lever Pull"
        ), category = ExerciseCategory.StraightArmDynamic, milestoneIndex = 5
    ),
    buildProgressionList(
        names = listOf(
            "Tuck Front Lever Row",
            "Advanced Tuck Front Lever Row",
            "45 Degree Advanced Tuck Front Lever Row",
            "Half-lay Front Lever Row",
            "Straddle Front Lever Row",
            "Full Front Lever Row"
        ), category = ExerciseCategory.BentArmDynamic, milestoneIndex = 5
    ),
    buildProgressionList(
        names = listOf(
            "Bodyweight Pull-up",
            "Weighted 5kg Pull-up",
        ), category = ExerciseCategory.VerticalPull, milestoneIndex = 0
    ),
    buildProgressionList(
        names = listOf(
            "Inverted Bodyweight Row"
        ), category = ExerciseCategory.HorizontalPull, milestoneIndex = 0
    ),
    buildProgressionList(
        names = listOf(
            "One Unit Lean Pseudo Planche Push-up"
        ), category = ExerciseCategory.HorizontalPush
    ),
    buildProgressionList(
        names = listOf(
            "Weighted 5kg Push-up",
        ), category = ExerciseCategory.WeightedHorizontalPush
    ),
    buildProgressionList(
        names = listOf(
            "Pike Push-up",
            "Advanced Pike Push-up",
            "Ring Dips"
        ), category = ExerciseCategory.VerticalPush
    )
)
