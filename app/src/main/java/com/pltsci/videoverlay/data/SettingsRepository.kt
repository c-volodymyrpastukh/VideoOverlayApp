package com.pltsci.videoverlay.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pltsci.videoverlay.model.GridSettings
import com.pltsci.videoverlay.model.Scenario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private val dimensionKey = intPreferencesKey("dimension")
    private val imageUrlKey = stringPreferencesKey("image_url")
    private val scenarioJsonKey = stringPreferencesKey("scenario_json")
    private val scenarioActiveKey = booleanPreferencesKey("scenario_active")

    private val json = Json { ignoreUnknownKeys = true }

    val settings: Flow<GridSettings> = context.dataStore.data.map { prefs ->
        val scenario = prefs[scenarioJsonKey]?.let { raw ->
            try {
                json.decodeFromString<Scenario>(raw)
            } catch (_: Exception) {
                null
            }
        }
        GridSettings(
            dimension = prefs[dimensionKey] ?: 4,
            imageUrl = prefs[imageUrlKey] ?: "",
            scenario = scenario,
            scenarioActive = prefs[scenarioActiveKey] ?: false
        )
    }

    suspend fun save(settings: GridSettings) {
        context.dataStore.edit { prefs ->
            prefs[dimensionKey] = settings.dimension
            prefs[imageUrlKey] = settings.imageUrl
            prefs[scenarioActiveKey] = settings.scenarioActive
            if (settings.scenario != null) {
                prefs[scenarioJsonKey] = json.encodeToString(Scenario.serializer(), settings.scenario)
            } else {
                prefs.remove(scenarioJsonKey)
            }
        }
    }
}
