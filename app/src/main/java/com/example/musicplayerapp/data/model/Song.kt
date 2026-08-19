package com.example.musicplayerapp.data.model

data class Song(
    val id: Int,
    val title: String,
    val artist: String,
    val url: String,
    val imageUrl: String,
    val accentColorHex: String,
    /** Optional soundtrack tag — e.g. the movie this track appears in. Null for standalone singles. */
    val movieTitle: String? = null
)
