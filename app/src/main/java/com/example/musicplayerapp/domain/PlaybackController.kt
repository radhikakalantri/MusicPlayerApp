package com.example.musicplayerapp.domain

import com.example.musicplayerapp.data.model.Song

/**
 * Abstraction the ViewModel depends on instead of talking to the Android
 * Service/Intent APIs directly. Production code wires this to
 * ServicePlaybackController; tests wire it to a lightweight fake — that's
 * what makes MusicViewModel unit-testable with plain JUnit.
 */
interface PlaybackController {
    fun play(song: Song)
    fun pause()
    fun stop()
}
