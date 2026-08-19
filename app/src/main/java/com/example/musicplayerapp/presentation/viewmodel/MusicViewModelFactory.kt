package com.example.musicplayerapp.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicplayerapp.data.repository.MusicRepositoryImpl
import com.example.musicplayerapp.domain.usecase.GetArtistsUseCase
import com.example.musicplayerapp.domain.usecase.GetEpisodesUseCase
import com.example.musicplayerapp.domain.usecase.GetMoviesUseCase
import com.example.musicplayerapp.domain.usecase.GetPlaylistsUseCase
import com.example.musicplayerapp.domain.usecase.GetPodcastsUseCase
import com.example.musicplayerapp.domain.usecase.GetSongsForPlaylistUseCase
import com.example.musicplayerapp.domain.usecase.GetSongsUseCase
import com.example.musicplayerapp.service.PlaybackManager
import com.example.musicplayerapp.service.ServicePlaybackController

/**
 * Manual dependency wiring (no Hilt/Koin to keep the project dependency-free).
 * This is the one place production collaborators get constructed.
 */
class MusicViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = MusicRepositoryImpl()
        return MusicViewModel(
            getSongsUseCase = GetSongsUseCase(repository),
            getPlaylistsUseCase = GetPlaylistsUseCase(repository),
            getArtistsUseCase = GetArtistsUseCase(repository),
            getMoviesUseCase = GetMoviesUseCase(repository),
            getPodcastsUseCase = GetPodcastsUseCase(repository),
            getEpisodesUseCase = GetEpisodesUseCase(repository),
            getSongsForPlaylistUseCase = GetSongsForPlaylistUseCase(),
            playbackController = ServicePlaybackController(application),
            playbackStateSource = PlaybackManager.state
        ) as T
    }
}
