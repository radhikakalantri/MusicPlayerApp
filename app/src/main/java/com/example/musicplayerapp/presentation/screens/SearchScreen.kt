package com.example.musicplayerapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.musicplayerapp.presentation.components.ArtistCard
import com.example.musicplayerapp.presentation.components.MovieCard
import com.example.musicplayerapp.presentation.components.SongItem
import com.example.musicplayerapp.data.model.Movie
import com.example.musicplayerapp.data.model.Song
import com.example.musicplayerapp.presentation.theme.GenreColors
import com.example.musicplayerapp.presentation.theme.SpotifyBlack
import com.example.musicplayerapp.presentation.viewmodel.MusicViewModel

private val genres = listOf(
    "Pop", "Chill", "Party", "Focus", "Workout", "Throwback", "Indie", "Electronic", "Acoustic"
)

@Composable
fun SearchScreen(
    viewModel: MusicViewModel,
    onSongClick: (Song) -> Unit,
    onMovieClick: (Movie) -> Unit
) {
    val query by viewModel.searchQuery.collectAsState()
    val songResults by viewModel.searchResults.collectAsState()
    val artistResults by viewModel.artistSearchResults.collectAsState()
    val movieResults by viewModel.movieSearchResults.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SpotifyBlack)
            .padding(20.dp)
    ) {
        Text(text = "Search", color = Color.White, style = MaterialTheme.typography.headlineMedium)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Songs, artists, or movies") },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.White.copy(alpha = 0.08f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.08f)
            )
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(20.dp))

        if (query.isBlank()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(genres) { genre ->
                    val index = genres.indexOf(genre)
                    GenreTile(
                        name = genre,
                        colors = listOf(
                            GenreColors[index % GenreColors.size],
                            GenreColors[(index + 3) % GenreColors.size]
                        )
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (movieResults.isNotEmpty()) {
                    item {
                        Text(
                            text = "From Movies",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(movieResults) { movie ->
                                MovieCard(movie = movie) { onMovieClick(movie) }
                            }
                        }
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (artistResults.isNotEmpty()) {
                    item {
                        Text(
                            text = "Artists",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            items(artistResults) { artist -> ArtistCard(artist = artist) }
                        }
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (songResults.isNotEmpty()) {
                    item {
                        Text(
                            text = "Songs",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(songResults) { song ->
                        SongItem(song = song) { onSongClick(song) }
                    }
                }

                if (artistResults.isEmpty() && songResults.isEmpty() && movieResults.isEmpty()) {
                    item {
                        Text(
                            text = "No results for \"$query\"",
                            color = Color.White.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GenreTile(name: String, colors: List<Color>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.6f)
            .clip(RoundedCornerShape(10.dp))
            .background(Brush.linearGradient(colors))
            .padding(14.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Text(text = name, color = Color.White, style = MaterialTheme.typography.titleLarge)
    }
}
