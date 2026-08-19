package com.example.musicplayerapp.service

import com.example.musicplayerapp.data.model.Playable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Bridges the Service (which owns the actual player) and the ViewModel
 * (which the Compose UI observes) using a shared StateFlow. This keeps
 * playback state as a single source of truth without tight coupling.
 *
 * Living in the same process as the Service, the queue itself is stored
 * here directly (no Intent-extras serialization needed) — the Service
 * reads PlaybackManager.state.value.queue whenever it needs to know what
 * to play next.
 */
object PlaybackManager {
    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state

    fun update(newState: PlaybackState) {
        _state.value = newState
    }

    fun setQueue(items: List<Playable>, startIndex: Int) {
        _state.value = _state.value.copy(queue = items, currentIndex = startIndex)
    }
}
