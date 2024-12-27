/* * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leeweeder.ubpfl.ui.routine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.leeweeder.ubpfl.R
import com.leeweeder.ubpfl.ui.LocalNavController
import com.leeweeder.ubpfl.ui.MainActivityUiState
import com.leeweeder.ubpfl.ui.routine.components.ExercisePreviewCard
import com.leeweeder.ubpfl.util.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineScreen(
    uiState: MainActivityUiState.Success,
    paddingValues: PaddingValues
) {
    val workout = uiState.workout
    val progression = uiState.progressions
    val progress = uiState.progress

    var fabHeight by remember { mutableStateOf(0.dp) }

    Scaffold(
        modifier = Modifier
            .padding(paddingValues)
            .consumeWindowInsets(paddingValues),
        topBar = {
            Column(
                modifier = Modifier
                    .height(160.dp)
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    modifier = Modifier.padding(start = 8.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Text(
                        "${workout.macrocycle.statusName} Macrocycle",
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Surface(
                    modifier = Modifier.padding(start = 8.dp),
                    shape = MaterialTheme.shapes.large,
                    color = workout.mesocycle.color.colorContainer,
                    contentColor = workout.mesocycle.color.onColorContainer
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "Mesocycle ${workout.mesocycleNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                        )
                        Surface(
                            shape = CircleShape,
                            color = workout.mesocycle.color.color.copy(alpha = 0.5f),
                            contentColor = workout.mesocycle.color.onColorContainer
                        ) {
                            Row {
                                Text(
                                    "${workout.mesocycle.name} Cycle",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                                )
                                Surface(
                                    shape = MaterialTheme.shapes.large,
                                    color = workout.mesocycle.color.color,
                                    contentColor = workout.mesocycle.color.onColor
                                ) {
                                    Text(
                                        "Week ${workout.weekNumber}",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(
                                            vertical = 8.dp,
                                            horizontal = 16.dp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        workout.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Workout ${workout.workoutNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }, floatingActionButton = {
            val navController = LocalNavController.current
            val density = LocalDensity.current
            ExtendedFloatingActionButton(text = {
                Text("Start routine")
            }, icon = {
                Icon(
                    painter = painterResource(R.drawable.exercise_24px),
                    contentDescription = null
                )
            }, onClick = {
                navController.navigate<Screen>(Screen.WarmUp)
            }, modifier = Modifier.onGloballyPositioned {
                fabHeight = with(density) {
                    it.size.height.toDp()
                }
            })
        }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LinearProgressIndicator(
                progress = {
                    progress
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(top = 16.dp, bottom = fabHeight + 32.dp),
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(workout.workout.workouts) { index, exerciseCategoryNumberOfSet ->
                    ExercisePreviewCard(
                        exercise = exerciseCategoryNumberOfSet.progressiveExercise,
                        numberOfSets = exerciseCategoryNumberOfSet.numberOfSet,
                        progressions = progression[exerciseCategoryNumberOfSet.progressiveExercise]?.second!!,
                        currentProgressionLevel = progression[exerciseCategoryNumberOfSet.progressiveExercise]?.first!!
                    )
                }
            }
        }
    }
}