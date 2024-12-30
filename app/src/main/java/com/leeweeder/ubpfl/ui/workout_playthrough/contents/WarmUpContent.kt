package com.leeweeder.ubpfl.ui.workout_playthrough.contents

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.leeweeder.ubpfl.R
import com.leeweeder.ubpfl.api_program.asset.ExerciseType
import com.leeweeder.ubpfl.api_program.asset.WorkoutConstants

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarmUpContent(
    paddingValues: PaddingValues,
    nestedScrollConnection: NestedScrollConnection,
    onNavigateToSkillSection: () -> Unit
) {
    var fabHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    Scaffold(
        modifier = Modifier
            .padding(paddingValues)
            .consumeWindowInsets(paddingValues)
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToSkillSection,
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
