package com.pltsci.videoverlay.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pltsci.videoverlay.model.GridSettings

@Composable
fun SettingsDialog(
    currentSettings: GridSettings,
    onDismiss: () -> Unit,
    onConfirm: (GridSettings) -> Unit
) {
    var dimensionText by remember { mutableStateOf(currentSettings.dimension.toString()) }
    var imageUrl by remember { mutableStateOf(if(currentSettings.imageUrl.isEmpty()) "https://apptweak-blog.imgix.net/2025/04/1-Headspace-AS.PNG" else currentSettings.imageUrl) }
    var dimensionError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configure Grid") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = dimensionText,
                    onValueChange = {
                        dimensionText = it
                        dimensionError = false
                    },
                    label = { Text("Grid dimension (1–12)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = dimensionError,
                    supportingText = if (dimensionError) {
                        { Text("Enter a number between 1 and 12") }
                    } else null,
                    singleLine = true
                )
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Image URL (optional)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val dim = dimensionText.toIntOrNull()
                if (dim == null || dim !in 1..12) {
                    dimensionError = true
                } else {
                    onConfirm(GridSettings(dimension = dim, imageUrl = imageUrl.trim()))
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
