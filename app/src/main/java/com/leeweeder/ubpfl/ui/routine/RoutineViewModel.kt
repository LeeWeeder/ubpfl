/*
 * Copyright (C) 2022 The Android Open Source Project
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

import androidx.lifecycle.ViewModel
import com.leeweeder.ubpfl.api_program.util.WorkoutWrapper
import com.leeweeder.ubpfl.data.DataStoreRepository
import com.leeweeder.ubpfl.feature_progression.data.ProgressionRepository
import com.leeweeder.ubpfl.feature_progression.data.source.Progression
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed interface RoutineUiState {
    data object Loading : RoutineUiState
    data class Error(val throwable: Throwable) : RoutineUiState
    data class Success(val workout: WorkoutWrapper, val progressions: List<Progression>, val progress: Float) :
        RoutineUiState
}

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val progressionRepository: ProgressionRepository
) : ViewModel() {

    fun onEvent(event: RoutineEvent) {
    }
}

