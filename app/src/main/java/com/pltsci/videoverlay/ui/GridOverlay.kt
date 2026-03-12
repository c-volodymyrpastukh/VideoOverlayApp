package com.pltsci.videoverlay.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun GridOverlay(
    dimension: Int,
    modifier: Modifier = Modifier,
    resetTrigger: Int = 0,
    onActiveCellsChanged: ((Set<Int>) -> Unit)? = null
) {
    // Reset active cells whenever the dimension or resetTrigger changes
    var activeCells by remember(dimension, resetTrigger) { mutableStateOf(emptySet<Int>()) }

    BoxWithConstraints(modifier = modifier) {
        val cellWidth = maxWidth / dimension
        val cellHeight = maxHeight / dimension

        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                repeat(dimension) { row ->
                    Row {
                        repeat(dimension) { col ->
                            val index = row * dimension + col
                            GridCell(
                                label = (index + 1).toString(),
                                isActive = index in activeCells,
                                onClick = {
                                    activeCells = if (index in activeCells) {
                                        activeCells - index
                                    } else {
                                        activeCells + index
                                    }
                                    onActiveCellsChanged?.invoke(activeCells)
                                },
                                modifier = Modifier
                                    .width(cellWidth)
                                    .height(cellHeight)
                            )
                        }
                    }
                }
            }
        }
    }
}
