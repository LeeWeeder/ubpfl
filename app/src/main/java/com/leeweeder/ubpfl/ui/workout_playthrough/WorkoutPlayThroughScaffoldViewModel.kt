package com.leeweeder.ubpfl.ui.workout_playthrough

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leeweeder.ubpfl.feature_timer.data.RecentTimerDurationsRepository
import com.leeweeder.ubpfl.feature_timer.presentation.RecentTimerDurationsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutPlayThroughScaffoldViewModel @Inject constructor(private val recentTimerDurationsRepository: RecentTimerDurationsRepository) :
    ViewModel() {
    val recentTimerDurationsState: StateFlow<RecentTimerDurationsState> =
        recentTimerDurationsRepository.recentTimerDurations.map { recentTimerDurations ->
            RecentTimerDurationsState.Success(durations = recentTimerDurations.durationsMap.entries.sortedByDescending {
                it.value
            }.map {
                it.key
            }.toSet())
        }.catch {
            RecentTimerDurationsState.Error(it)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            RecentTimerDurationsState.Loading
        )

    fun onEvent(event: WorkoutPlayThroughEvent) {
        when (event) {
            is WorkoutPlayThroughEvent.SaveTimerDuration -> {
                viewModelScope.launch {
                    recentTimerDurationsRepository.addTimerDuration(event.duration)
                }
            }
        }
    }
}

sealed class WorkoutPlayThroughEvent {
    data class SaveTimerDuration(val duration: Int) : WorkoutPlayThroughEvent()
}
