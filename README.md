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
- **Your Library** — Liked Songs entry + all playlists
- **Playlist detail** — cover, description, and its songs
- **Movie detail** — a soundtrack's cover + every song tagged to it
- **Now Playing** — full-screen player with blurred album-art background, gradient play/pause + like button
- **Mini-player** — persistent bar above the bottom nav, opens Now Playing

> Search-by-movie works against `movieTitle` tags on the sample songs (original/fictional soundtrack names, not real films) — real, licensed catalog metadata plugs into the same field with no UI changes.

## Architecture

```
data/          Song, Playlist, Artist, Movie models + MusicRepository (swap in Retrofit/Room later)
domain/        GetSongsUseCase, GetPlaylistsUseCase, GetArtistsUseCase, GetMoviesUseCase, GetSongsForPlaylistUseCase
               PlaybackController (interface — dependency inversion for playback)
service/       MusicPlayerService (ExoPlayer, foreground service)
               ServicePlaybackController (implements PlaybackController via Intents)
               PlaybackManager (StateFlow bridge between service & UI)
presentation/  MusicViewModel (plain ViewModel — no Android deps, fully unit-testable)
               MusicViewModelFactory (manual DI wiring)
               screens/ (Home, Search, Library, PlaylistDetail, Player)
               components/ (SongItem, PlaylistCard, MiniPlayer, BottomNavBar, GradientBackground)
               theme/, navigation/ (NavGraph)
```

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
- **Colors**: `presentation/theme/Color.kt` (green/black + genre tile colors).
- **New playback behaviors** (shuffle, queue, skip): add methods to the `PlaybackController` interface, implement in `ServicePlaybackController`, and they're immediately testable via `FakePlaybackController`.
