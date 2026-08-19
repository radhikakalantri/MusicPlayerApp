package com.example.musicplayerapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.musicplayerapp.data.model.Playlist
import com.example.musicplayerapp.presentation.theme.AccentPink
import com.example.musicplayerapp.presentation.theme.PurpleGradientStart
import com.example.musicplayerapp.presentation.theme.SpotifyBlack
import com.example.musicplayerapp.presentation.viewmodel.MusicViewModel

@Composable
fun LibraryScreen(
    viewModel: MusicViewModel,
    onPlaylistClick: (Playlist) -> Unit,
    onLikedSongsClick: () -> Unit
) {
    val playlists by viewModel.playlists.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SpotifyBlack),
        contentPadding = PaddingValues(20.dp)
    ) {
        item {
            Text(text = "Your Library", color = Color.White, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(20.dp))
            LikedSongsRow(onClick = onLikedSongsClick)
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(playlists) { playlist ->
            PlaylistRow(playlist = playlist) { onPlaylistClick(playlist) }
        }
    }
}

@Composable
private fun LikedSongsRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Brush.linearGradient(listOf(PurpleGradientStart, AccentPink))),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Favorite, contentDescription = "Liked songs", tint = Color.White)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = "Liked Songs", color = Color.White, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun PlaylistRow(playlist: Playlist, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = playlist.coverUrl,
            contentDescription = playlist.name,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(6.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = playlist.name, color = Color.White, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "Playlist · ${playlist.songIds.size} songs",
                color = Color.White.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
