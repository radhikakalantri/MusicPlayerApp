package com.example.musicplayerapp.domain.usecase

import com.example.musicplayerapp.data.model.Playlist
import com.example.musicplayerapp.data.repository.MusicRepository
import kotlinx.coroutines.flow.Flow

class GetPlaylistsUseCase(private val repository: MusicRepository) {
    operator fun invoke(): Flow<List<Playlist>> = repository.getPlaylists()
}
