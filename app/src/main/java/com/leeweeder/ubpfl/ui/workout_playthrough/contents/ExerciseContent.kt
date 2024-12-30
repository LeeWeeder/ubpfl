package com.leeweeder.ubpfl.ui.workout_playthrough.contents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ExerciseContent(paddingValues: PaddingValues) {
    Column(modifier = Modifier.padding(paddingValues)) {
        Text(text = "Exercise Content")
    }
}