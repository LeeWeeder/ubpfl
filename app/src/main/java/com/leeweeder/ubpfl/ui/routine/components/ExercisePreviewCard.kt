package com.leeweeder.ubpfl.ui.routine.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.leeweeder.ubpfl.api_program.asset.ProgressiveExercise
import com.leeweeder.ubpfl.api_program.asset.formatName
import com.leeweeder.ubpfl.api_program.asset.formatVolumeRange
import com.leeweeder.ubpfl.feature_progression.data.source.Progression
import com.leeweeder.ubpfl.ui.LocalNavController
import com.leeweeder.ubpfl.util.Screen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ExercisePreviewCard(
    exercise: ProgressiveExercise,
    numberOfSets: Int,
    progressions: List<Progression>,
    currentProgressionLevel: Int
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(top = 12.dp, start = 16.dp, end = 12.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        exercise.formatName(),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "$numberOfSets set${if (numberOfSets > 1) "s" else ""} of ${exercise.formatVolumeRange()}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            val navController = LocalNavController.current
            ProgressionItem(
                progressions.size,
                currentProgressionLevel,
                progressions.find { it.level == currentProgressionLevel }?.name!!,
                modifier = Modifier.clickable {
                    navController.navigate(Screen.ProgressionSelection(exercise))
                }
            )
        }
    }
}