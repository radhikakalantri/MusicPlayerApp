package com.example.musicplayerapp.domain.usecase

import com.example.musicplayerapp.data.model.Movie
import com.example.musicplayerapp.data.repository.MusicRepository
import kotlinx.coroutines.flow.Flow

class GetMoviesUseCase(private val repository: MusicRepository) {
    operator fun invoke(): Flow<List<Movie>> = repository.getMovies()
}
