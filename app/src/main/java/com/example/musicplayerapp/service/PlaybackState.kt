package com.example.musicplayerapp.service

import com.example.musicplayerapp.data.model.Playable

data class PlaybackState(
    val queue: List<Playable> = emptyList(),
    val currentIndex: Int = -1,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val isShuffleEnabled: Boolean = false,
    val playbackSpeed: Float = 1f
) {
    val currentItem: Playable? get() = queue.getOrNull(currentIndex)
}
