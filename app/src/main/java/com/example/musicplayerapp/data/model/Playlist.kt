package com.example.musicplayerapp.data.model

data class Playlist(
    val id: Int,
    val name: String,
    val description: String,
    val coverUrl: String,
    val songIds: List<Int>
)
