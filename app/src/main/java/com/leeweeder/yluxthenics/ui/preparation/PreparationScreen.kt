package com.leeweeder.yluxthenics.ui.preparation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PreparationScreen(
    viewModel: PreparationViewModel = hiltViewModel(),
    onFinishCountDown: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val remainingSeconds by viewModel.remainingSeconds
    PreparationScreen(
        remainingSeconds = remainingSeconds,
        onFinishCountDown = onFinishCountDown,
        onNavigateBack = onNavigateBack,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun PreparationScreen(
    remainingSeconds: Int,
    onFinishCountDown: () -> Unit,
    onNavigateBack: () -> Unit,
    onEvent: (PreparationEvent) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        onEvent(PreparationEvent.SetMediaPlayerCompletionListener { isTimerFinished ->
            if (isTimerFinished) {
                onFinishCountDown()
            }
        })
        onEvent(PreparationEvent.StartTimer)
    }

    BackHandler {
        onEvent(PreparationEvent.CancelTimer)
        onNavigateBack()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (remainingSeconds != 0) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Get ready", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = remainingSeconds.toString(),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 200.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Text(
                    text = "Start!",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}