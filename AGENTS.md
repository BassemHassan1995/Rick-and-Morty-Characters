# AGENTS.md — Rick and Morty Characters App

## Project Overview
Kotlin/Jetpack Compose Android app consuming the [Rick and Morty API](https://rickandmortyapi.com/api/). Clean Architecture + UDF with offline-first caching via Paging 3 + Room RemoteMediator.

## Key Directory Map
```
app/src/main/java/bassem/task/characters/
├── data/
│   ├── local/          # Room DB: AppDatabase, entities, DAOs
│   ├── remote/         # Retrofit: CharacterApiService, DTOs, ApiException hierarchy
│   ├── mapper/         # Extension fns: CharacterDto/Entity → Domain
│   ├── mediator/       # CharacterRemoteMediator (Paging 3 cache coordinator)
│   └── repository/     # CharacterRepositoryImpl
├── domain/
│   ├── model/          # Pure Kotlin: Character, CharacterStatus
│   ├── repository/     # CharacterRepository interface
│   └── usecase/        # GetCharactersUseCase, GetCharacterByIdUseCase, ToggleFavoriteUseCase, ...
├── presentation/
│   ├── base/           # BaseViewModel<Event,State,Effect>, ViewContracts interfaces
│   ├── characterlist/  # CharacterListContract, CharacterListViewModel, CharacterListScreen
│   ├── characterdetails/
│   └── favoritelist/
├── di/                 # Hilt modules: NetworkModule, DatabaseModule, RepositoryModule
└── ui/
    ├── navigation/     # NavGraph.kt — Destinations object + AppNavGraph composable
    ├── components/     # Shared composables: BaseScaffold, SearchBar, CommonViews
    └── theme/
```

## UDF Contract Pattern
Every screen defines a `*Contract.kt` file with three sealed types, all extending base interfaces from `presentation/base/ViewContracts.kt`:
- `*State : ViewState` — immutable data class held in `StateFlow`
- `*Event : ViewEvent` — sealed interface for user/system actions
- `*Effect : ViewEffect` — one-time events (navigation, snackbars) sent via `Channel`

All ViewModels extend `BaseViewModel<Event, State, Effect>` and implement `onEvent(event)`. Update state via `setState { copy(...) }` and fire effects via `sendEffect { ... }`.

## Two Paging Strategies
| Mode | Implementation | Cache |
|------|---------------|-------|
| Browse (no query) | `CharacterRemoteMediator` + Room `PagingSource` | Room (offline-first) |
| Search (name query) | `CharacterSearchPagingSource` (network only) | None |

`CharacterRepositoryImpl.getCharacters(name)` switches between strategies based on whether `name` is blank.

## Caching Behaviour
- `RemoteMediator.initialize()` returns `SKIP_INITIAL_REFRESH` when the DB already has rows.
- On `REFRESH`, both `remoteKeysDao` and `characterDao` are cleared inside a Room transaction before inserting new data.
- `RemoteKeyEntity` stores `prevPage`/`nextPage` keyed by `characterId`.
- Favorites are stored in a separate `FavoriteEntity` table and joined via `CharacterEntityWithFavorite`.

## Error Handling
All network exceptions convert via `Exception.toApiException()` (`data/remote/utils/Extensions.kt`) into a typed hierarchy: `NotFoundException`, `TimeoutException`, `NetworkException`, `ClientException`, `ServerException`, `UnknownException` — all extending `ApiException`.

## Navigation
Destinations are string constants in `ui/navigation/NavGraph.kt` → `Destinations` object. Route for details: `"character_detail/{characterId}"` with `NavType.IntType`.

## Build & Test Commands
```bash
# Debug build
./gradlew assembleDebug

# Unit tests (fast)
./gradlew testDebugUnitTest

# Instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Install on connected device
./gradlew installDebug
```

## DI / Annotation Processors
Uses **KSP** (not KAPT) for both Hilt (`hilt-compiler`) and Moshi codegen (`moshi-kotlin-codegen`). Room also uses KSP (`room-compiler`). Do not switch to KAPT.

## Adding a New Screen
1. Create `presentation/<feature>/` with `<Feature>Contract.kt`, `<Feature>ViewModel.kt`, `<Feature>Screen.kt`.
2. Add a `Destinations` constant and `composable` entry in `NavGraph.kt`.
3. Inject ViewModel via `hiltViewModel()` in the nav graph, not inside the screen composable itself.
4. Provide use-case bindings in the relevant Hilt module under `di/`.

