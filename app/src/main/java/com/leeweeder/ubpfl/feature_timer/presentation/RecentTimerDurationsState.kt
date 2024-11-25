package com.leeweeder.ubpfl.feature_timer.presentation

sealed interface RecentTimerDurationsState {
    data object Loading : RecentTimerDurationsState
    data class Error(val throwable: Throwable) : RecentTimerDurationsState
    data class Success(val durations: Set<Int>) : RecentTimerDurationsState {
        override fun equals(other: Any?): Boolean {
            if (other is Success) {
                return durations.toList() == other.durations.toList()
            }

            return false
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + durations.hashCode()
            return result
        }
    }
}