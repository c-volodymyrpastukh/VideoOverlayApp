package com.pltsci.videoverlay.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pltsci.videoverlay.model.GridSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private val dimensionKey = intPreferencesKey("dimension")
    private val imageUrlKey = stringPreferencesKey("image_url")

    val settings: Flow<GridSettings> = context.dataStore.data.map { prefs ->
        GridSettings(
            dimension = prefs[dimensionKey] ?: 4,
            imageUrl = prefs[imageUrlKey] ?: ""
        )
    }

    suspend fun save(settings: GridSettings) {
        context.dataStore.edit { prefs ->
            prefs[dimensionKey] = settings.dimension
            prefs[imageUrlKey] = settings.imageUrl
        }
    }
}
