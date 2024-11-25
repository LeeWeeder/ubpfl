package com.leeweeder.ubpfl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leeweeder.ubpfl.api_program.asset.ExerciseCategory
import com.leeweeder.ubpfl.api_program.asset.Period
import com.leeweeder.ubpfl.api_program.asset.flatten
import com.leeweeder.ubpfl.api_program.util.WorkoutWrapper
import com.leeweeder.ubpfl.assets.initialData
import com.leeweeder.ubpfl.data.DataStoreRepository
import com.leeweeder.ubpfl.feature_progression.data.ProgressionRepository
import com.leeweeder.ubpfl.feature_progression.data.source.Progression
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Error(val throwable: Throwable) : MainActivityUiState
    data class Success(
        val workout: WorkoutWrapper,
        val progressions: List<Progression>,
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
    val uiState: StateFlow<MainActivityUiState> = dataStoreRepository.programFlow
        .flatMapLatest { program ->
            try {
                val progress = program.progress
                val workouts = Period.entries[program.period].flatten()
                val workout = workouts[progress]
                fetchProgression(workout.workout.workouts.map {
                    it.exerciseCategory
                }).map {
                    (MainActivityUiState.Success(
                        workout,
                        it,
                        (progress.toFloat() + 1) / workouts.size
                    ))
                }
            } catch (e: Exception) {
                flowOf(MainActivityUiState.Error(e))
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainActivityUiState.Loading)

    private suspend fun fetchProgression(exerciseCategories: List<ExerciseCategory>): Flow<List<Progression>> {
        return dataStoreRepository.progressionFlow.map { progression ->
            exerciseCategories.map { category ->
                progressionRepository.getProgressionByLevel(
                    category,
                    when (category) {
                        ExerciseCategory.BentArmDynamic -> progression.bentArmDynamic
                        ExerciseCategory.HorizontalPull -> progression.horizontalPull
                        ExerciseCategory.HorizontalPush -> progression.horizontalPush
                        ExerciseCategory.StraightArmDynamic -> progression.straightArmDynamic
                        ExerciseCategory.SupportedStatic -> progression.supportedStatic
                        ExerciseCategory.UnsupportedStatic -> progression.unsupportedStatic
                        ExerciseCategory.VerticalPull -> progression.verticalPull
                        ExerciseCategory.VerticalPush -> progression.verticalPush
                        ExerciseCategory.WeightedHorizontalPush -> progression.weightedHorizontalPush
                    }
                )
            }
        }
    }
}

fun initializeDatabase(
    scope: CoroutineScope,
    dataStoreRepository: DataStoreRepository,
    progressionRepository: ProgressionRepository
) {
    scope.launch {
        val isInitialized = dataStoreRepository.progressionFlow.first().isInitialized()
        if (!isInitialized) {
            progressionRepository.insertAllProgressions(
                initialData.flatten()
            )
            dataStoreRepository.setIsInitialized(false)
        }
    }
}

