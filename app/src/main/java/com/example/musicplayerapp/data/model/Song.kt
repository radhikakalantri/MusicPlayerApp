package com.example.musicplayerapp.data.model

data class Song(
    override val id: Int,
    override val title: String,
    val artist: String,
    override val url: String,
    override val imageUrl: String,
    override val accentColorHex: String,
    /** Optional soundtrack tag — e.g. the movie this track appears in. Null for standalone singles. */
    val movieTitle: String? = null
) : Playable {
    override val subtitle: String get() = artist
}
