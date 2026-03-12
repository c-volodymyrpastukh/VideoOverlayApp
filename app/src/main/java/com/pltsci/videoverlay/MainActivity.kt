package com.pltsci.videoverlay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pltsci.videoverlay.data.SettingsRepository
import com.pltsci.videoverlay.ui.MainScreen

class MainActivity : ComponentActivity() {

    private val repository by lazy { SettingsRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen(repository = repository)
        }
    }
}
