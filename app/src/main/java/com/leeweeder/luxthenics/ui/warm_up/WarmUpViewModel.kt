package com.leeweeder.luxthenics.ui.warm_up

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leeweeder.luxthenics.feature_timer.data.RecentTimerDurationsRepository
import com.leeweeder.luxthenics.feature_timer.presentation.RecentTimerDurationsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WarmUpState(
    val isTimerVisible: Boolean = false,
    val isTimerActive: Boolean = false
)

@HiltViewModel
class WarmUpViewModel @Inject constructor(private val recentTimerDurationsRepository: RecentTimerDurationsRepository) :
    ViewModel() {
    private val _uiState = mutableStateOf(WarmUpState())
    val uiState: State<WarmUpState> = _uiState

    val recentTimerDurationsState: StateFlow<RecentTimerDurationsState> =
        recentTimerDurationsRepository.recentTimerDurations.map { recentTimerDurations ->
            RecentTimerDurationsState.Success(durations = recentTimerDurations.durationsMap.entries.sortedByDescending {
                it.value
            }.map {
                it.key
            }.toSet())
                .apply {
                    Log.d("WarmUpViewModel", "recentTimerDurationsState: ${this.durations}")
                }
        }.catch {
            RecentTimerDurationsState.Error(it)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            RecentTimerDurationsState.Loading
        )

    fun onEvent(event: WarmUpEvent) {
        when (event) {
            is WarmUpEvent.ToggleTimerVisibility -> {
                _uiState.value = uiState.value.copy(
                    isTimerVisible = event.visible
                )
            }

            is WarmUpEvent.SaveTimerDuration -> {
                viewModelScope.launch {
                    recentTimerDurationsRepository.addTimerDuration(event.duration)
                }
            }
        }
    }
}

sealed class WarmUpEvent {
    data class ToggleTimerVisibility(val visible: Boolean) : WarmUpEvent()
    data class SaveTimerDuration(val duration: Int) : WarmUpEvent()
}
