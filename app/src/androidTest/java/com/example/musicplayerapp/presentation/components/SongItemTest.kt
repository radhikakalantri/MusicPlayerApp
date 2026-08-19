package com.example.musicplayerapp.presentation.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.musicplayerapp.data.model.Song
import org.junit.Rule
import org.junit.Test

class SongItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val song = Song(
        id = 1,
        title = "Test Song",
        artist = "Test Artist",
        url = "https://example.com/song.mp3",
        imageUrl = "https://example.com/cover.jpg",
        accentColorHex = "#FFFFFF"
    )

    @Test
    fun songItem_displaysTitleAndArtist() {
        composeTestRule.setContent {
            SongItem(song = song, onClick = {})
        }

        composeTestRule.onNodeWithText("Test Song").assertExists()
        composeTestRule.onNodeWithText("Test Artist").assertExists()
    }

    @Test
    fun songItem_invokesOnClick_whenTapped() {
        var clicked = false

        composeTestRule.setContent {
            SongItem(song = song, onClick = { clicked = true })
        }

        composeTestRule.onNodeWithText("Test Song").performClick()

        assert(clicked) { "Expected onClick to be invoked after tapping the song row" }
    }
}
