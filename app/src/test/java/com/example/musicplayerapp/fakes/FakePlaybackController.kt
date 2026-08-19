package com.example.musicplayerapp.fakes

import com.example.musicplayerapp.data.model.Playable
import com.example.musicplayerapp.domain.PlaybackController

/**
 * Records calls instead of touching Android Intents/Services, so
 * MusicViewModel can be tested with plain JUnit.
 */
class FakePlaybackController : PlaybackController {

    var lastQueue: List<Playable>? = null
        private set
    var lastStartIndex: Int? = null
        private set
    var lastPlayAtIndex: Int? = null
        private set
    var resumeCallCount = 0
        private set
    var pauseCallCount = 0
        private set
    var nextCallCount = 0
        private set
    var previousCallCount = 0
        private set
    var lastSeekPositionMs: Long? = null
        private set
    var lastShuffleEnabled: Boolean? = null
        private set
    var lastPlaybackSpeed: Float? = null
        private set
    var stopCallCount = 0
        private set

    override fun playQueue(items: List<Playable>, startIndex: Int) {
        lastQueue = items
        lastStartIndex = startIndex
    }

    override fun playAtIndex(index: Int) {
        lastPlayAtIndex = index
    }

    override fun resume() {
        resumeCallCount++
    }

    override fun pause() {
        pauseCallCount++
    }

    override fun next() {
        nextCallCount++
    }

    override fun previous() {
        previousCallCount++
    }

    override fun seekTo(positionMs: Long) {
        lastSeekPositionMs = positionMs
    }

    override fun setShuffleEnabled(enabled: Boolean) {
        lastShuffleEnabled = enabled
    }

    override fun setPlaybackSpeed(speed: Float) {
        lastPlaybackSpeed = speed
    }

    override fun stop() {
        stopCallCount++
    }
}
