package com.example.musicplayerapp.domain.usecase

import com.example.musicplayerapp.data.model.Playlist
import com.example.musicplayerapp.data.model.Song

/**
 * Pure business logic, no coroutines/Android needed — trivial to unit test.
 */
class GetSongsForPlaylistUseCase {
    operator fun invoke(playlist: Playlist, allSongs: List<Song>): List<Song> =
        allSongs.filter { it.id in playlist.songIds }
}
