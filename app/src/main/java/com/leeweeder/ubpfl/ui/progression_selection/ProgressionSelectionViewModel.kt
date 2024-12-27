package com.leeweeder.ubpfl.ui.progression_selection

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.leeweeder.ubpfl.api_program.asset.ProgressiveExercise
import com.leeweeder.ubpfl.data.DataStoreRepository
import com.leeweeder.ubpfl.data.getLevelByExerciseCategory
import com.leeweeder.ubpfl.feature_progression.data.ProgressionRepository
import com.leeweeder.ubpfl.feature_progression.data.source.Progression
import com.leeweeder.ubpfl.util.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.leeweeder.ubpfl.Progression as ProgressionPreference

sealed interface ProgressionSelectionUiState {
    data object Loading : ProgressionSelectionUiState
    data class Error(val throwable: Throwable) : ProgressionSelectionUiState
    data class Success(
        val progressions: List<Progression>, val level: Int
    ) : ProgressionSelectionUiState
}

@HiltViewModel
class ProgressionSelectionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dataStoreRepository: DataStoreRepository,
    private val progressionRepository: ProgressionRepository
) : ViewModel() {
    private val _exerciseCategory =
        mutableStateOf(savedStateHandle.toRoute<Screen.ProgressionSelection>().progressiveExercise)
    val progressiveExercise: State<ProgressiveExercise> = _exerciseCategory

    private val _temporarilySelectedProgression = mutableStateOf<Progression?>(null)
    val temporarilySelectedProgression: State<Progression?> = _temporarilySelectedProgression

    val uiState: StateFlow<ProgressionSelectionUiState> = combine(
        dataStoreRepository.progressionFlow,
        progressionRepository.getProgressionsStream(_exerciseCategory.value)
    ) { progression: ProgressionPreference, progressions: List<Progression> ->
        ProgressionSelectionUiState.Success(
            progressions = progressions,
            level = progression.getLevelByExerciseCategory(_exerciseCategory.value)
        )
    }.catch {
        ProgressionSelectionUiState.Error(it)
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), ProgressionSelectionUiState.Loading
    ).also { state ->
        viewModelScope.launch {
            state.collect {
                Log.d("Init", state.value.toString())
                _temporarilySelectedProgression.value =
                    if (it is ProgressionSelectionUiState.Success) {
                        it.progressions.find { progression ->
                            progression.level == it.level
                        }
                    } else null
            }
        }
    }

    fun onEvent(event: ProgressionSelectionEvent) {
        when (event) {
            is ProgressionSelectionEvent.SetTemporarilySelectedProgression -> {
                _temporarilySelectedProgression.value = event.progression
            }

            ProgressionSelectionEvent.SetProgressionLevelWithTemporarilySelectedProgressionLevel -> {
                viewModelScope.launch {
                    temporarilySelectedProgression.value?.let {
                        dataStoreRepository.setProgressionLevel(
                            progressiveExercise.value,
                            level = it.level
                        )
                    }
                }
            }

            is ProgressionSelectionEvent.AddEditProgression -> {
                viewModelScope.launch {
                    val progression = event.progression
                    if (progression.id == null) {
                        // Add
                    } else {
                        progressionRepository.updateName(progression.id, progression.name)
                    }
                }
            }
        }
    }
}

sealed class ProgressionSelectionEvent {
    data class SetTemporarilySelectedProgression(val progression: Progression) :
        ProgressionSelectionEvent()

    data object SetProgressionLevelWithTemporarilySelectedProgressionLevel :
        ProgressionSelectionEvent()

    data class AddEditProgression(val progression: com.leeweeder.ubpfl.ui.progression_selection.AddEditProgression) :
        ProgressionSelectionEvent()
}