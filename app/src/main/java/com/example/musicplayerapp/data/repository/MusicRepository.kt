package com.example.musicplayerapp.data.repository

import com.example.musicplayerapp.data.model.Artist
import com.example.musicplayerapp.data.model.Movie
import com.example.musicplayerapp.data.model.Playlist
import com.example.musicplayerapp.data.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getSongs(): Flow<List<Song>>
    fun getPlaylists(): Flow<List<Playlist>>
    fun getArtists(): Flow<List<Artist>>
    fun getMovies(): Flow<List<Movie>>
}
