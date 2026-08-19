package com.example.musicplayerapp.data.repository

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MusicRepositoryImplTest {

    private val repository: MusicRepository = MusicRepositoryImpl()

    @Test
    fun `getSongs returns a non-empty catalog with unique ids`() = runTest {
        val songs = repository.getSongs().first()

        assertTrue("expected at least one song", songs.isNotEmpty())
        val ids = songs.map { it.id }
        assertEquals("song ids should be unique", ids.size, ids.toSet().size)
    }

    @Test
    fun `getSongs returns songs with non-blank title, artist and urls`() = runTest {
        val songs = repository.getSongs().first()

        songs.forEach { song ->
            assertTrue("title should not be blank for song ${song.id}", song.title.isNotBlank())
            assertTrue("artist should not be blank for song ${song.id}", song.artist.isNotBlank())
            assertTrue("url should not be blank for song ${song.id}", song.url.isNotBlank())
            assertTrue("imageUrl should not be blank for song ${song.id}", song.imageUrl.isNotBlank())
        }
    }

    @Test
    fun `getPlaylists returns playlists whose songIds all exist in the catalog`() = runTest {
        val songs = repository.getSongs().first()
        val playlists = repository.getPlaylists().first()
        val validSongIds = songs.map { it.id }.toSet()

        assertTrue("expected at least one playlist", playlists.isNotEmpty())
        playlists.forEach { playlist ->
            assertTrue(
                "playlist '${playlist.name}' references a song id not present in the catalog",
                validSongIds.containsAll(playlist.songIds)
            )
            assertTrue("playlist '${playlist.name}' should contain at least one song", playlist.songIds.isNotEmpty())
        }
    }

    @Test
    fun `getArtists derives one entry per unique artist name with an accurate song count`() = runTest {
        val songs = repository.getSongs().first()
        val artists = repository.getArtists().first()

        val expectedArtistNames = songs.map { it.artist }.toSet()
        val actualArtistNames = artists.map { it.name }.toSet()
        assertEquals(expectedArtistNames, actualArtistNames)

        artists.forEach { artist ->
            val expectedCount = songs.count { it.artist == artist.name }
            assertEquals(
                "song count for '${artist.name}' should match the catalog",
                expectedCount,
                artist.songCount
            )
        }
    }

    @Test
    fun `getMovies only includes songs that are actually tagged with that movie`() = runTest {
        val songs = repository.getSongs().first()
        val movies = repository.getMovies().first()

        assertTrue("expected at least one soundtrack for the sample catalog", movies.isNotEmpty())
        movies.forEach { movie ->
            movie.songIds.forEach { songId ->
                val song = songs.first { it.id == songId }
                assertEquals(
                    "song ${song.id} should be tagged with the movie it's grouped under",
                    movie.title,
                    song.movieTitle
                )
            }
        }
    }

    @Test
    fun `getMovies excludes songs with no movie tag`() = runTest {
        val songs = repository.getSongs().first()
        val movies = repository.getMovies().first()

        val songsWithoutMovie = songs.filter { it.movieTitle == null }.map { it.id }
        val idsInMovies = movies.flatMap { it.songIds }

        assertTrue(
            "standalone singles should never appear inside a movie's song list",
            songsWithoutMovie.none { it in idsInMovies }
        )
    }
}
