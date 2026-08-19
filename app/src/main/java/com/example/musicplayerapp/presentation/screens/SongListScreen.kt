package com.example.musicplayerapp.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.musicplayerapp.data.model.Song
import com.example.musicplayerapp.presentation.components.GradientBackground
import com.example.musicplayerapp.presentation.components.SongItem
import com.example.musicplayerapp.presentation.theme.PurpleGradientEnd
import com.example.musicplayerapp.presentation.theme.PurpleGradientStart
import com.example.musicplayerapp.presentation.theme.SurfaceDark
import com.example.musicplayerapp.presentation.viewmodel.MusicViewModel

@Composable
fun SongListScreen(
    viewModel: MusicViewModel,
    onSongClick: (Song) -> Unit
) {
    val songs by viewModel.songs.collectAsState()

    GradientBackground(colors = listOf(PurpleGradientStart, PurpleGradientEnd, SurfaceDark)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Good vibes only \uD83C\uDFA7",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Pick something to play",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(songs) { song ->
                    SongItem(song = song) { onSongClick(song) }
                }
            }
        }
    }
}
