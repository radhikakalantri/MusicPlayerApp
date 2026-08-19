package com.example.musicplayerapp.presentation.viewmodel

import app.cash.turbine.test
import com.example.musicplayerapp.data.model.Artist
import com.example.musicplayerapp.data.model.Movie
import com.example.musicplayerapp.data.model.Podcast
import com.example.musicplayerapp.domain.usecase.GetArtistsUseCase
import com.example.musicplayerapp.domain.usecase.GetEpisodesUseCase
import com.example.musicplayerapp.domain.usecase.GetMoviesUseCase
import com.example.musicplayerapp.domain.usecase.GetPlaylistsUseCase
import com.example.musicplayerapp.domain.usecase.GetPodcastsUseCase
import com.example.musicplayerapp.domain.usecase.GetSongsForPlaylistUseCase
import com.example.musicplayerapp.domain.usecase.GetSongsUseCase
import com.example.musicplayerapp.fakes.FakeMusicRepository
import com.example.musicplayerapp.fakes.FakePlaybackController
import com.example.musicplayerapp.service.PlaybackState
import com.example.musicplayerapp.util.MainDispatcherRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MusicViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeMusicRepository
    private lateinit var playbackController: FakePlaybackController
    private lateinit var playbackStateFlow: MutableStateFlow<PlaybackState>
    private lateinit var viewModel: MusicViewModel

    @Before
    fun setUp() {
        repository = FakeMusicRepository()
        playbackController = FakePlaybackController()
        playbackStateFlow = MutableStateFlow(PlaybackState())
        viewModel = MusicViewModel(
            getSongsUseCase = GetSongsUseCase(repository),
            getPlaylistsUseCase = GetPlaylistsUseCase(repository),
            getArtistsUseCase = GetArtistsUseCase(repository),
            getMoviesUseCase = GetMoviesUseCase(repository),
            getPodcastsUseCase = GetPodcastsUseCase(repository),
            getEpisodesUseCase = GetEpisodesUseCase(repository),
            getSongsForPlaylistUseCase = GetSongsForPlaylistUseCase(),
            playbackController = playbackController,
            playbackStateSource = playbackStateFlow
        )
    }

    @Test
    fun `songs are loaded from the repository on init`() = runTest {
        viewModel.songs.test {
            var songs = awaitItem()
            if (songs.isEmpty()) songs = awaitItem()
            assertEquals(FakeMusicRepository.defaultSongs, songs)
        }
    }

    @Test
    fun `playlists are loaded from the repository on init`() = runTest {
        viewModel.playlists.test {
            var playlists = awaitItem()
            if (playlists.isEmpty()) playlists = awaitItem()
            assertEquals(FakeMusicRepository.defaultPlaylists, playlists)
        }
    }

    @Test
    fun `artists are loaded from the repository on init`() = runTest {
        viewModel.artists.test {
            var artists = awaitItem()
            if (artists.isEmpty()) artists = awaitItem()
            assertEquals(FakeMusicRepository.defaultArtists, artists)
        }
    }

    @Test
    fun `movies are loaded from the repository on init`() = runTest {
        viewModel.movies.test {
            var movies = awaitItem()
            if (movies.isEmpty()) movies = awaitItem()
            assertEquals(FakeMusicRepository.defaultMovies, movies)
        }
    }

    @Test
    fun `podcasts and episodes are loaded from the repository on init`() = runTest {
        viewModel.podcasts.test {
            var podcasts = awaitItem()
            if (podcasts.isEmpty()) podcasts = awaitItem()
            assertEquals(FakeMusicRepository.defaultPodcasts, podcasts)
        }
        viewModel.episodes.test {
            var episodes = awaitItem()
            if (episodes.isEmpty()) episodes = awaitItem()
            assertEquals(FakeMusicRepository.defaultEpisodes, episodes)
        }
    }

    @Test
    fun `playSong queues the given context and starts at that song's index`() = runTest {
        viewModel.songs.test {
            var songs = awaitItem()
            if (songs.isEmpty()) songs = awaitItem()
        }

        val songs = FakeMusicRepository.defaultSongs
        val target = songs[1]

        viewModel.playSong(target, songs)

        assertEquals(songs, playbackController.lastQueue)
        assertEquals(1, playbackController.lastStartIndex)
    }

    @Test
    fun `playSong defaults to queuing the full catalog when no context is given`() = runTest {
        viewModel.songs.test {
            var songs = awaitItem()
            if (songs.isEmpty()) songs = awaitItem()
        }

        val target = FakeMusicRepository.defaultSongs.first()
        viewModel.playSong(target)

        assertEquals(FakeMusicRepository.defaultSongs, playbackController.lastQueue)
        assertEquals(0, playbackController.lastStartIndex)
    }

    @Test
    fun `playEpisode queues the given context and starts at that episode's index`() = runTest {
        viewModel.episodes.test {
            var episodes = awaitItem()
            if (episodes.isEmpty()) episodes = awaitItem()
        }

        val episodes = FakeMusicRepository.defaultEpisodes
        val target = episodes[1]

        viewModel.playEpisode(target, episodes)

        assertEquals(episodes, playbackController.lastQueue)
        assertEquals(1, playbackController.lastStartIndex)
    }

    @Test
    fun `playAtQueueIndex delegates to the playback controller`() {
        viewModel.playAtQueueIndex(3)

        assertEquals(3, playbackController.lastPlayAtIndex)
    }

    @Test
    fun `togglePlayPause does nothing when nothing is loaded`() {
        viewModel.togglePlayPause()

        assertEquals(0, playbackController.resumeCallCount)
        assertEquals(0, playbackController.pauseCallCount)
    }

    @Test
    fun `togglePlayPause pauses when something is currently playing`() {
        playbackStateFlow.value = PlaybackState(
            queue = FakeMusicRepository.defaultSongs,
            currentIndex = 0,
            isPlaying = true
        )

        viewModel.togglePlayPause()

        assertEquals(1, playbackController.pauseCallCount)
        assertEquals(0, playbackController.resumeCallCount)
    }

    @Test
    fun `togglePlayPause resumes when something is loaded but paused`() {
        playbackStateFlow.value = PlaybackState(
            queue = FakeMusicRepository.defaultSongs,
            currentIndex = 0,
            isPlaying = false
        )

        viewModel.togglePlayPause()

        assertEquals(1, playbackController.resumeCallCount)
        assertEquals(0, playbackController.pauseCallCount)
    }

    @Test
    fun `skipNext and skipPrevious delegate to the playback controller`() {
        viewModel.skipNext()
        viewModel.skipNext()
        viewModel.skipPrevious()

        assertEquals(2, playbackController.nextCallCount)
        assertEquals(1, playbackController.previousCallCount)
    }

    @Test
    fun `seekTo delegates the target position to the playback controller`() {
        viewModel.seekTo(45_000L)

        assertEquals(45_000L, playbackController.lastSeekPositionMs)
    }

    @Test
    fun `toggleShuffle flips the current shuffle state`() {
        playbackStateFlow.value = PlaybackState(isShuffleEnabled = false)

        viewModel.toggleShuffle()

        assertEquals(true, playbackController.lastShuffleEnabled)
    }

    @Test
    fun `setPlaybackSpeed delegates the chosen speed to the playback controller`() {
        viewModel.setPlaybackSpeed(1.5f)

        assertEquals(1.5f, playbackController.lastPlaybackSpeed)
    }

    @Test
    fun `stop delegates to the playback controller`() {
        viewModel.stop()

        assertEquals(1, playbackController.stopCallCount)
    }

    @Test
    fun `search results are empty when the query is blank`() = runTest {
        viewModel.searchResults.test {
            assertTrue(awaitItem().isEmpty())
        }
    }

    @Test
    fun `search filters songs by title case-insensitively`() = runTest {
        viewModel.songs.test {
            var songs = awaitItem()
            if (songs.isEmpty()) songs = awaitItem()
        }

        viewModel.onSearchQueryChange("test song")

        viewModel.searchResults.test {
            var results = awaitItem()
            if (results.isEmpty()) results = awaitItem()
            assertEquals(2, results.size)
            assertTrue(results.all { it.title.contains("Test Song", ignoreCase = true) })
        }
    }

    @Test
    fun `search filters songs by artist`() = runTest {
        viewModel.songs.test {
            var songs = awaitItem()
            if (songs.isEmpty()) songs = awaitItem()
        }

        viewModel.onSearchQueryChange("Artist A")

        viewModel.searchResults.test {
            var results = awaitItem()
            if (results.isEmpty()) results = awaitItem()
            assertEquals(2, results.size)
            assertTrue(results.all { it.artist == "Artist A" })
        }
    }

    @Test
    fun `searching a musician's name surfaces the artist, not just their tracks`() = runTest {
        viewModel.artists.test {
            var artists = awaitItem()
            if (artists.isEmpty()) artists = awaitItem()
        }

        viewModel.onSearchQueryChange("Artist A")

        viewModel.artistSearchResults.test {
            var results: List<Artist> = awaitItem()
            if (results.isEmpty()) results = awaitItem()
            assertEquals(1, results.size)
            assertEquals("Artist A", results.first().name)
        }
    }

    @Test
    fun `artist search is empty when the query is blank`() = runTest {
        viewModel.artistSearchResults.test {
            assertTrue(awaitItem().isEmpty())
        }
    }

    @Test
    fun `searching a movie name surfaces the soundtrack itself`() = runTest {
        viewModel.movies.test {
            var movies = awaitItem()
            if (movies.isEmpty()) movies = awaitItem()
        }

        viewModel.onSearchQueryChange("Test Movie")

        viewModel.movieSearchResults.test {
            var results: List<Movie> = awaitItem()
            if (results.isEmpty()) results = awaitItem()
            assertEquals(1, results.size)
            assertEquals("Test Movie", results.first().title)
        }
    }

    @Test
    fun `searching a movie name also returns the songs tagged with that movie`() = runTest {
        viewModel.songs.test {
            var songs = awaitItem()
            if (songs.isEmpty()) songs = awaitItem()
        }

        viewModel.onSearchQueryChange("Test Movie")

        viewModel.searchResults.test {
            var results = awaitItem()
            if (results.isEmpty()) results = awaitItem()
            assertEquals(2, results.size)
            assertTrue(results.all { it.movieTitle == "Test Movie" })
        }
    }

    @Test
    fun `songsForMovie returns only songs referenced by that movie`() = runTest {
        viewModel.songs.test {
            var songs = awaitItem()
            if (songs.isEmpty()) songs = awaitItem()
        }

        val movie = FakeMusicRepository.defaultMovies.first()
        val result = viewModel.songsForMovie(movie)

        assertEquals(movie.songIds.sorted(), result.map { it.id }.sorted())
    }

    @Test
    fun `songsForPlaylist returns only songs referenced by that playlist`() = runTest {
        viewModel.songs.test {
            var songs = awaitItem()
            if (songs.isEmpty()) songs = awaitItem()
        }

        val playlist = FakeMusicRepository.defaultPlaylists.first()
        val result = viewModel.songsForPlaylist(playlist)

        assertEquals(playlist.songIds.sorted(), result.map { it.id }.sorted())
    }

    @Test
    fun `episodesForPodcast returns only episodes referenced by that podcast`() = runTest {
        viewModel.episodes.test {
            var episodes = awaitItem()
            if (episodes.isEmpty()) episodes = awaitItem()
        }

        val podcast: Podcast = FakeMusicRepository.defaultPodcasts.first()
        val result = viewModel.episodesForPodcast(podcast)

        assertEquals(podcast.episodeIds.sorted(), result.map { it.id }.sorted())
    }
}
