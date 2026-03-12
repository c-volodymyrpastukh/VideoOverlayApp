package com.pltsci.videoverlay.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pltsci.videoverlay.data.SettingsRepository
import com.pltsci.videoverlay.model.GridSettings
import com.pltsci.videoverlay.theme.SettingsBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainScreen(repository: SettingsRepository) {
    val settings by repository.settings.collectAsState(initial = GridSettings())
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Scenario execution state
    var currentLinkIndex by remember { mutableIntStateOf(0) }
    var navigationPending by remember { mutableStateOf(false) }
    var resetTrigger by remember { mutableIntStateOf(0) }

    // Reset scenario state when scenario changes or is deactivated
    LaunchedEffect(settings.scenarioActive, settings.scenario) {
        currentLinkIndex = 0
        navigationPending = false
        resetTrigger++
    }

    val scenarioActive = settings.scenarioActive && settings.scenario != null
    val currentLink = if (scenarioActive) {
        settings.scenario?.links?.getOrNull(currentLinkIndex)
    } else null

    val displayImageUrl = if (scenarioActive && currentLink != null) {
        currentLink.imageUrl
    } else {
        settings.imageUrl
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddings ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddings)) {

            // Layer 0: reference image behind the grid
            if (displayImageUrl.isNotBlank()) {
                AsyncImage(
                    model = displayImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Layer 1: transparent grid overlay
            GridOverlay(
                dimension = settings.dimension,
                modifier = Modifier.fillMaxSize(),
                resetTrigger = resetTrigger,
                onActiveCellsChanged = { activeCells ->
                    if (!scenarioActive || navigationPending || currentLink == null) return@GridOverlay
                    val matchedRoute = currentLink.routes.firstOrNull { route ->
                        route.cellIndices == activeCells
                    }
                    if (matchedRoute != null) {
                        navigationPending = true
                        scope.launch {
                            delay(2000)
                            currentLinkIndex = matchedRoute.targetLinkIndex
                            navigationPending = false
                            resetTrigger++
                        }
                    }
                }
            )

            // Layer 2: settings button — top-right, always on top
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 64.dp, end = 16.dp)
                    .background(SettingsBackground, shape = RoundedCornerShape(6.dp))
                    .clickable { showDialog = true }
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Settings",
                    color = Color.White,
                    fontSize = 28.sp
                )
            }
        }


        if (showDialog) {
            SettingsDialog(
                currentSettings = settings,
                onDismiss = { showDialog = false },
                onConfirm = { newSettings ->
                    scope.launch { repository.save(newSettings) }
                    showDialog = false
                }
            )
        }
    }
}
