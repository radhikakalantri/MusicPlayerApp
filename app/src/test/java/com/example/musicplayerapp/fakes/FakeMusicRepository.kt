package com.example.musicplayerapp.fakes

import com.example.musicplayerapp.data.model.Artist
import com.example.musicplayerapp.data.model.Movie
import com.example.musicplayerapp.data.model.Playlist
import com.example.musicplayerapp.data.model.Song
import com.example.musicplayerapp.data.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMusicRepository(
    private val songs: List<Song> = defaultSongs,
    private val playlists: List<Playlist> = defaultPlaylists,
    private val artists: List<Artist> = defaultArtists,
    private val movies: List<Movie> = defaultMovies
) : MusicRepository {

    override fun getSongs(): Flow<List<Song>> = flow { emit(songs) }

    override fun getPlaylists(): Flow<List<Playlist>> = flow { emit(playlists) }

    override fun getArtists(): Flow<List<Artist>> = flow { emit(artists) }

    override fun getMovies(): Flow<List<Movie>> = flow { emit(movies) }

    companion object {
        val defaultSongs = listOf(
            Song(1, "Test Song One", "Artist A", "https://example.com/1.mp3", "https://example.com/1.jpg", "#FFFFFF", movieTitle = "Test Movie"),
            Song(2, "Test Song Two", "Artist B", "https://example.com/2.mp3", "https://example.com/2.jpg", "#000000", movieTitle = "Test Movie"),
            Song(3, "Another Track", "Artist A", "https://example.com/3.mp3", "https://example.com/3.jpg", "#123456", movieTitle = null)
        )

        val defaultPlaylists = listOf(
            Playlist(1, "Test Playlist", "For testing", "https://example.com/cover.jpg", listOf(1, 2))
        )

        val defaultArtists = listOf(
            Artist(name = "Artist A", imageUrl = "https://example.com/1.jpg", songCount = 2),
            Artist(name = "Artist B", imageUrl = "https://example.com/2.jpg", songCount = 1)
        )

        val defaultMovies = listOf(
            Movie(title = "Test Movie", coverUrl = "https://example.com/1.jpg", songIds = listOf(1, 2))
        )
    }
}
