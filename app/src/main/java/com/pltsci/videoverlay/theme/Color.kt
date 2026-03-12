package com.pltsci.videoverlay.theme

import androidx.compose.ui.graphics.Color

// Cell default state: 20% black fill, 50% gray border
val CellDefaultBackground = Color.Black.copy(alpha = 0.40f)
val CellDefaultBorder = Color(red = 0f, green = 0.667f, blue = 0.267f, alpha = 0.60f)

// Cell active state: 40% green fill, solid green border
val CellActiveBackground = Color(red = 0f, green = 0.667f, blue = 0.267f, alpha = 0.40f)
val CellActiveBorder = Color(red = 0f, green = 0.667f, blue = 0.267f, alpha = 1.0f)

// Cell label: non-bright gray
val CellLabelColor = Color.Red.copy(alpha = 1.0f)

// Settings button overlay background
val SettingsBackground = Color.Black.copy(alpha = 0.50f)
