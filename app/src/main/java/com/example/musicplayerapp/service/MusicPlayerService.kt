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
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicplayerapp.MainActivity
import com.example.musicplayerapp.R
import com.example.musicplayerapp.data.model.Playable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Foreground service that owns the ExoPlayer instance and publishes
 * playback progress via coroutines/Flow (PlaybackManager) so the Compose
 * UI stays reactive without holding a service reference.
 *
 * Handles: queue playback, next/previous (with a small history stack so
 * "previous" is sane under shuffle), seeking, shuffle toggling, playback
 * speed, and auto-advance when a track finishes.
 */
class MusicPlayerService : Service() {

    private var exoPlayer: ExoPlayer? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null

    /** Indices we've played, most recent last — lets "previous" undo shuffle jumps too. */
    private val playHistory = mutableListOf<Int>()

    companion object {
        const val ACTION_PLAY_QUEUE = "com.example.musicplayerapp.ACTION_PLAY_QUEUE"
        const val ACTION_RESUME = "com.example.musicplayerapp.ACTION_RESUME"
        const val ACTION_PAUSE = "com.example.musicplayerapp.ACTION_PAUSE"
        const val ACTION_NEXT = "com.example.musicplayerapp.ACTION_NEXT"
        const val ACTION_PREVIOUS = "com.example.musicplayerapp.ACTION_PREVIOUS"
        const val ACTION_SEEK = "com.example.musicplayerapp.ACTION_SEEK"
        const val ACTION_SET_SHUFFLE = "com.example.musicplayerapp.ACTION_SET_SHUFFLE"
        const val ACTION_SET_SPEED = "com.example.musicplayerapp.ACTION_SET_SPEED"
        const val ACTION_STOP = "com.example.musicplayerapp.ACTION_STOP"

        const val EXTRA_INDEX = "extra_index"
        const val EXTRA_POSITION_MS = "extra_position_ms"
        const val EXTRA_SHUFFLE = "extra_shuffle"
        const val EXTRA_SPEED = "extra_speed"

        private const val CHANNEL_ID = "music_playback_channel"
        private const val NOTIFICATION_ID = 101
        private const val RESTART_THRESHOLD_MS = 3000L
    }

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        advance()
                    }
                }
            })
        }
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_QUEUE -> handlePlayQueue(intent)
            ACTION_RESUME -> handleResume()
            ACTION_PAUSE -> handlePause()
            ACTION_NEXT -> advance()
            ACTION_PREVIOUS -> handlePrevious()
            ACTION_SEEK -> handleSeek(intent)
            ACTION_SET_SHUFFLE -> handleSetShuffle(intent)
            ACTION_SET_SPEED -> handleSetSpeed(intent)
            ACTION_STOP -> handleStop()
        }
        return START_STICKY
    }

    private fun handlePlayQueue(intent: Intent) {
        val index = intent.getIntExtra(EXTRA_INDEX, 0)
        playHistory.clear()
        val queue = PlaybackManager.state.value.queue
        loadAndPlay(queue, index)
    }

    private fun handleResume() {
        exoPlayer?.play()
        val current = PlaybackManager.state.value
        PlaybackManager.update(current.copy(isPlaying = true))
        current.currentItem?.let { startForeground(NOTIFICATION_ID, buildNotification(it, isPlaying = true)) }
        startProgressUpdates()
    }

    private fun handlePause() {
        exoPlayer?.pause()
        val current = PlaybackManager.state.value
        PlaybackManager.update(current.copy(isPlaying = false))
        current.currentItem?.let { startForeground(NOTIFICATION_ID, buildNotification(it, isPlaying = false)) }
        progressJob?.cancel()
    }

    private fun advance() {
        val current = PlaybackManager.state.value
        val queue = current.queue
        if (queue.isEmpty() || current.currentIndex < 0) return

        playHistory.add(current.currentIndex)

        val nextIndex = if (current.isShuffleEnabled) {
            randomIndexExcluding(queue.size, current.currentIndex)
        } else if (current.currentIndex + 1 < queue.size) {
            current.currentIndex + 1
        } else {
            0
        }
        loadAndPlay(queue, nextIndex)
    }

    private fun handlePrevious() {
        val current = PlaybackManager.state.value
        val queue = current.queue
        if (queue.isEmpty()) return

        // Standard player behavior: more than a few seconds in, "previous" restarts the track.
        if ((exoPlayer?.currentPosition ?: 0L) > RESTART_THRESHOLD_MS) {
            exoPlayer?.seekTo(0)
            PlaybackManager.update(current.copy(currentPositionMs = 0L))
            return
        }

        val previousIndex = when {
            playHistory.isNotEmpty() -> playHistory.removeAt(playHistory.size - 1)
            current.isShuffleEnabled -> randomIndexExcluding(queue.size, current.currentIndex)
            current.currentIndex - 1 >= 0 -> current.currentIndex - 1
            else -> queue.size - 1
        }
        loadAndPlay(queue, previousIndex)
    }

    private fun randomIndexExcluding(size: Int, exclude: Int): Int {
        if (size <= 1) return 0
        var index: Int
        do {
            index = Random.nextInt(size)
        } while (index == exclude)
        return index
    }

    private fun handleSeek(intent: Intent) {
        val positionMs = intent.getLongExtra(EXTRA_POSITION_MS, 0L)
        exoPlayer?.seekTo(positionMs)
        PlaybackManager.update(PlaybackManager.state.value.copy(currentPositionMs = positionMs))
    }

    private fun handleSetShuffle(intent: Intent) {
        val enabled = intent.getBooleanExtra(EXTRA_SHUFFLE, false)
        PlaybackManager.update(PlaybackManager.state.value.copy(isShuffleEnabled = enabled))
    }

    private fun handleSetSpeed(intent: Intent) {
        val speed = intent.getFloatExtra(EXTRA_SPEED, 1f)
        exoPlayer?.setPlaybackSpeed(speed)
        PlaybackManager.update(PlaybackManager.state.value.copy(playbackSpeed = speed))
    }

    private fun handleStop() {
        exoPlayer?.stop()
        playHistory.clear()
        PlaybackManager.update(PlaybackState())
        progressJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun loadAndPlay(queue: List<Playable>, index: Int) {
        val item = queue.getOrNull(index) ?: return
        val speed = PlaybackManager.state.value.playbackSpeed

        exoPlayer?.setMediaItem(MediaItem.fromUri(item.url))
        exoPlayer?.prepare()
        exoPlayer?.setPlaybackSpeed(speed)
        exoPlayer?.play()

        startForeground(NOTIFICATION_ID, buildNotification(item, isPlaying = true))
        PlaybackManager.update(
            PlaybackManager.state.value.copy(
                queue = queue,
                currentIndex = index,
                isPlaying = true,
                currentPositionMs = 0L,
                durationMs = 0L
            )
        )
        startProgressUpdates()
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

    private fun buildNotification(item: Playable, isPlaying: Boolean): Notification {
        val contentIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(item.title)
            .setContentText(item.subtitle)
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
