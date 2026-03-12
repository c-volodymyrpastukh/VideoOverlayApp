package com.pltsci.videoverlay.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pltsci.videoverlay.model.GridSettings

@Composable
fun SettingsDialog(
    currentSettings: GridSettings,
    onDismiss: () -> Unit,
    onConfirm: (GridSettings) -> Unit
) {
    var dimensionText by remember { mutableStateOf(currentSettings.dimension.toString()) }
    var imageUrl by remember { mutableStateOf("") }
    var dimensionError by remember { mutableStateOf(false) }
    var showScenarioEditor by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configure Grid", fontSize = 33.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = dimensionText,
                    onValueChange = {
                        dimensionText = it
                        dimensionError = false
                    },
                    textStyle = TextStyle(fontSize = 24.sp),
                    label = { Text("Grid dimension (1\u201312)", fontSize = 27.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = dimensionError,
                    supportingText = if (dimensionError) {
                        { Text("Enter a number between 1 and 12", fontSize = 21.sp) }
                    } else null,
                    singleLine = true
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        textStyle = TextStyle(fontSize = 24.sp),
                        label = { Text("Image URL (optional)", fontSize = 27.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        clipboardManager.getText()?.text?.let { imageUrl = it }
                    }) {
                        Text("Paste", fontSize = 24.sp)
                    }
                }
                TextButton(onClick = { showScenarioEditor = true }) {
                    Text("Configure Scenario", fontSize = 27.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val dim = dimensionText.toIntOrNull()
                if (dim == null || dim !in 1..12) {
                    dimensionError = true
                } else {
                    onConfirm(GridSettings(
                        dimension = dim,
                        imageUrl = imageUrl.trim(),
                        scenario = currentSettings.scenario,
                        scenarioActive = currentSettings.scenarioActive
                    ))
                }
            }) {
                Text("OK", fontSize = 24.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontSize = 24.sp)
            }
        }
    )

    if (showScenarioEditor) {
        ScenarioEditor(
            scenario = currentSettings.scenario,
            scenarioActive = currentSettings.scenarioActive,
            dimension = dimensionText.toIntOrNull() ?: currentSettings.dimension,
            onDismiss = { showScenarioEditor = false },
            onSave = { scenario, active ->
                showScenarioEditor = false
                onConfirm(GridSettings(
                    dimension = dimensionText.toIntOrNull() ?: currentSettings.dimension,
                    imageUrl = imageUrl.trim(),
                    scenario = scenario,
                    scenarioActive = active
                ))
            }
        )
    }
}
