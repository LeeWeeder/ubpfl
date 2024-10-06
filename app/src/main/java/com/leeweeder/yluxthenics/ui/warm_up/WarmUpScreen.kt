package com.leeweeder.yluxthenics.ui.warm_up

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leeweeder.yluxthenics.api_program.asset.ExerciseType
import com.leeweeder.yluxthenics.api_program.asset.WorkoutConstants
import com.leeweeder.yluxthenics.feature_timer.presentation.RecentTimerDurationsState
import com.leeweeder.yluxthenics.feature_timer.presentation.TimerScaffold

@Composable
fun WarmUpScreen(viewModel: WarmUpViewModel = hiltViewModel()) {
    val recentTimerDurationsState by viewModel.recentTimerDurationsState.collectAsStateWithLifecycle()
    WarmUpScreen(
        uiState = viewModel.uiState.value,
        recentTimerDurationsState = recentTimerDurationsState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun WarmUpScreen(
    uiState: WarmUpState,
    recentTimerDurationsState: RecentTimerDurationsState,
    onEvent: (WarmUpEvent) -> Unit
) {
    val density = LocalDensity.current
    TimerScaffold(recentTimerDurationsState = recentTimerDurationsState, onTimerStart = {
        onEvent(WarmUpEvent.SaveTimerDuration(it))
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            var finishButtonHeight by remember {
                mutableStateOf(0.dp)
            }

            LazyColumn(contentPadding = PaddingValues(bottom = finishButtonHeight)) {
                item {
                    Text(
                        text = "Warm-up",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                items(WorkoutConstants.warmUp) { exercise ->
                    ListItem(headlineContent = { Text(text = exercise.name) }, supportingContent = {
                        Text(
                            text = "${exercise.volume} ${if (exercise.type == ExerciseType.Timed) "seconds" else "reps"}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    })
                }
            }

            Button(onClick = {
                // TODO
            },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .onGloballyPositioned { layoutCoordinates ->
                        finishButtonHeight = with(density) {
                            layoutCoordinates.size.height.toDp()
                        }
                    }) {
                Text(text = "Finish warm-up")
            }
        }
    }
}
