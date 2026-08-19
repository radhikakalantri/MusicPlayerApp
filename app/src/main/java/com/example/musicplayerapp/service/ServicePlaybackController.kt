package com.example.musicplayerapp.service

import android.app.Application
import android.content.Intent
import com.example.musicplayerapp.data.model.Playable
import com.example.musicplayerapp.domain.PlaybackController

/**
 * Real implementation of PlaybackController: turns playback intents into
 * Intents sent to MusicPlayerService. All Android-framework-specific
 * playback code lives here, out of the ViewModel.
 */
class ServicePlaybackController(private val application: Application) : PlaybackController {

    override fun playQueue(items: List<Playable>, startIndex: Int) {
        PlaybackManager.setQueue(items, startIndex)
        sendForegroundAction(MusicPlayerService.ACTION_PLAY_QUEUE) {
            putExtra(MusicPlayerService.EXTRA_INDEX, startIndex)
        }
    }

    override fun playAtIndex(index: Int) {
        sendForegroundAction(MusicPlayerService.ACTION_PLAY_QUEUE) {
            putExtra(MusicPlayerService.EXTRA_INDEX, index)
        }
    }

    override fun resume() = sendForegroundAction(MusicPlayerService.ACTION_RESUME)

    override fun pause() = sendAction(MusicPlayerService.ACTION_PAUSE)

    override fun next() = sendForegroundAction(MusicPlayerService.ACTION_NEXT)

    override fun previous() = sendForegroundAction(MusicPlayerService.ACTION_PREVIOUS)

    override fun seekTo(positionMs: Long) {
        sendAction(MusicPlayerService.ACTION_SEEK) {
            putExtra(MusicPlayerService.EXTRA_POSITION_MS, positionMs)
        }
    }

    override fun setShuffleEnabled(enabled: Boolean) {
        sendAction(MusicPlayerService.ACTION_SET_SHUFFLE) {
            putExtra(MusicPlayerService.EXTRA_SHUFFLE, enabled)
        }
    }

    override fun setPlaybackSpeed(speed: Float) {
        sendAction(MusicPlayerService.ACTION_SET_SPEED) {
            putExtra(MusicPlayerService.EXTRA_SPEED, speed)
        }
    }

    override fun stop() = sendAction(MusicPlayerService.ACTION_STOP)

    /** For actions that (re)start playback and must post a foreground notification promptly. */
    private fun sendForegroundAction(action: String, configure: Intent.() -> Unit = {}) {
        val intent = Intent(application, MusicPlayerService::class.java).apply {
            this.action = action
            configure()
        }
        application.startForegroundService(intent)
    }

    /** For control actions sent while the service is already running (pause, seek, shuffle, speed, stop). */
    private fun sendAction(action: String, configure: Intent.() -> Unit = {}) {
        val intent = Intent(application, MusicPlayerService::class.java).apply {
            this.action = action
            configure()
        }
        application.startService(intent)
    }
}
