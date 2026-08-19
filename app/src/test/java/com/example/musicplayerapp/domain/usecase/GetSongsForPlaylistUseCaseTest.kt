package com.example.musicplayerapp.domain.usecase

import com.example.musicplayerapp.data.model.Playlist
import com.example.musicplayerapp.data.model.Song
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetSongsForPlaylistUseCaseTest {

    private val useCase = GetSongsForPlaylistUseCase()

    private val allSongs = listOf(
        Song(1, "One", "Artist A", "url1", "img1", "#111111"),
        Song(2, "Two", "Artist B", "url2", "img2", "#222222"),
        Song(3, "Three", "Artist C", "url3", "img3", "#333333")
    )

    @Test
    fun `returns only songs whose id is in the playlist`() {
        val playlist = Playlist(1, "Mix", "desc", "cover", songIds = listOf(1, 3))

        val result = useCase(playlist, allSongs)

        assertEquals(2, result.size)
        assertTrue(result.any { it.id == 1 })
        assertTrue(result.any { it.id == 3 })
        assertTrue(result.none { it.id == 2 })
    }

    @Test
    fun `returns empty list when playlist has no matching songs`() {
        val playlist = Playlist(2, "Empty", "desc", "cover", songIds = listOf(99))

        val result = useCase(playlist, allSongs)

        assertTrue(result.isEmpty())
    }

    @Test
    fun `preserves the order songs appear in the source list, not the playlist`() {
        val playlist = Playlist(3, "Reordered", "desc", "cover", songIds = listOf(3, 1))

        val result = useCase(playlist, allSongs)

        assertEquals(listOf(1, 3), result.map { it.id })
    }
}
