package com.example.musicplayerapp.data.repository

import com.example.musicplayerapp.data.model.Artist
import com.example.musicplayerapp.data.model.Episode
import com.example.musicplayerapp.data.model.Movie
import com.example.musicplayerapp.data.model.Playlist
import com.example.musicplayerapp.data.model.Podcast
import com.example.musicplayerapp.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * In-memory catalog + playlists + soundtrack tagging. Swap this out for a
 * Retrofit/Room-backed implementation (or a licensed music API) later
 * without touching domain or presentation layers — they only ever depend
 * on the MusicRepository interface.
 *
 * movieTitle is an original/fictional soundtrack tag for demo purposes —
 * plug in real, licensed catalog metadata here when you have one.
 */
class MusicRepositoryImpl : MusicRepository {

    private val songs = listOf(
        Song(1, "Sunset Drive", "Wave Riders", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", "https://picsum.photos/seed/sunsetdrive/600/600", "#FF6B6B", movieTitle = "Neon Horizon"),
        Song(2, "Neon Nights", "Astra Beats", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3", "https://picsum.photos/seed/neonnights/600/600", "#845EC2", movieTitle = "Neon Horizon"),
        Song(3, "Ocean Breeze", "Coral Sound", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3", "https://picsum.photos/seed/oceanbreeze/600/600", "#00C9A7", movieTitle = "Ocean's Edge"),
        Song(4, "Golden Hour", "Lumen", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3", "https://picsum.photos/seed/goldenhour/600/600", "#FFC75F", movieTitle = null),
        Song(5, "Midnight City", "Nocturn", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3", "https://picsum.photos/seed/midnightcity/600/600", "#4D8076", movieTitle = "City of Echoes"),
        Song(6, "Electric Dreams", "Voltage", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3", "https://picsum.photos/seed/electricdreams/600/600", "#F9F871", movieTitle = "Neon Horizon"),
        Song(7, "Morning Coffee", "Lumen", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3", "https://picsum.photos/seed/morningcoffee/600/600", "#FF9671", movieTitle = null),
        Song(8, "City Lights", "Astra Beats", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3", "https://picsum.photos/seed/citylights/600/600", "#D65DB1", movieTitle = "City of Echoes"),
        Song(9, "Heavy Lift", "Ironclad", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3", "https://picsum.photos/seed/heavylift/600/600", "#FF6F91", movieTitle = "Iron Season"),
        Song(10, "Runner's High", "Ironclad", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3", "https://picsum.photos/seed/runnershigh/600/600", "#FFC75F", movieTitle = "Iron Season"),
        Song(11, "Old Cassette", "Nocturn", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-11.mp3", "https://picsum.photos/seed/oldcassette/600/600", "#845EC2", movieTitle = "City of Echoes"),
        Song(12, "Rewind", "Wave Riders", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-12.mp3", "https://picsum.photos/seed/rewind/600/600", "#00C9A7", movieTitle = "Ocean's Edge"),
        Song(13, "Deep Focus", "Coral Sound", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-13.mp3", "https://picsum.photos/seed/deepfocus/600/600", "#4D8076", movieTitle = null),
        Song(14, "Study Session", "Lumen", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-14.mp3", "https://picsum.photos/seed/studysession/600/600", "#6C5CE7", movieTitle = null)
    )

    private val playlists = listOf(
        Playlist(1, "Daily Mix 1", "Your favorite mix, refreshed daily", "https://picsum.photos/seed/dailymix1/600/600", listOf(1, 2, 3, 4)),
        Playlist(2, "Chill Hits", "Kick back and relax", "https://picsum.photos/seed/chillhits/600/600", listOf(3, 5, 13, 14)),
        Playlist(3, "Workout Beats", "Energy for your next session", "https://picsum.photos/seed/workoutbeats/600/600", listOf(9, 10, 6, 8)),
        Playlist(4, "Throwback", "The ones you never skip", "https://picsum.photos/seed/throwback/600/600", listOf(11, 12, 7)),
        Playlist(5, "Focus Flow", "Deep work, no distractions", "https://picsum.photos/seed/focusflow/600/600", listOf(13, 14, 5)),
        Playlist(6, "Party Anthems", "Turn it up", "https://picsum.photos/seed/partyanthems/600/600", listOf(2, 6, 8, 9))
    )

    private val episodes = listOf(
        Episode(101, "Building Better Habits", "Mindset Weekly", "Jordan Lee", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3", "https://picsum.photos/seed/mindsetweekly1/600/600", "#1DB954", "32 min"),
        Episode(102, "The Focus Trap", "Mindset Weekly", "Jordan Lee", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3", "https://picsum.photos/seed/mindsetweekly2/600/600", "#1DB954", "28 min"),
        Episode(103, "Rest as Strategy", "Mindset Weekly", "Jordan Lee", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3", "https://picsum.photos/seed/mindsetweekly3/600/600", "#1DB954", "35 min"),
        Episode(104, "Indie Devs to Watch", "Tech Unfiltered", "Priya Nair", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3", "https://picsum.photos/seed/techunfiltered1/600/600", "#509BF5", "41 min"),
        Episode(105, "The AI Hype Cycle", "Tech Unfiltered", "Priya Nair", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3", "https://picsum.photos/seed/techunfiltered2/600/600", "#509BF5", "38 min"),
        Episode(106, "Shipping Fast, Breaking Things", "Tech Unfiltered", "Priya Nair", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3", "https://picsum.photos/seed/techunfiltered3/600/600", "#509BF5", "44 min")
    )

    override fun getSongs(): Flow<List<Song>> = flow {
        emit(songs)
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        emit(playlists)
    }

    override fun getArtists(): Flow<List<Artist>> = flow {
        val artists = songs
            .groupBy { it.artist }
            .map { (name, artistSongs) ->
                Artist(
                    name = name,
                    imageUrl = artistSongs.first().imageUrl,
                    songCount = artistSongs.size
                )
            }
            .sortedBy { it.name }
        emit(artists)
    }

    override fun getMovies(): Flow<List<Movie>> = flow {
        val movies = songs
            .filter { it.movieTitle != null }
            .groupBy { it.movieTitle!! }
            .map { (title, movieSongs) ->
                Movie(
                    title = title,
                    coverUrl = movieSongs.first().imageUrl,
                    songIds = movieSongs.map { it.id }
                )
            }
            .sortedBy { it.title }
        emit(movies)
    }

    override fun getPodcasts(): Flow<List<Podcast>> = flow {
        val podcasts = episodes
            .groupBy { it.podcastTitle }
            .map { (title, showEpisodes) ->
                Podcast(
                    title = title,
                    host = showEpisodes.first().host,
                    coverUrl = showEpisodes.first().imageUrl,
                    episodeIds = showEpisodes.map { it.id }
                )
            }
            .sortedBy { it.title }
        emit(podcasts)
    }

    override fun getEpisodes(): Flow<List<Episode>> = flow {
        emit(episodes)
    }
}
