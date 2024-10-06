package com.leeweeder.yluxthenics.feature_timer.presentation

sealed interface RecentTimerDurationsState {
    data object Loading : RecentTimerDurationsState
    data class Error(val throwable: Throwable) : RecentTimerDurationsState
    data class Success(val durations: Set<Int>) : RecentTimerDurationsState
}