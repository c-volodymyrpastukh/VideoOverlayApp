package com.pltsci.videoverlay.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pltsci.videoverlay.model.Route
import com.pltsci.videoverlay.model.Scenario
import com.pltsci.videoverlay.model.ScenarioLink

private data class MutableRoute(
    var cellIndicesText: String,
    var targetLinkIndex: Int
)

private data class MutableLink(
    var imageUrl: String,
    val routes: MutableList<MutableRoute>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenarioEditor(
    scenario: Scenario?,
    scenarioActive: Boolean,
    dimension: Int,
    onDismiss: () -> Unit,
    onSave: (Scenario?, Boolean) -> Unit
) {
    var active by remember { mutableStateOf(scenarioActive) }
    val links = remember {
        (scenario?.links?.map { link ->
            MutableLink(
                imageUrl = link.imageUrl,
                routes = link.routes.map { route ->
                    MutableRoute(
                        cellIndicesText = route.cellIndices.map { it + 1 }.sorted().joinToString(", "),
                        targetLinkIndex = route.targetLinkIndex
                    )
                }.toMutableList()
            )
        } ?: emptyList()).toMutableStateList()
    }
    // Force recomposition counter
    var recomposeKey by remember { mutableIntStateOf(0) }
    // Read recomposeKey to trigger recomposition
    recomposeKey.let { _ -> }

    val maxCellIndex = dimension * dimension
    val clipboardManager = LocalClipboardManager.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("Scenario Configuration", fontSize = 22.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Enable Scenario Mode", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Switch(checked = active, onCheckedChange = { active = it })
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    links.forEachIndexed { linkIdx, link ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Link ${linkIdx + 1}", fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(
                                        value = link.imageUrl,
                                        onValueChange = {
                                            links[linkIdx] = link.copy(imageUrl = it)
                                        },
                                        label = { Text("Image URL") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    TextButton(onClick = {
                                        clipboardManager.getText()?.text?.let {
                                            links[linkIdx] = link.copy(imageUrl = it)
                                        }
                                    }) {
                                        Text("Paste")
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Routes:", fontSize = 16.sp)

                                link.routes.forEachIndexed { routeIdx, route ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = route.cellIndicesText,
                                            onValueChange = { newText ->
                                                val updatedRoutes = link.routes.toMutableList()
                                                updatedRoutes[routeIdx] = route.copy(cellIndicesText = newText)
                                                links[linkIdx] = link.copy(routes = updatedRoutes)
                                            },
                                            label = { Text("Cells") },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Target link dropdown
                                        var expanded by remember { mutableStateOf(false) }
                                        ExposedDropdownMenuBox(
                                            expanded = expanded,
                                            onExpandedChange = { expanded = it },
                                            modifier = Modifier.width(120.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = "\u2192 Link ${route.targetLinkIndex + 1}",
                                                onValueChange = {},
                                                readOnly = true,
                                                singleLine = true,
                                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                            )
                                            ExposedDropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false }
                                            ) {
                                                links.forEachIndexed { targetIdx, _ ->
                                                    DropdownMenuItem(
                                                        text = { Text("Link ${targetIdx + 1}") },
                                                        onClick = {
                                                            val updatedRoutes = link.routes.toMutableList()
                                                            updatedRoutes[routeIdx] = route.copy(targetLinkIndex = targetIdx)
                                                            links[linkIdx] = link.copy(routes = updatedRoutes)
                                                            expanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }

                                        TextButton(onClick = {
                                            val updatedRoutes = link.routes.toMutableList()
                                            updatedRoutes.removeAt(routeIdx)
                                            links[linkIdx] = link.copy(routes = updatedRoutes)
                                            recomposeKey++
                                        }) {
                                            Text("X")
                                        }
                                    }
                                }

                                Row {
                                    TextButton(onClick = {
                                        val updatedRoutes = link.routes.toMutableList()
                                        updatedRoutes.add(MutableRoute(cellIndicesText = "", targetLinkIndex = 0))
                                        links[linkIdx] = link.copy(routes = updatedRoutes)
                                        recomposeKey++
                                    }) {
                                        Text("Add Route")
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    TextButton(onClick = {
                                        links.removeAt(linkIdx)
                                        recomposeKey++
                                    }) {
                                        Text("Delete Link")
                                    }
                                }
                            }
                        }
                    }

                    TextButton(onClick = {
                        links.add(MutableLink(imageUrl = "", routes = mutableListOf()))
                    }) {
                        Text("Add Link", fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        val builtLinks = links.map { link ->
                            ScenarioLink(
                                imageUrl = link.imageUrl.trim(),
                                routes = link.routes.mapNotNull { route ->
                                    val indices = route.cellIndicesText
                                        .split(",")
                                        .mapNotNull { it.trim().toIntOrNull() }
                                        .filter { it in 1..maxCellIndex }
                                        .map { it - 1 }
                                        .toSet()
                                    if (indices.isEmpty()) null
                                    else Route(
                                        cellIndices = indices,
                                        targetLinkIndex = route.targetLinkIndex.coerceIn(0, links.size - 1)
                                    )
                                }
                            )
                        }
                        val scenario = if (builtLinks.isEmpty()) null else Scenario(links = builtLinks)
                        onSave(scenario, active)
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
