package com.example.musicplayerapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicplayerapp.MainActivity
import com.example.musicplayerapp.R
import com.example.musicplayerapp.data.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Foreground service that owns the ExoPlayer instance and publishes
 * playback progress via coroutines/Flow (PlaybackManager) so the
 * Compose UI stays reactive without holding a service reference.
 */
class MusicPlayerService : Service() {

    private var exoPlayer: ExoPlayer? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null

    companion object {
        const val ACTION_PLAY = "com.example.musicplayerapp.ACTION_PLAY"
        const val ACTION_PAUSE = "com.example.musicplayerapp.ACTION_PAUSE"
        const val ACTION_STOP = "com.example.musicplayerapp.ACTION_STOP"

        const val EXTRA_SONG_ID = "extra_song_id"
        const val EXTRA_SONG_TITLE = "extra_song_title"
        const val EXTRA_SONG_ARTIST = "extra_song_artist"
        const val EXTRA_SONG_URL = "extra_song_url"
        const val EXTRA_SONG_IMAGE = "extra_song_image"
        const val EXTRA_SONG_COLOR = "extra_song_color"

        private const val CHANNEL_ID = "music_playback_channel"
        private const val NOTIFICATION_ID = 101
    }

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).build()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> intent.let { handlePlay(it) }
            ACTION_PAUSE -> pause()
            ACTION_STOP -> stopPlayback()
        }
        return START_STICKY
    }

    private fun handlePlay(intent: Intent) {
        val url = intent.getStringExtra(EXTRA_SONG_URL) ?: return
        val song = Song(
            id = intent.getIntExtra(EXTRA_SONG_ID, 0),
            title = intent.getStringExtra(EXTRA_SONG_TITLE) ?: "",
            artist = intent.getStringExtra(EXTRA_SONG_ARTIST) ?: "",
            url = url,
            imageUrl = intent.getStringExtra(EXTRA_SONG_IMAGE) ?: "",
            accentColorHex = intent.getStringExtra(EXTRA_SONG_COLOR) ?: "#6C5CE7"
        )

        val currentState = PlaybackManager.state.value
        if (currentState.currentSong?.id != song.id) {
            exoPlayer?.setMediaItem(MediaItem.fromUri(url))
            exoPlayer?.prepare()
        }
        exoPlayer?.play()

        startForeground(NOTIFICATION_ID, buildNotification(song, isPlaying = true))
        PlaybackManager.update(currentState.copy(currentSong = song, isPlaying = true))
        startProgressUpdates()
    }

    private fun pause() {
        exoPlayer?.pause()
        val current = PlaybackManager.state.value
        PlaybackManager.update(current.copy(isPlaying = false))
        current.currentSong?.let { startForeground(NOTIFICATION_ID, buildNotification(it, isPlaying = false)) }
        progressJob?.cancel()
    }

    private fun stopPlayback() {
        exoPlayer?.stop()
        PlaybackManager.update(PlaybackState())
        progressJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = serviceScope.launch {
            while (isActive) {
                val player = exoPlayer
                if (player != null) {
                    val current = PlaybackManager.state.value
                    PlaybackManager.update(
                        current.copy(
                            currentPositionMs = player.currentPosition,
                            durationMs = player.duration.coerceAtLeast(0L)
                        )
                    )
                }
                delay(500)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(song: Song, isPlaying: Boolean): Notification {
        val contentIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentIntent(contentIntent)
            .setOngoing(isPlaying)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        progressJob?.cancel()
        exoPlayer?.release()
        serviceScope.cancel()
    }
}
