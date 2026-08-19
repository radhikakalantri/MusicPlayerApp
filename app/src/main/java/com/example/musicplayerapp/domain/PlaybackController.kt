package com.example.musicplayerapp.domain

import com.example.musicplayerapp.data.model.Playable

/**
 * Abstraction the ViewModel depends on instead of talking to the Android
 * Service/Intent APIs directly. Production code wires this to
 * ServicePlaybackController; tests wire it to a lightweight fake — that's
 * what makes MusicViewModel unit-testable with plain JUnit.
 *
 * Operates on Playable (Song or Episode) so the same queue/shuffle/speed
 * logic works for music and podcasts alike.
 */
interface PlaybackController {
    /** Loads a fresh queue and starts playing the item at startIndex. */
    fun playQueue(items: List<Playable>, startIndex: Int)

    /** Jumps to an item already in the current queue (e.g. tapping "Up Next"). */
    fun playAtIndex(index: Int)

    fun resume()
    fun pause()
    fun next()
    fun previous()

    /** Seeks within the currently playing item. */
    fun seekTo(positionMs: Long)

    fun setShuffleEnabled(enabled: Boolean)

    /** 0.5x–2x style playback speed. */
    fun setPlaybackSpeed(speed: Float)

    fun stop()
}
