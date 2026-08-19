package com.example.musicplayerapp.service

import android.app.Application
import android.content.Intent
import com.example.musicplayerapp.data.model.Song
import com.example.musicplayerapp.domain.PlaybackController

/**
 * Real implementation of PlaybackController: turns playback intents into
 * Intents sent to MusicPlayerService. All Android-framework-specific
 * playback code lives here, out of the ViewModel.
 */
class ServicePlaybackController(private val application: Application) : PlaybackController {

    override fun play(song: Song) {
        val intent = Intent(application, MusicPlayerService::class.java).apply {
            action = MusicPlayerService.ACTION_PLAY
            putExtra(MusicPlayerService.EXTRA_SONG_ID, song.id)
            putExtra(MusicPlayerService.EXTRA_SONG_TITLE, song.title)
            putExtra(MusicPlayerService.EXTRA_SONG_ARTIST, song.artist)
            putExtra(MusicPlayerService.EXTRA_SONG_URL, song.url)
            putExtra(MusicPlayerService.EXTRA_SONG_IMAGE, song.imageUrl)
            putExtra(MusicPlayerService.EXTRA_SONG_COLOR, song.accentColorHex)
        }
        application.startForegroundService(intent)
    }

    override fun pause() {
        application.startService(
            Intent(application, MusicPlayerService::class.java).apply {
                action = MusicPlayerService.ACTION_PAUSE
            }
        )
    }

    override fun stop() {
        application.startService(
            Intent(application, MusicPlayerService::class.java).apply {
                action = MusicPlayerService.ACTION_STOP
            }
        )
    }
}
