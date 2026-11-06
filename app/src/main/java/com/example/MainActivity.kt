package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.DayZeroScreen
import com.example.ui.DayZeroViewModel
import com.example.ui.theme.DayZeroTheme

class MainActivity : ComponentActivity() {

    private val viewModel: DayZeroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DayZeroTheme(darkTheme = true) {
                DayZeroScreen(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh wallpaper on app open if a new calendar day has begun
        viewModel.refreshWallpaperOnOpen()
    }
}
