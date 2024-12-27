package com.leeweeder.ubpfl.ui.warm_up

import LargeTopAppBar
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.leeweeder.ubpfl.R
import com.leeweeder.ubpfl.api_program.asset.ExerciseType
import com.leeweeder.ubpfl.api_program.asset.WorkoutConstants
import com.leeweeder.ubpfl.feature_timer.presentation.RecentTimerDurationsState
import com.leeweeder.ubpfl.feature_timer.presentation.TimerScaffold
import com.leeweeder.ubpfl.feature_timer.presentation.TimerSheetState
import com.leeweeder.ubpfl.feature_timer.presentation.TimerSheetVisibilityState
import com.leeweeder.ubpfl.feature_timer.presentation.ToggleTimerVisibilityIconButton
import com.leeweeder.ubpfl.feature_timer.presentation.WorkoutDurationText
import com.leeweeder.ubpfl.feature_timer.presentation.rememberTimerScaffoldState
import com.leeweeder.ubpfl.feature_timer.presentation.rememberTimerState
import com.leeweeder.ubpfl.ui.workout.WorkoutViewModel

@Composable
fun WarmUpScreen(
    viewModel: WarmUpViewModel = hiltViewModel(),
    workoutViewModel: WorkoutViewModel = viewModel()
) {
    val recentTimerDurationsState by viewModel.recentTimerDurationsState.collectAsStateWithLifecycle()
    val workoutDurationMillis by workoutViewModel.workoutDurationMillis.collectAsStateWithLifecycle()
    WarmUpScreen(
        uiState = viewModel.uiState.value,
        recentTimerDurationsState = recentTimerDurationsState,
        onEvent = viewModel::onEvent,
        workoutDurationMillis = workoutDurationMillis
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarmUpScreen(
    uiState: WarmUpState,
    recentTimerDurationsState: RecentTimerDurationsState,
    workoutDurationMillis: Long,
    onEvent: (WarmUpEvent) -> Unit
) {
    val timerScaffoldState = rememberTimerScaffoldState(initialTimerStateValue = TimerSheetVisibilityState.Shown(state = TimerSheetState.Preparation))
    val timerState = rememberTimerState(timerScaffoldState)

    LaunchedEffect(Unit) {
        timerState.setDuration(WorkoutConstants.warmUp.first().volume)
        timerState.startPreparationTimer()
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    TimerScaffold(recentTimerDurationsState = recentTimerDurationsState, onTimerStart = {
        onEvent(WarmUpEvent.SaveTimerDuration(it))
    }, topBar = {
        LargeTopAppBar(
            title = {
                Text(text = "Warm-up")
            },
            navigationIcon = {
                WorkoutDurationText(workoutDurationMillis.toInt() / 1000)
            },
            actions = {
                ToggleTimerVisibilityIconButton(timerScaffoldState, timerState)
            },
            scrollBehavior = scrollBehavior
        )
    }, timerState = timerState, timerScaffoldState = timerScaffoldState) { paddingValues ->
        var fabHeight by remember { mutableStateOf(0.dp) }
        val density = LocalDensity.current
        Scaffold(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        // TODO: Implement main workout section screen
                    },
                    text = {
                        Text("Finish warm-up")
                    },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.done_24px),
                            contentDescription = null
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.onGloballyPositioned {
                        fabHeight = with(density) {
                            it.size.height.toDp()
                        }
                    })
            }
        ) {
            LazyColumn(contentPadding = PaddingValues(bottom = fabHeight + 16.dp)) {
                items(WorkoutConstants.warmUp) { exercise ->
                    ListItem(headlineContent = { Text(text = exercise.name) }, supportingContent = {
                        Text(
                            text = "${exercise.volume} ${if (exercise.type == ExerciseType.Timed) "seconds" else "reps"}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    })
                }
            }
        }
    }
}
