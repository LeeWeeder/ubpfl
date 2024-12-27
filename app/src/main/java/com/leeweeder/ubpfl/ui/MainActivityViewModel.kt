package com.leeweeder.ubpfl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leeweeder.ubpfl.api_program.asset.Macrocycle
import com.leeweeder.ubpfl.api_program.asset.ProgressiveExercise
import com.leeweeder.ubpfl.api_program.asset.flatten
import com.leeweeder.ubpfl.api_program.util.WorkoutWrapper
import com.leeweeder.ubpfl.assets.initialData
import com.leeweeder.ubpfl.data.DataStoreRepository
import com.leeweeder.ubpfl.feature_progression.data.ProgressionRepository
import com.leeweeder.ubpfl.feature_progression.data.source.Progression
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias Level = Int

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Error(val throwable: Throwable) : MainActivityUiState
    data class Success(
        val workout: WorkoutWrapper,
        val progressions: Map<ProgressiveExercise, Pair<Level, List<Progression>>>,
        val progress: Float
    ) :
        MainActivityUiState
}

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val progressionRepository: ProgressionRepository
) : ViewModel() {

    init {
        initializeDatabase(viewModelScope, dataStoreRepository, progressionRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<MainActivityUiState> = dataStoreRepository.macrocyleFlow
        .flatMapLatest { macrocycle ->
            try {
                val progress = macrocycle.progress
                val workouts = Macrocycle.entries[macrocycle.period].flatten()
                val workout = workouts[progress]

                val progressions =
                    mutableMapOf<ProgressiveExercise, Pair<Level, List<Progression>>>()

                workout.workout.workouts.map { exerciseNumberOfSetWrapper ->
                    exerciseNumberOfSetWrapper.progressiveExercise
                }.map { exercise ->
                    progressions.put(
                        exercise,
                        Pair(
                            getExerciseCurrentProgressionLevel(exercise),
                            getProgressionsOfExercise(exercise)
                        )
                    )
                }

                flowOf(
                    MainActivityUiState.Success(
                        workout,
                        progressions,
                        progress.toFloat() / workouts.size
                    )
                )
            } catch (e: Exception) {
                flowOf(MainActivityUiState.Error(e))
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainActivityUiState.Loading)

    private suspend fun getProgressionsOfExercise(exercise: ProgressiveExercise): List<Progression> {
        return dataStoreRepository.progressionFlow.map { progression ->
            progressionRepository.getProgressions(exercise)
        }.first()
    }

    private suspend fun getExerciseCurrentProgressionLevel(exercise: ProgressiveExercise): Level {
        return dataStoreRepository.progressionFlow.map { progression ->
            when (exercise) {
                ProgressiveExercise.BentArmDynamic -> progression.bentArmDynamic
                ProgressiveExercise.HorizontalPull -> progression.horizontalPull
                ProgressiveExercise.HorizontalPush -> progression.horizontalPush
                ProgressiveExercise.StraightArmDynamic -> progression.straightArmDynamic
                ProgressiveExercise.SupportedStatic -> progression.supportedStatic
                ProgressiveExercise.UnsupportedStatic -> progression.unsupportedStatic
                ProgressiveExercise.VerticalPull -> progression.verticalPull
                ProgressiveExercise.VerticalPush -> progression.verticalPush
                ProgressiveExercise.WeightedHorizontalPush -> progression.weightedHorizontalPush
            }
        }.first()
    }
}

fun initializeDatabase(
    scope: CoroutineScope,
    dataStoreRepository: DataStoreRepository,
    progressionRepository: ProgressionRepository
) {
    scope.launch {
        val isInitialized = dataStoreRepository.progressionFlow.first().initialized
        if (!isInitialized) {
            progressionRepository.insertAllProgressions(
                initialData.flatten()
            )
            dataStoreRepository.setInitialized(true)
        }
    }
}

