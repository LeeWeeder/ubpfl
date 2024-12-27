package com.leeweeder.ubpfl.ui.routine.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.leeweeder.ubpfl.R

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ProgressionItem(
    totalNumberOfProgressions: Int,
    level: Int,
    name: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .padding(horizontal = 16.dp)
            .clip(CircleShape)
            .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
            .then(modifier)
            .padding(4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val containerColor = MaterialTheme.colorScheme.surfaceDim
        val primaryColor = MaterialTheme.colorScheme.primary
        Box(
            modifier = Modifier
                .size(24.dp)
                .drawBehind {
                    drawCircle(
                        color = containerColor, style = Stroke(
                            this.size.height * 0.14f
                        )
                    )
                    drawArc(
                        color = primaryColor,
                        startAngle = 270f,
                        sweepAngle = -(360f / totalNumberOfProgressions * level),
                        useCenter = false,
                        style = Stroke(
                            this.size.height * 0.15f,
                            cap = StrokeCap.Round
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                level.toString(),
                style = if (level < 100) MaterialTheme.typography.titleMedium else MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            name,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 3.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                painter = painterResource(R.drawable.chevron_forward_24px),
                contentDescription = null,
                Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}