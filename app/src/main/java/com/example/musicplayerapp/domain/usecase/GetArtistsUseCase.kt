package com.example.musicplayerapp.domain.usecase

import com.example.musicplayerapp.data.model.Artist
import com.example.musicplayerapp.data.repository.MusicRepository
import kotlinx.coroutines.flow.Flow

class GetArtistsUseCase(private val repository: MusicRepository) {
    operator fun invoke(): Flow<List<Artist>> = repository.getArtists()
}
