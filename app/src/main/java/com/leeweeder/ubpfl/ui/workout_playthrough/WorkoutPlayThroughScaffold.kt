package com.leeweeder.ubpfl.ui.workout_playthrough

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leeweeder.ubpfl.api_program.asset.WorkoutConstants
import com.leeweeder.ubpfl.feature_timer.presentation.RecentTimerDurationsState
import com.leeweeder.ubpfl.feature_timer.presentation.TimerScaffold
import com.leeweeder.ubpfl.feature_timer.presentation.TimerSheetState
import com.leeweeder.ubpfl.feature_timer.presentation.TimerSheetVisibilityState
import com.leeweeder.ubpfl.feature_timer.presentation.TimerUiState
import com.leeweeder.ubpfl.feature_timer.presentation.ToggleTimerVisibilityIconButton
import com.leeweeder.ubpfl.feature_timer.presentation.WorkoutDurationText
import com.leeweeder.ubpfl.feature_timer.presentation.rememberTimerScaffoldState
import com.leeweeder.ubpfl.feature_timer.presentation.rememberTimerState
import com.leeweeder.ubpfl.ui.workout.WorkoutViewModel

@Composable
fun WorkoutPlayThroughScaffold(
    viewModel: WorkoutPlayThroughScaffoldViewModel = hiltViewModel(),
    workoutViewModel: WorkoutViewModel = viewModel(),
    navHost: @Composable (PaddingValues, NestedScrollConnection) -> Unit
) {
    val recentTimerDurationsState by viewModel.recentTimerDurationsState.collectAsStateWithLifecycle()
    val workoutDurationMillis by workoutViewModel.workoutDurationMillis.collectAsStateWithLifecycle()
    WorkoutPlayThroughScaffold(
        recentTimerDurationsState = recentTimerDurationsState,
        onEvent = viewModel::onEvent,
        workoutDurationMillis = workoutDurationMillis,
        navHost = navHost
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutPlayThroughScaffold(
    recentTimerDurationsState: RecentTimerDurationsState,
    workoutDurationMillis: Long,
    onEvent: (WorkoutPlayThroughEvent) -> Unit,
    navHost: @Composable (PaddingValues, NestedScrollConnection) -> Unit
) {
    val timerScaffoldState = rememberTimerScaffoldState(
        initialTimerStateValue = TimerSheetVisibilityState.Shown(
            state = TimerSheetState.Preparation(
                TimerUiState.Started
            )
        )
    )
    val timerState = rememberTimerState(timerScaffoldState)

    LaunchedEffect(Unit) {
        timerState.setWorkDuration(WorkoutConstants.warmUp.first().volume)
        timerState.startPreparationTimer()
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    TimerScaffold(recentTimerDurationsState = recentTimerDurationsState, onTimerStart = {
        onEvent(WorkoutPlayThroughEvent.SaveTimerDuration(it))
    }, topBar = {
        TopAppBar(title = {
            WorkoutDurationText(workoutDurationMillis.toInt() / 1000)
        }, actions = {
            ToggleTimerVisibilityIconButton(timerScaffoldState, timerState)
        }, scrollBehavior = scrollBehavior
        )
    }, timerState = timerState, timerScaffoldState = timerScaffoldState) { paddingValues ->
        navHost(paddingValues, scrollBehavior.nestedScrollConnection)
    }
}
