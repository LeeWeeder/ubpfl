package com.leeweeder.ubpfl.ui.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutViewModel: ViewModel() {
    private val _workoutDurationMillis = MutableStateFlow(0L)
    val workoutDurationMillis: StateFlow<Long> = _workoutDurationMillis.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                _workoutDurationMillis.value += 1000L
            }
        }
    }
}