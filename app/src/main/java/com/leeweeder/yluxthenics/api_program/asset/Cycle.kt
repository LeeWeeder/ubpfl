package com.leeweeder.yluxthenics.api_program.asset

import androidx.annotation.IntRange
import com.leeweeder.yluxthenics.ui.theme.ColorFamily
import com.leeweeder.yluxthenics.ui.theme.extended

typealias WeekNumber = Int

private const val PREPARATION_WEEK_NUMBER = 1

private const val WEEKLY_NUMBER_OF_WORKOUTS = 4

sealed class Cycle {
    abstract val numberOfWeeks: Int
    abstract val plan: List<List<Workout>>
    abstract val name: String
    abstract val color: ColorFamily

    protected sealed class WorkoutWeek {
        data object Preparation : WorkoutWeek()
        data class Regular(val weekNumber: Int) : WorkoutWeek()
        data object Deload : WorkoutWeek()
    }

    protected fun buildPlan(
        builder: (WorkoutWeek) -> List<Workout>
    ) = (PREPARATION_WEEK_NUMBER..numberOfWeeks).map { weekNumber ->
        when (weekNumber) {
            PREPARATION_WEEK_NUMBER -> {
                builder(WorkoutWeek.Preparation)
            }

            in (PREPARATION_WEEK_NUMBER + 1 until numberOfWeeks) -> {
                builder(WorkoutWeek.Regular(weekNumber))
            }

            else -> {
                builder(WorkoutWeek.Deload)
            }
        }
    }

    protected sealed class WorkoutNumber(
        @IntRange(
            from = 1,
            to = WEEKLY_NUMBER_OF_WORKOUTS.toLong()
        ) val value: Int
    ) {
        data object Workout1 : WorkoutNumber(1)
        data object Workout2 : WorkoutNumber(2)
        data object Workout3 : WorkoutNumber(3)
        data object Workout4 : WorkoutNumber(4)
    }

    protected fun buildWeekWorkout(
        builder: (workoutNumber: WorkoutNumber) -> Workout
    ) = (1..WEEKLY_NUMBER_OF_WORKOUTS).map { workoutNumber ->
        when (workoutNumber) {
            1 -> builder(WorkoutNumber.Workout1)

            2 -> builder(WorkoutNumber.Workout2)

            3 -> builder(WorkoutNumber.Workout3)

            else -> builder(WorkoutNumber.Workout4)
        }
    }

    data object Hypertrophy : Cycle() {

        /**
         * Build a hypertrophy workout.
         *
         * @return a map of workout and its order
         *
         * @param skill1 The [Workout.SkillWorkoutNumberOfSet] for skill1/workout1
         * @param hypertrophy The [Workout.HypertrophyWorkoutNumberOfSet] for hypertrophy1/workout2 and hypertrophy2/workout4 reversed
         * @param skill2 The [Workout.SkillWorkoutNumberOfSet] for skill2/workout3
         * */
        private fun buildHypertrophyWeekWorkout(
            skill1: Workout.SkillWorkoutNumberOfSet,
            hypertrophy: Triple<Int, Int, Int>,
            skill2: Workout.SkillWorkoutNumberOfSet
        ): List<Workout> {
            return buildWeekWorkout { workoutNumber ->
                when (workoutNumber) {
                    WorkoutNumber.Workout1 -> {
                        Workout.VerticalPushSkill(
                            numberOfSet = skill1,
                            count = 1
                        )
                    }

                    WorkoutNumber.Workout2 -> {
                        Workout.HorizontalPushVerticalFirstHypertrophy(
                            numberOfSet = Workout.HypertrophyWorkoutNumberOfSet(
                                verticalPull = hypertrophy.first,
                                horizontalPull = hypertrophy.second,
                                push = hypertrophy.third
                            ),
                            count = 1
                        )
                    }

                    WorkoutNumber.Workout3 -> {
                        Workout.VerticalPushSkill(
                            numberOfSet = skill2,
                            count = 2
                        )
                    }

                    WorkoutNumber.Workout4 -> {
                        Workout.WeightedHorizontalPushHorizontalFirstHypertrophy(
                            numberOfSet = Workout.HypertrophyWorkoutNumberOfSet(
                                verticalPull = hypertrophy.second,
                                horizontalPull = hypertrophy.first,
                                push = hypertrophy.third
                            ),
                            count = 2
                        )
                    }
                }
            }
        }

        override val numberOfWeeks = 6

        override val plan: List<List<Workout>> = buildPlan { workoutWeek ->
            val skill2 = Workout.SkillWorkoutNumberOfSet(
                unsupportedStatic = 1,
                supportedStatic = 1,
                straightArmDynamic = 2,
                bentArmDynamic = 2,
                push = 3
            )

            when (workoutWeek) {
                WorkoutWeek.Preparation -> {
                    buildHypertrophyWeekWorkout(
                        skill1 = Workout.SkillWorkoutNumberOfSet(
                            unsupportedStatic = 2,
                            supportedStatic = 2,
                            straightArmDynamic = 1,
                            bentArmDynamic = 1,
                            push = 2
                        ),
                        hypertrophy = Triple(2, 2, 2),
                        skill2 = skill2.copy(push = 2),
                    )
                }

                is WorkoutWeek.Regular -> {
                    /**
                     * Build regular weekly workouts
                     *
                     * @param first Number of set for the first exercise
                     * @param second Number of set for the second exercise
                     * */
                    fun buildRegularWeekWorkout(first: Int, second: Int): List<Workout> {
                        val skill1 = Workout.SkillWorkoutNumberOfSet(
                            unsupportedStatic = 2,
                            supportedStatic = 2,
                            straightArmDynamic = 2,
                            bentArmDynamic = 2,
                            push = 3
                        )

                        return buildHypertrophyWeekWorkout(
                            skill1 = skill1,
                            hypertrophy = Triple(first, second, 3),
                            skill2 = skill2
                        )
                    }



                    when (workoutWeek.weekNumber) {
                        2 -> {
                            buildRegularWeekWorkout(3, 2)
                        }

                        3 -> {
                            buildRegularWeekWorkout(3, 3)
                        }

                        4 -> {
                            buildRegularWeekWorkout(4, 3)
                        }

                        5 -> {
                            buildRegularWeekWorkout(4, 4)
                        }

                        else -> {
                            throw IllegalArgumentException("Invalid week number ${workoutWeek.weekNumber}")
                        }
                    }
                }

                WorkoutWeek.Deload -> {
                    buildHypertrophyWeekWorkout(
                        skill1 = Workout.SkillWorkoutNumberOfSet(
                            unsupportedStatic = 1,
                            supportedStatic = 1,
                            straightArmDynamic = 1,
                            bentArmDynamic = 1,
                            push = 1
                        ),
                        hypertrophy = Triple(1, 1, 1),
                        skill2 = Workout.SkillWorkoutNumberOfSet(
                            unsupportedStatic = 1,
                            supportedStatic = 1,
                            straightArmDynamic = 1,
                            bentArmDynamic = 1,
                            push = 1
                        )
                    )
                }
            }
        }
        override val name = "Hypertrophy"
        override val color = extended.hypertrophy
    }

    data object Skill : Cycle() {
        private fun buildSkillWorkout(
            skill1: Workout.SkillWorkoutNumberOfSet,
            skill2: Workout.SkillWorkoutNumberOfSet,
            skill3: Workout.SkillWorkoutNumberOfSet,
            hypertrophy: Workout.HypertrophyWorkoutNumberOfSet
        ): List<Workout> {
            return buildWeekWorkout { workoutNumber ->
                when (workoutNumber) {
                    WorkoutNumber.Workout1 -> {
                        Workout.VerticalPushSkill(
                            numberOfSet = skill1,
                            count = 1
                        )
                    }

                    WorkoutNumber.Workout2 -> {
                        Workout.HorizontalPushSkill(
                            numberOfSet = skill2,
                            count = 2
                        )
                    }

                    WorkoutNumber.Workout3 -> {
                        Workout.VerticalPushSkill(
                            numberOfSet = skill3,
                            count = 3
                        )
                    }

                    WorkoutNumber.Workout4 -> {
                        Workout.WeightedHorizontalPushVerticalFirstHypertrophy(
                            numberOfSet = hypertrophy,
                            count = 1
                        )
                    }
                }
            }
        }

        override val numberOfWeeks = 5

        override val plan = buildPlan { workoutWeek ->
            val hypertrophy =
                Workout.HypertrophyWorkoutNumberOfSet(
                    verticalPull = 2,
                    horizontalPull = 2,
                    push = 3
                )

            fun buildRegularWeekWorkout(
                focusNumberOfSet: Int,
                regularNumberOfSet: Int
            ): List<Workout> {
                return buildSkillWorkout(
                    skill1 = Workout.SkillWorkoutNumberOfSet(
                        unsupportedStatic = focusNumberOfSet,
                        supportedStatic = regularNumberOfSet,
                        straightArmDynamic = regularNumberOfSet,
                        bentArmDynamic = regularNumberOfSet,
                        push = 3
                    ),
                    skill2 = Workout.SkillWorkoutNumberOfSet(
                        unsupportedStatic = regularNumberOfSet,
                        supportedStatic = regularNumberOfSet,
                        straightArmDynamic = focusNumberOfSet,
                        bentArmDynamic = regularNumberOfSet,
                        push = 3
                    ),
                    skill3 = Workout.SkillWorkoutNumberOfSet(
                        unsupportedStatic = regularNumberOfSet,
                        supportedStatic = focusNumberOfSet,
                        straightArmDynamic = regularNumberOfSet,
                        bentArmDynamic = regularNumberOfSet,
                        push = 3
                    ),
                    hypertrophy = hypertrophy
                )
            }

            when (workoutWeek) {
                WorkoutWeek.Preparation -> {
                    buildSkillWorkout(
                        skill1 = Workout.SkillWorkoutNumberOfSet(
                            unsupportedStatic = 2,
                            supportedStatic = 2,
                            straightArmDynamic = 1,
                            bentArmDynamic = 1,
                            push = 2
                        ),
                        skill2 = Workout.SkillWorkoutNumberOfSet(
                            unsupportedStatic = 1,
                            supportedStatic = 1,
                            straightArmDynamic = 2,
                            bentArmDynamic = 1,
                            push = 2
                        ),
                        skill3 = Workout.SkillWorkoutNumberOfSet(
                            unsupportedStatic = 2,
                            supportedStatic = 2,
                            straightArmDynamic = 1,
                            bentArmDynamic = 1,
                            push = 2
                        ),
                        hypertrophy = hypertrophy.copy(push = 2)
                    )
                }

                is WorkoutWeek.Regular -> {
                    when (workoutWeek.weekNumber) {
                        2 -> {
                            buildRegularWeekWorkout(3, 2)
                        }

                        in (2 + 1) until numberOfWeeks -> {
                            buildRegularWeekWorkout(4, 2)
                        }

                        else -> {
                            throw IllegalArgumentException("Invalid week number ${workoutWeek.weekNumber}")
                        }
                    }
                }

                WorkoutWeek.Deload -> {
                    val skill = Workout.SkillWorkoutNumberOfSet(
                        unsupportedStatic = 1,
                        supportedStatic = 1,
                        straightArmDynamic = 1,
                        bentArmDynamic = 1,
                        push = 1
                    )

                    buildSkillWorkout(
                        skill1 = skill,
                        skill2 = skill,
                        skill3 = skill,
                        hypertrophy = Workout.HypertrophyWorkoutNumberOfSet(1, 1, push = 1)
                    )
                }
            }
        }
        override val name = "Skill"
        override val color = extended.skill
    }
}
