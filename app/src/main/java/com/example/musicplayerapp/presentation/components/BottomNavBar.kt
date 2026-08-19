package com.example.musicplayerapp.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.musicplayerapp.presentation.navigation.Screen
import com.example.musicplayerapp.presentation.theme.SpotifyDarkGray
import com.example.musicplayerapp.presentation.theme.SpotifyGreen

private data class NavItem(val screen: Screen, val label: String)

private val items = listOf(
    NavItem(Screen.Home, "Home"),
    NavItem(Screen.Search, "Search"),
    NavItem(Screen.Podcasts, "Podcasts"),
    NavItem(Screen.Library, "Your Library")
)

@Composable
fun BottomNavBar(currentRoute: String?, onNavigate: (Screen) -> Unit) {
    NavigationBar(containerColor = SpotifyDarkGray) {
        items.forEach { item ->
            val selected = currentRoute == item.screen.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.screen) },
                icon = {
                    Icon(
                        imageVector = when (item.screen) {
                            Screen.Home -> Icons.Filled.Home
                            Screen.Search -> Icons.Filled.Search
                            Screen.Podcasts -> Icons.Filled.Headset
                            Screen.Library -> Icons.Filled.LibraryMusic
                            else -> Icons.Filled.Home
                        },
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = SpotifyGreen,
                    indicatorColor = SpotifyGreen,
                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                    unselectedTextColor = Color.White.copy(alpha = 0.6f)
                )
            )
        }
    }
}
