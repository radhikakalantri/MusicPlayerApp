package com.example.musicplayerapp.fakes

import com.example.musicplayerapp.data.model.Song
import com.example.musicplayerapp.domain.PlaybackController

/**
 * Records calls instead of touching Android Intents/Services, so
 * MusicViewModel can be tested with plain JUnit.
 */
class FakePlaybackController : PlaybackController {

    var lastPlayed: Song? = null
        private set
    var pauseCallCount = 0
        private set
    var stopCallCount = 0
        private set

    override fun play(song: Song) {
        lastPlayed = song
    }

    override fun pause() {
        pauseCallCount++
    }

    override fun stop() {
        stopCallCount++
    }
}
