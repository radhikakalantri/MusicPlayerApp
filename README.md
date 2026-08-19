# 🎵 Music Player — Jetpack Compose · MVVM · Coroutines/Flow

A Spotify-style Android music player: bottom navigation, colorful playlist
covers, a persistent mini-player, search with genre tiles, and a full
"Now Playing" screen — built on Kotlin, Jetpack Compose, coroutines/Flow,
and a testable clean-MVVM layering.

> Note: this uses royalty-free sample tracks/artwork, not Spotify's actual
> catalog — that's licensed content I can't include. Swap `MusicRepositoryImpl`
> for your own API/catalog whenever you're ready; nothing above the data
> layer needs to change.

## Screens

- **Home** — greeting, "Shuffle Play" CTA, horizontal row of playlists, "Popular right now" list
- **Search** — text search across song title, artist, and movie/soundtrack name; shows a colorful genre grid when empty, and "From Movies" / "Artists" / "Songs" result sections when typing
- **Podcasts** — grid of shows → tap into a **Podcast detail** screen listing episodes
- **Your Library** — Liked Songs entry + all playlists
- **Playlist detail** — cover, description, and its songs (playing here queues just that playlist)
- **Movie detail** — a soundtrack's cover + every song tagged to it (queues just that soundtrack)
- **Now Playing** — full-screen player: seekable progress bar, shuffle toggle, previous/next, gradient play/pause, like, and a 0.5x–2x playback speed picker
- **Up Next (Queue)** — the whole current queue with the playing item highlighted; tap any row to jump to it
- **Mini-player** — persistent bar above the bottom nav with play/pause + skip-next, opens Now Playing on tap

> Search-by-movie and podcasts both work against sample data (original/fictional soundtrack and show names, not real films or shows) — real, licensed catalog metadata plugs into the same fields with no UI changes.

## Playback features

- **Queue-based playback** — playing any song/episode loads a queue (the playlist, movie, podcast, or full catalog it came from) so next/previous stay contextual
- **Next / Previous** — previous restarts the current track if you're more than 3s in (standard player behavior), otherwise steps back; a small history stack makes "previous" sane even under shuffle
- **Shuffle** — toggle on the Now Playing screen; next/previous pick randomly within the current queue while enabled
- **Seekable progress bar** — drag to scrub to any point in the track
- **Playback speed** — 0.5x, 0.75x, 1x, 1.25x, 1.5x, 2x, applied live via ExoPlayer
- **Auto-advance** — the next queue item starts automatically when the current one finishes
- **Podcasts** — episodes are just another `Playable`, so they get the exact same queue/shuffle/speed/seek controls as songs

## Architecture

```
data/          Song, Playlist, Artist, Movie, Episode, Podcast models (Song & Episode implement Playable)
                MusicRepository (swap in Retrofit/Room later)
domain/        GetSongsUseCase, GetPlaylistsUseCase, GetArtistsUseCase, GetMoviesUseCase,
                GetPodcastsUseCase, GetEpisodesUseCase, GetSongsForPlaylistUseCase
                PlaybackController (interface — queue/next/previous/seek/shuffle/speed, dependency-inverted)
service/       MusicPlayerService (ExoPlayer, foreground service, owns the queue/history/auto-advance)
               ServicePlaybackController (implements PlaybackController via Intents)
               PlaybackManager (StateFlow bridge + in-memory queue, shared between Service and ViewModel)
presentation/  MusicViewModel (plain ViewModel — no Android deps, fully unit-testable)
               MusicViewModelFactory (manual DI wiring)
               screens/ (Home, Search, Podcasts, PodcastDetail, Library, PlaylistDetail, MovieDetail, Player, Queue)
               components/ (SongItem, EpisodeItem, PlaylistCard, MovieCard, PodcastCard, ArtistCard,
                             MiniPlayer, BottomNavBar, GradientButton, GradientIconButton)
               theme/, navigation/ (NavGraph)
```

`Playable` is the common interface `Song` and `Episode` both implement (id, title, subtitle, url, imageUrl,
accentColorHex) — it's what lets the queue, shuffle, seek, speed, and player UI work identically for music
and podcasts without duplicating any playback logic.

`MusicViewModel` depends only on use cases and the `PlaybackController`
interface — never on `Context`/`Intent`/`Service` directly. That's what lets
it be tested with plain JUnit + fakes, no Robolectric/emulator needed.

## Tests

```
app/src/test/            JVM unit tests (run with ./gradlew test)
  data/repository/MusicRepositoryImplTest.kt   catalog integrity (unique ids, non-blank fields, valid playlist references)
  domain/usecase/GetSongsForPlaylistUseCaseTest.kt   pure filter logic
  presentation/viewmodel/MusicViewModelTest.kt   loading, search, play/pause/stop, playlist filtering — via FakeMusicRepository + FakePlaybackController
  fakes/                                          FakeMusicRepository, FakePlaybackController
  util/MainDispatcherRule.kt                       swaps Dispatchers.Main for tests

app/src/androidTest/      Instrumented Compose UI tests (run on a device/emulator)
  presentation/components/SongItemTest.kt          renders title/artist, click callback fires
```

Run unit tests from the command line:

```bash
./gradlew test
```

Run instrumented UI tests (needs a connected device or emulator):

```bash
./gradlew connectedAndroidTest
```

## Requirements

- Android Studio Koala (2024.1.1) or newer
- JDK 17
- Min SDK 24, Target/Compile SDK 34

## Run it

1. Open the project root (`MusicPlayerApp/`) in Android Studio.
2. Let Gradle sync.
3. Run on a device/emulator with internet access (sample tracks/art stream from the network).

## Push to GitHub

```bash
cd MusicPlayerApp
git init
git add .
git commit -m "Spotify-style Compose MVVM music player with tests"
git branch -M main
git remote add origin https://github.com/<your-username>/<your-repo>.git
git push -u origin main
```

## Customizing

- **Real songs**: replace the list in `data/repository/MusicRepositoryImpl.kt`, or point `MusicRepository` at a real API/Room database — the rest of the app is unaffected.
- **Colors**: `presentation/theme/Color.kt` (Spotify green/black + genre tile colors).
- **New playback behaviors** (shuffle, queue, skip): add methods to the `PlaybackController` interface, implement in `ServicePlaybackController`, and they're immediately testable via `FakePlaybackController`.
