package com.example.musicplayerapp.domain.usecase

import com.example.musicplayerapp.data.model.Song
import com.example.musicplayerapp.data.repository.MusicRepository
import kotlinx.coroutines.flow.Flow

class GetSongsUseCase(private val repository: MusicRepository) {
    operator fun invoke(): Flow<List<Song>> = repository.getSongs()
}
