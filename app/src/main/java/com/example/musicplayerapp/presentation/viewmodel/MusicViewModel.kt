package com.example.musicplayerapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapp.data.model.Artist
import com.example.musicplayerapp.data.model.Episode
import com.example.musicplayerapp.data.model.Movie
import com.example.musicplayerapp.data.model.Playlist
import com.example.musicplayerapp.data.model.Podcast
import com.example.musicplayerapp.data.model.Song
import com.example.musicplayerapp.domain.PlaybackController
import com.example.musicplayerapp.domain.usecase.GetArtistsUseCase
import com.example.musicplayerapp.domain.usecase.GetEpisodesUseCase
import com.example.musicplayerapp.domain.usecase.GetMoviesUseCase
import com.example.musicplayerapp.domain.usecase.GetPlaylistsUseCase
import com.example.musicplayerapp.domain.usecase.GetPodcastsUseCase
import com.example.musicplayerapp.domain.usecase.GetSongsForPlaylistUseCase
import com.example.musicplayerapp.domain.usecase.GetSongsUseCase
import com.example.musicplayerapp.service.PlaybackState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Plain ViewModel (not AndroidViewModel) — it only depends on abstractions
 * (use cases + PlaybackController), never on Context/Intent/Service
 * directly. That's what lets it be unit-tested with plain fakes, no
 * Robolectric/Instrumentation needed.
 */
class MusicViewModel(
    private val getSongsUseCase: GetSongsUseCase,
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val getArtistsUseCase: GetArtistsUseCase,
    private val getMoviesUseCase: GetMoviesUseCase,
    private val getPodcastsUseCase: GetPodcastsUseCase,
    private val getEpisodesUseCase: GetEpisodesUseCase,
    private val getSongsForPlaylistUseCase: GetSongsForPlaylistUseCase,
    private val playbackController: PlaybackController,
    playbackStateSource: StateFlow<PlaybackState>
) : ViewModel() {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _podcasts = MutableStateFlow<List<Podcast>>(emptyList())
    val podcasts: StateFlow<List<Podcast>> = _podcasts

    private val _episodes = MutableStateFlow<List<Episode>>(emptyList())
    val episodes: StateFlow<List<Episode>> = _episodes

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    /** Matches by song title, artist name, OR the movie/soundtrack it's from. */
    val searchResults: StateFlow<List<Song>> = combine(_songs, _searchQuery) { songs, query ->
        if (query.isBlank()) {
            emptyList()
        } else {
            songs.filter { song ->
                song.title.contains(query, ignoreCase = true) ||
                    song.artist.contains(query, ignoreCase = true) ||
                    (song.movieTitle?.contains(query, ignoreCase = true) == true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Lets a search for a musician's name surface the artist itself, not just their tracks. */
    val artistSearchResults: StateFlow<List<Artist>> = combine(_artists, _searchQuery) { artists, query ->
        if (query.isBlank()) {
            emptyList()
        } else {
            artists.filter { it.name.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Lets a search for a movie name surface the soundtrack itself, alongside its songs. */
    val movieSearchResults: StateFlow<List<Movie>> = combine(_movies, _searchQuery) { movies, query ->
        if (query.isBlank()) {
            emptyList()
        } else {
            movies.filter { it.title.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val playbackState: StateFlow<PlaybackState> = playbackStateSource.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlaybackState()
    )

    init {
        viewModelScope.launch { getSongsUseCase().collect { _songs.value = it } }
        viewModelScope.launch { getPlaylistsUseCase().collect { _playlists.value = it } }
        viewModelScope.launch { getArtistsUseCase().collect { _artists.value = it } }
        viewModelScope.launch { getMoviesUseCase().collect { _movies.value = it } }
        viewModelScope.launch { getPodcastsUseCase().collect { _podcasts.value = it } }
        viewModelScope.launch { getEpisodesUseCase().collect { _episodes.value = it } }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun songsForMovie(movie: Movie): List<Song> =
        _songs.value.filter { it.id in movie.songIds }

    fun songsForPlaylist(playlist: Playlist): List<Song> =
        getSongsForPlaylistUseCase(playlist, _songs.value)

    fun episodesForPodcast(podcast: Podcast): List<Episode> =
        _episodes.value.filter { it.id in podcast.episodeIds }

    /**
     * Plays a song, queuing [queueContext] around it (defaults to the full
     * catalog) so next/previous naturally cycle through that list —
     * e.g. pass a playlist's songs so skipping stays within the playlist.
     */
    fun playSong(song: Song, queueContext: List<Song> = _songs.value) {
        val index = queueContext.indexOf(song).let { if (it >= 0) it else 0 }
        playbackController.playQueue(queueContext, index)
    }

    fun playEpisode(episode: Episode, queueContext: List<Episode> = _episodes.value) {
        val index = queueContext.indexOf(episode).let { if (it >= 0) it else 0 }
        playbackController.playQueue(queueContext, index)
    }

    /** Jumps to an item already sitting in the current queue — used by the "Up Next" screen. */
    fun playAtQueueIndex(index: Int) {
        playbackController.playAtIndex(index)
    }

    fun togglePlayPause() {
        val current = playbackState.value
        if (current.currentItem == null) return
        if (current.isPlaying) playbackController.pause() else playbackController.resume()
    }

    fun skipNext() {
        playbackController.next()
    }

    fun skipPrevious() {
        playbackController.previous()
    }

    fun seekTo(positionMs: Long) {
        playbackController.seekTo(positionMs)
    }

    fun toggleShuffle() {
        playbackController.setShuffleEnabled(!playbackState.value.isShuffleEnabled)
    }

    fun setPlaybackSpeed(speed: Float) {
        playbackController.setPlaybackSpeed(speed)
    }

    fun stop() {
        playbackController.stop()
    }
}
