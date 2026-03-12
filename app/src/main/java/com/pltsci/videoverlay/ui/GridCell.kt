package com.pltsci.videoverlay.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import com.pltsci.videoverlay.theme.CellActiveBackground
import com.pltsci.videoverlay.theme.CellActiveBorder
import com.pltsci.videoverlay.theme.CellDefaultBackground
import com.pltsci.videoverlay.theme.CellDefaultBorder
import com.pltsci.videoverlay.theme.CellLabelColor

@Composable
fun GridCell(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isActive) CellActiveBackground else CellDefaultBackground,
        animationSpec = tween(durationMillis = 200),
        label = "cellBackground"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isActive) CellActiveBorder else CellDefaultBorder,
        animationSpec = tween(durationMillis = 200),
        label = "cellBorder"
    )

    Box(
        modifier = modifier
            .border(1.dp, borderColor)
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = CellLabelColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
