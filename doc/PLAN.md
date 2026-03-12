# VideoOverlayApp — Development Plan

## Context

Temporary Android app for video shoot purposes. The actor/driver will be instructed to touch specific positions on the screen. The app captures those touches visually. The real app is not yet ready, so this overlay acts as a stand-in — a transparent grid layered over a reference screenshot, composited in post-production.

---

## Project State

- **Package:** `com.pltsci.videoverlay`
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 36
- **Build system:** Gradle Kotlin DSL
- **UI toolkit:** Jetpack Compose (100% — no XML layouts)
- **Current state:** Empty Android Studio project — no Activity, no layouts yet.

---

## Architecture

Single-screen Compose app, no navigation needed.

```
MainActivity (setContent)
  └── MainScreen (Composable)
        └── Box (full screen)
              ├── [layer 0] AsyncImage — reference image, fills the Box
              ├── [layer 1] GridOverlay — transparent grid of square cells
              └── [layer 2] SettingsButton — top-right corner, always on top
        └── SettingsDialog (Composable dialog, shown on demand)
```

State is hoisted in `MainScreen` via `remember`/`mutableStateOf`. Settings persisted with `DataStore Preferences`.

---

## File Plan

```
app/src/main/
  java/com/pltsci/videoverlay/
    MainActivity.kt          — sets Compose content, edge-to-edge
    ui/
      MainScreen.kt          — root composable, Box layering
      GridOverlay.kt         — grid of GridCell composables
      GridCell.kt            — single square cell with states
      SettingsDialog.kt      — dialog composable (dimension + image URL)
    model/
      GridSettings.kt        — data class: dimension, imageUrl
    data/
      SettingsRepository.kt  — DataStore read/write for GridSettings
    theme/
      Theme.kt               — MaterialTheme setup (dark/light)
      Color.kt               — app color tokens
```

No XML layout files. Drawables only for the launcher icon (already present).

---

## Feature Breakdown

### 1. Layered Screen (Box)

The root composable is a `Box` that fills the entire screen:

1. **Bottom layer — Reference image:** `AsyncImage` (Coil Compose) fills the box with `ContentScale.Crop` or `ContentScale.FillBounds`. Hidden (`alpha = 0f` or not composed) when URL is empty/invalid.
2. **Middle layer — Grid overlay:** Transparent grid drawn on top of the image. Cells are partially transparent so the image underneath is visible.
3. **Top layer — Settings button:** Positioned at `Alignment.TopEnd`, always rendered above the grid.

### 2. Grid Matrix

- `LazyVerticalGrid(columns = Fixed(dimension))` inside a `Box` that fills available space.
- Each cell uses `aspectRatio(1f)` to enforce square shape.
- Cell labels: sequential numbers `1, 2, 3, ...` (up to `dimension × dimension`), or letters for small grids. Non-bright gray text.
- Default dimension: **4** (4×4 = 16 cells).

### 3. Cell Visual States

| State   | Background fill           | Border                  |
|---------|---------------------------|-------------------------|
| Default | Black 20% (`#33000000`)   | Gray `#80808080`, 2dp   |
| Active  | Green 40% (`#6600AA44`)   | Green `#FF00AA44`, 2dp  |
| Ripple  | `indication = ripple`     | —                       |

- State held in `remember { mutableStateOf(false) }` per cell (or a `Set<Int>` of active indices at the grid level).
- Tap toggles active state (tap again → resets to default).
- Compose `indication` + `interactionSource` provides the ripple on press.
- Border rendered via `Modifier.border(2.dp, color, shape = RectangleShape)`.

### 4. Settings Button

- `Text("Settings")` or `Icon(Icons.Default.Settings)` composable at `Alignment.TopEnd` inside the root `Box`.
- Wrapped in `clickable { showDialog = true }` with padding.
- Color: white or light gray (visible over any background image).

### 5. Settings Dialog

`AlertDialog` composable with two fields:

| Field | Type | Validation |
|---|---|---|
| Matrix dimension | `OutlinedTextField`, numeric | 1–12, integer |
| Image URL | `OutlinedTextField`, text | optional, no strict validation |

On confirm: state updates in `MainScreen` and persisted via `SettingsRepository`. Grid recomposes with new dimension; image reloads with new URL.

---

## Dependencies to Add (`app/build.gradle.kts`)

```kotlin
// Compose BOM — manages all Compose versions together
implementation(platform("androidx.compose:compose-bom:2024.06.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.ui:ui-tooling-preview")
implementation("androidx.compose.material3:material3")
implementation("androidx.activity:activity-compose:1.9.0")

// Coil for Compose image loading
implementation("io.coil-kt:coil-compose:2.7.0")

// DataStore for settings persistence
implementation("androidx.datastore:datastore-preferences:1.1.1")

// Debug only
debugImplementation("androidx.compose.ui:ui-tooling")
```

---

## Implementation Steps (in order)

1. **Add Compose + Coil + DataStore dependencies** to `app/build.gradle.kts`; sync.
2. **Add `INTERNET` permission** to `AndroidManifest.xml`.
3. **Create `theme/`** — `Color.kt` and `Theme.kt` with `MaterialTheme`.
4. **Create `model/GridSettings.kt`** — data class.
5. **Create `data/SettingsRepository.kt`** — DataStore read (`Flow<GridSettings>`) and write (`suspend fun save`).
6. **Create `ui/GridCell.kt`** — square composable with `aspectRatio(1f)`, `border`, `indication`, active/default background.
7. **Create `ui/GridOverlay.kt`** — `LazyVerticalGrid` of `GridCell` items; holds active-cell state set.
8. **Create `ui/SettingsDialog.kt`** — `AlertDialog` composable with dimension + URL fields.
9. **Create `ui/MainScreen.kt`** — root `Box`: `AsyncImage` → `GridOverlay` → settings button; collects settings from repository as `State`.
10. **Create `MainActivity.kt`** — `ComponentActivity`, `setContent { MainScreen() }`, edge-to-edge enabled.
11. **Declare `MainActivity`** in `AndroidManifest.xml` (with `android:theme` pointing to no-action-bar theme).
12. **Test** on emulator: square cells, ripple, active toggle, image visible under grid, settings dialog, persistence across restart.

---

## Out of Scope

- Authentication, networking beyond image loading
- Multiple screens
- Video recording
- Any backend
- Accessibility beyond default Compose behavior
