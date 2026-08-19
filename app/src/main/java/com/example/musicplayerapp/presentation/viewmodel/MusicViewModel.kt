package com.example.musicplayerapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapp.data.model.Artist
import com.example.musicplayerapp.data.model.Movie
import com.example.musicplayerapp.data.model.Playlist
import com.example.musicplayerapp.data.model.Song
import com.example.musicplayerapp.domain.PlaybackController
import com.example.musicplayerapp.domain.usecase.GetArtistsUseCase
import com.example.musicplayerapp.domain.usecase.GetMoviesUseCase
import com.example.musicplayerapp.domain.usecase.GetPlaylistsUseCase
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
    }

    fun songsForMovie(movie: Movie): List<Song> =
        _songs.value.filter { it.id in movie.songIds }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun songsForPlaylist(playlist: Playlist): List<Song> =
        getSongsForPlaylistUseCase(playlist, _songs.value)

    fun playSong(song: Song) {
        playbackController.play(song)
    }

    fun togglePlayPause() {
        val current = playbackState.value
        val song = current.currentSong ?: return
        if (current.isPlaying) playbackController.pause() else playbackController.play(song)
    }

    fun stop() {
        playbackController.stop()
    }
}
