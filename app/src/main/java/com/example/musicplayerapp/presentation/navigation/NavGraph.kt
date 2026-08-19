package com.example.musicplayerapp.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.musicplayerapp.presentation.components.BottomNavBar
import com.example.musicplayerapp.presentation.components.MiniPlayer
import com.example.musicplayerapp.presentation.screens.HomeScreen
import com.example.musicplayerapp.presentation.screens.LibraryScreen
import com.example.musicplayerapp.presentation.screens.MovieDetailScreen
import com.example.musicplayerapp.presentation.screens.PlayerScreen
import com.example.musicplayerapp.presentation.screens.PlaylistDetailScreen
import com.example.musicplayerapp.presentation.screens.PodcastDetailScreen
import com.example.musicplayerapp.presentation.screens.PodcastsScreen
import com.example.musicplayerapp.presentation.screens.QueueScreen
import com.example.musicplayerapp.presentation.screens.SearchScreen
import com.example.musicplayerapp.presentation.theme.SpotifyDarkGray
import com.example.musicplayerapp.presentation.viewmodel.MusicViewModel
import java.net.URLDecoder
import java.net.URLEncoder

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Podcasts : Screen("podcasts")
    data object Library : Screen("library")
    data object Player : Screen("player")
    data object Queue : Screen("queue")
    data object PlaylistDetail : Screen("playlist/{playlistId}") {
        fun routeFor(playlistId: Int) = "playlist/$playlistId"
    }
    data object MovieDetail : Screen("movie/{movieTitle}") {
        fun routeFor(movieTitle: String) = "movie/${URLEncoder.encode(movieTitle, "UTF-8")}"
    }
    data object PodcastDetail : Screen("podcast/{podcastTitle}") {
        fun routeFor(podcastTitle: String) = "podcast/${URLEncoder.encode(podcastTitle, "UTF-8")}"
    }
}

private val bottomBarRoutes = setOf(Screen.Home.route, Screen.Search.route, Screen.Podcasts.route, Screen.Library.route)

@Composable
fun MusicNavGraph(viewModel: MusicViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val playback by viewModel.playbackState.collectAsState()

    Scaffold(
        containerColor = SpotifyDarkGray,
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                Column {
                    MiniPlayer(
                        playbackState = playback,
                        onTogglePlayPause = { viewModel.togglePlayPause() },
                        onSkipNext = { viewModel.skipNext() },
                        onClick = { navController.navigate(Screen.Player.route) }
                    )
                    BottomNavBar(currentRoute = currentRoute) { screen ->
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                            popUpTo(Screen.Home.route) { saveState = true }
                            restoreState = true
                        }
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onPlaylistClick = { playlist ->
                        navController.navigate(Screen.PlaylistDetail.routeFor(playlist.id))
                    },
                    onSongClick = { song ->
                        viewModel.playSong(song)
                        navController.navigate(Screen.Player.route)
                    }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = viewModel,
                    onSongClick = { song ->
                        viewModel.playSong(song)
                        navController.navigate(Screen.Player.route)
                    },
                    onMovieClick = { movie ->
                        navController.navigate(Screen.MovieDetail.routeFor(movie.title))
                    }
                )
            }
            composable(Screen.Podcasts.route) {
                PodcastsScreen(
                    viewModel = viewModel,
                    onPodcastClick = { podcast ->
                        navController.navigate(Screen.PodcastDetail.routeFor(podcast.title))
                    }
                )
            }
            composable(Screen.Library.route) {
                LibraryScreen(
                    viewModel = viewModel,
                    onPlaylistClick = { playlist ->
                        navController.navigate(Screen.PlaylistDetail.routeFor(playlist.id))
                    },
                    onLikedSongsClick = { /* future: dedicated liked-songs screen */ }
                )
            }
            composable(Screen.Player.route) {
                PlayerScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onOpenQueue = { navController.navigate(Screen.Queue.route) }
                )
            }
            composable(Screen.Queue.route) {
                QueueScreen(viewModel = viewModel) { navController.popBackStack() }
            }
            composable(
                route = Screen.PlaylistDetail.route,
                arguments = listOf(navArgument("playlistId") { type = NavType.IntType })
            ) { entry ->
                val playlistId = entry.arguments?.getInt("playlistId") ?: -1
                val playlists by viewModel.playlists.collectAsState()
                val playlist = playlists.firstOrNull { it.id == playlistId }
                if (playlist != null) {
                    val playlistSongs = viewModel.songsForPlaylist(playlist)
                    PlaylistDetailScreen(
                        playlist = playlist,
                        songs = playlistSongs,
                        onBack = { navController.popBackStack() },
                        onSongClick = { song ->
                            viewModel.playSong(song, playlistSongs)
                            navController.navigate(Screen.Player.route)
                        }
                    )
                }
            }
            composable(
                route = Screen.MovieDetail.route,
                arguments = listOf(navArgument("movieTitle") { type = NavType.StringType })
            ) { entry ->
                val encodedTitle = entry.arguments?.getString("movieTitle") ?: ""
                val movieTitle = URLDecoder.decode(encodedTitle, "UTF-8")
                val movies by viewModel.movies.collectAsState()
                val movie = movies.firstOrNull { it.title == movieTitle }
                if (movie != null) {
                    val movieSongs = viewModel.songsForMovie(movie)
                    MovieDetailScreen(
                        movie = movie,
                        songs = movieSongs,
                        onBack = { navController.popBackStack() },
                        onSongClick = { song ->
                            viewModel.playSong(song, movieSongs)
                            navController.navigate(Screen.Player.route)
                        }
                    )
                }
            }
            composable(
                route = Screen.PodcastDetail.route,
                arguments = listOf(navArgument("podcastTitle") { type = NavType.StringType })
            ) { entry ->
                val encodedTitle = entry.arguments?.getString("podcastTitle") ?: ""
                val podcastTitle = URLDecoder.decode(encodedTitle, "UTF-8")
                val podcasts by viewModel.podcasts.collectAsState()
                val podcast = podcasts.firstOrNull { it.title == podcastTitle }
                if (podcast != null) {
                    val podcastEpisodes = viewModel.episodesForPodcast(podcast)
                    PodcastDetailScreen(
                        podcast = podcast,
                        episodes = podcastEpisodes,
                        onBack = { navController.popBackStack() },
                        onEpisodeClick = { episode ->
                            viewModel.playEpisode(episode, podcastEpisodes)
                            navController.navigate(Screen.Player.route)
                        }
                    )
                }
            }
        }
    }
}
