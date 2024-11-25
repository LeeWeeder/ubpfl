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

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leeweeder.ubpfl.R
import com.leeweeder.ubpfl.api_program.asset.ExerciseCategory
import com.leeweeder.ubpfl.api_program.asset.formatName
import com.leeweeder.ubpfl.ui.MainActivityUiState

@Composable
fun RoutineScreen(
    uiState: MainActivityUiState.Success,
    viewModel: RoutineViewModel = hiltViewModel(),
    paddingValues: PaddingValues,
    onNavigateToProgressionSelectionScreen: (ExerciseCategory) -> Unit
) {
    RoutineScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        paddingValues = paddingValues,
        onNavigateToProgressionSelectionScreen = onNavigateToProgressionSelectionScreen
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoutineScreen(
    uiState: MainActivityUiState.Success,
    onEvent: (RoutineEvent) -> Unit,
    onNavigateToProgressionSelectionScreen: (ExerciseCategory) -> Unit,
    paddingValues: PaddingValues
) {
    val workout = uiState.workout
    val progression = uiState.progressions
    val progress = uiState.progress

    Scaffold(
        modifier = Modifier
            .padding(paddingValues)
            .consumeWindowInsets(paddingValues),
        topBar = {
            LargeTopAppBar(title = {
                Column {
                    Text(
                        text = workout.name,
                        style = MaterialTheme.typography.displayMedium
                    )
                }
            }, navigationIcon = {
                val height = 8.dp
                Row(
                    modifier = Modifier
                        .padding(bottom = 8.dp, top = 32.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = workout.cycle.color.colorContainer,
                                shape = CircleShape
                            )
                            .size(48.dp, height)
                    )
                    Box(
                        modifier = Modifier
                            .background(
                                color = workout.cycle.color.color,
                                shape = CircleShape
                            )
                            .size(height, height)
                    )
                }
            })
        }) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp)
                ) {
                    val textStyle = MaterialTheme.typography.labelLarge
                    Text(
                        text = "Workout ${workout.workoutOrder}",
                        style = textStyle
                    )
                    Text(text = "●", style = textStyle)
                    val periodWeekNumber =
                        (workout.cycleOrder * workout.cycle.numberOfWeeks) + workout.weekNumber

                    Text(text = "Period Week $periodWeekNumber", style = textStyle)
                    Text(text = "●", style = textStyle)
                    Text(
                        text = "Cycle Week ${workout.weekNumber}",
                        style = textStyle
                    )
                }
            }
            item {
                LinearProgressIndicator(
                    progress = {
                        Log.d("Progress", progress.toString())
                        progress
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }
            itemsIndexed(workout.workout.workouts) { index, exerciseCategoryNumberOfSet ->
                ListItem(
                    headlineContent = {
                        val currentProgression = progression[index]
                        Text(
                            text = currentProgression.level.toString() + ". " + currentProgression.name,
                            maxLines = 2,
                            style = MaterialTheme.typography.titleMedium,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    overlineContent = {
                        Text(
                            text = exerciseCategoryNumberOfSet.exerciseCategory.formatName(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    trailingContent = {
                        Icon(
                            painter = painterResource(id = R.drawable.chevron_forward_24px),
                            contentDescription = "Change progression"
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier.clickable {
                        onNavigateToProgressionSelectionScreen(exerciseCategoryNumberOfSet.exerciseCategory)
                    }
                )
            }
        }
    }
}