package com.example.musicplayerapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.musicplayerapp.data.model.Playlist
import com.example.musicplayerapp.data.model.Song
import com.example.musicplayerapp.presentation.components.GradientButton
import com.example.musicplayerapp.presentation.components.PlaylistCard
import com.example.musicplayerapp.presentation.components.SongItem
import com.example.musicplayerapp.presentation.theme.SpotifyBlack
import com.example.musicplayerapp.presentation.viewmodel.MusicViewModel

@Composable
fun HomeScreen(
    viewModel: MusicViewModel,
    onPlaylistClick: (Playlist) -> Unit,
    onSongClick: (Song) -> Unit
) {
    val songs by viewModel.songs.collectAsState()
    val playlists by viewModel.playlists.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SpotifyBlack),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Text(
                text = "Good evening",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (songs.isNotEmpty()) {
                GradientButton(
                    text = "Shuffle Play",
                    onClick = { onSongClick(songs.random()) },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Text(
                text = "Your playlists",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 20.dp)
            ) {
                items(playlists) { playlist ->
                    PlaylistCard(playlist = playlist) { onPlaylistClick(playlist) }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }

        item {
            Text(
                text = "Popular right now",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(songs) { song ->
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                SongItem(song = song) { onSongClick(song) }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }
    }
}
