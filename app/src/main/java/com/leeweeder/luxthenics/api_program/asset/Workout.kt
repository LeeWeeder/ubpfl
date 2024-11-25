package com.leeweeder.luxthenics.api_program.asset

import com.leeweeder.luxthenics.api_program.util.ExerciseCategoryNumberOfSet

sealed class Workout(open val count: Int) {
    abstract val type: WorkoutType
    abstract val workouts: List<ExerciseCategoryNumberOfSet>

    sealed class WorkoutType(open val count: Int) {
        abstract val name: String

        data class Hypertrophy(override val count: Int) : WorkoutType(count = count) {
            fun buildWorkout(
                numberOfSet: HypertrophyWorkoutNumberOfSet,
                first: PullCategory
            ): List<ExerciseCategoryNumberOfSet> {
                return when (first) {
                    PullCategory.Vertical -> {
                        listOf(
                            ExerciseCategoryNumberOfSet(
                                ExerciseCategory.VerticalPull,
                                numberOfSet.verticalPull
                            ),
                            ExerciseCategoryNumberOfSet(
                                ExerciseCategory.HorizontalPull,
                                numberOfSet.horizontalPull
                            )
                        )
                    }

                    PullCategory.Horizontal -> {
                        listOf(
                            ExerciseCategoryNumberOfSet(
                                ExerciseCategory.HorizontalPull,
                                numberOfSet.horizontalPull
                            ),
                            ExerciseCategoryNumberOfSet(
                                ExerciseCategory.VerticalPull,
                                numberOfSet.verticalPull
                            )
                        )
                    }
                }
            }

            override val name = "Hypertrophy"
        }

        data class Skill(override val count: Int) : WorkoutType(count = count) {
            fun buildWorkout(numberOfSet: SkillWorkoutNumberOfSet): List<ExerciseCategoryNumberOfSet> {
                return listOf(
                    ExerciseCategoryNumberOfSet(
                        ExerciseCategory.UnsupportedStatic,
                        numberOfSet.unsupportedStatic
                    ),
                    ExerciseCategoryNumberOfSet(
                        ExerciseCategory.SupportedStatic,
                        numberOfSet.supportedStatic
                    ),
                    ExerciseCategoryNumberOfSet(
                        ExerciseCategory.StraightArmDynamic,
                        numberOfSet.straightArmDynamic
                    ),
                    ExerciseCategoryNumberOfSet(
                        ExerciseCategory.BentArmDynamic,
                        numberOfSet.bentArmDynamic
                    )
                )
            }

            override val name = "Skill"
        }
    }

    data class SkillWorkoutNumberOfSet(
        val unsupportedStatic: Int,
        val supportedStatic: Int,
        val straightArmDynamic: Int,
        val bentArmDynamic: Int,
        val push: Int
    )

    data class HypertrophyWorkoutNumberOfSet(
        val verticalPull: Int,
        val horizontalPull: Int,
        val push: Int
    )

    sealed class PushCategory(val exerciseCategoryNumberOfSet: ExerciseCategoryNumberOfSet) {
        data class VerticalPush(val numberOfSet: Int) :
            PushCategory(ExerciseCategoryNumberOfSet(ExerciseCategory.VerticalPush, numberOfSet))

        data class HorizontalPush(val numberOfSet: Int) :
            PushCategory(ExerciseCategoryNumberOfSet(ExerciseCategory.HorizontalPush, numberOfSet))

        data class WeightedHorizontalPush(val numberOfSet: Int) :
            PushCategory(ExerciseCategoryNumberOfSet(ExerciseCategory.WeightedHorizontalPush, numberOfSet))
    }

    protected fun List<ExerciseCategoryNumberOfSet>.addPushCategory(pushCategory: PushCategory): List<ExerciseCategoryNumberOfSet> {
        val list = this.toMutableList()

        list.add(pushCategory.exerciseCategoryNumberOfSet)

        return list.toList()
    }

    enum class PullCategory {
        Vertical,
        Horizontal
    }

    data class VerticalPushSkill(
        val numberOfSet: SkillWorkoutNumberOfSet,
        override val count: Int
    ) :
        Workout(count = count) {
        override val type = WorkoutType.Skill(count)
        override val workouts = type.buildWorkout(numberOfSet).addPushCategory(
            PushCategory.VerticalPush(numberOfSet.push)
        )
    }

    data class HorizontalPushSkill(
        val numberOfSet: SkillWorkoutNumberOfSet,
        override val count: Int
    ) :
        Workout(count = count) {

        override val type = WorkoutType.Skill(count)
        override val workouts = type.buildWorkout(numberOfSet).addPushCategory(
            PushCategory.HorizontalPush(numberOfSet.push)
        )
    }

    data class HorizontalPushVerticalFirstHypertrophy(
        val numberOfSet: HypertrophyWorkoutNumberOfSet,
        override val count: Int
    ) :
        Workout(count = count) {

        override val type = WorkoutType.Hypertrophy(count)
        override val workouts =
            type.buildWorkout(
                numberOfSet,
                first = PullCategory.Vertical
            ).addPushCategory(
                PushCategory.HorizontalPush(numberOfSet.push)
            )

    }

    data class WeightedHorizontalPushHorizontalFirstHypertrophy(
        val numberOfSet: HypertrophyWorkoutNumberOfSet,
        override val count: Int
    ) :
        Workout(count = count) {

        override val type = WorkoutType.Hypertrophy(count)
        override val workouts =
            type.buildWorkout(
                numberOfSet,
                first = PullCategory.Horizontal
            ).addPushCategory(
                PushCategory.WeightedHorizontalPush(numberOfSet.push)
            )
    }

    data class WeightedHorizontalPushVerticalFirstHypertrophy(
        val numberOfSet: HypertrophyWorkoutNumberOfSet,
        override val count: Int
    ) :
        Workout(count = count) {

        override val type = WorkoutType.Hypertrophy(count)

        override val workouts =
            type.buildWorkout(
                numberOfSet,
                first = PullCategory.Vertical
            ).addPushCategory(
                PushCategory.WeightedHorizontalPush(numberOfSet.push)
            )
    }
}