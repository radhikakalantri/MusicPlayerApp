package com.example.musicplayerapp.fakes

import com.example.musicplayerapp.data.model.Artist
import com.example.musicplayerapp.data.model.Episode
import com.example.musicplayerapp.data.model.Movie
import com.example.musicplayerapp.data.model.Playlist
import com.example.musicplayerapp.data.model.Podcast
import com.example.musicplayerapp.data.model.Song
import com.example.musicplayerapp.data.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMusicRepository(
    private val songs: List<Song> = defaultSongs,
    private val playlists: List<Playlist> = defaultPlaylists,
    private val artists: List<Artist> = defaultArtists,
    private val movies: List<Movie> = defaultMovies,
    private val podcasts: List<Podcast> = defaultPodcasts,
    private val episodes: List<Episode> = defaultEpisodes
) : MusicRepository {

    override fun getSongs(): Flow<List<Song>> = flow { emit(songs) }

    override fun getPlaylists(): Flow<List<Playlist>> = flow { emit(playlists) }

    override fun getArtists(): Flow<List<Artist>> = flow { emit(artists) }

    override fun getMovies(): Flow<List<Movie>> = flow { emit(movies) }

    override fun getPodcasts(): Flow<List<Podcast>> = flow { emit(podcasts) }

    override fun getEpisodes(): Flow<List<Episode>> = flow { emit(episodes) }

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

        val defaultEpisodes = listOf(
            Episode(101, "Episode One", "Test Podcast", "Test Host", "https://example.com/e1.mp3", "https://example.com/e1.jpg", "#111111", "10 min"),
            Episode(102, "Episode Two", "Test Podcast", "Test Host", "https://example.com/e2.mp3", "https://example.com/e2.jpg", "#222222", "15 min")
        )

        val defaultPodcasts = listOf(
            Podcast(title = "Test Podcast", host = "Test Host", coverUrl = "https://example.com/e1.jpg", episodeIds = listOf(101, 102))
        )
    }
}
