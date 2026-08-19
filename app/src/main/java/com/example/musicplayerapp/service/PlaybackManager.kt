package com.example.musicplayerapp.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Bridges the Service (which owns the actual player) and the ViewModel
 * (which the Compose UI observes) using a shared StateFlow. This keeps
 * playback state as a single source of truth without tight coupling.
 */
object PlaybackManager {
    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state

    fun update(newState: PlaybackState) {
        _state.value = newState
    }
}
