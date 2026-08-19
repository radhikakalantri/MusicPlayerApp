package com.example.musicplayerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.musicplayerapp.presentation.navigation.MusicNavGraph
import com.example.musicplayerapp.presentation.theme.MusicPlayerAppTheme
import com.example.musicplayerapp.presentation.viewmodel.MusicViewModel
import com.example.musicplayerapp.presentation.viewmodel.MusicViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: MusicViewModel by viewModels {
        MusicViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicPlayerAppTheme {
                MusicNavGraph(viewModel = viewModel)
            }
        }
    }
}
