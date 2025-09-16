# Rick and Morty Characters App

A modern Android application built with **Kotlin** and **Jetpack Compose** that displays characters from the [Rick and Morty API](https://rickandmortyapi.com/). The app demonstrates **Clean Architecture**, **MVI with UDF (Unidirectional Data Flow)**, **offline-first approach** with **Room caching**, **Paging 3**, and **Hilt dependency injection**.

---

## 📱 Features

- **Character List Screen**
  - Displays a paginated list of characters (20 per page) with infinite scrolling
  - Shows character **image, name, and species**
  - Real-time search by character name with debouncing
  - Handles loading, error, and empty states gracefully
  - Navigate to character details on item tap
  - Smooth animations and modern Material 3 design

- **Character Details Screen**
  - Displays comprehensive character information: **name, image, species, and status**
  - Handles offline scenarios gracefully

- **Offline-First Architecture**
  - Characters are cached locally using **Room database** with **RemoteMediator**
  - Seamless offline experience - data available even without internet connection
  - Smart cache invalidation and synchronization

---

## 🏗️ Architecture

This app follows **Clean Architecture** principles with clear separation of concerns across 3 main layers:

```
bassem.task.characters/
│
├── data/           # Data sources and repository implementations
│   ├── local/      # Room database, entities, and DAOs
│   ├── remote/     # Retrofit API service and DTOs
│   ├── mapper/     # Data mapping between layers
│   ├── mediator/   # Paging 3 RemoteMediator for cache management
│   └── repository/ # Repository implementations
│
├── domain/         # Business logic and abstractions
│   ├── model/      # Domain models (pure Kotlin)
│   ├── repository/ # Repository interfaces
│   └── usecase/    # Business use cases
│
├── presentation/   # UI layer with MVI + UDF
│   ├── base/       # Base ViewModel and common UI components
│   ├── characterlist/    # Character list screen
│   └── characterdetails/ # Character details screen
│
├── di/            # Hilt dependency injection modules
├── ui/            # Common UI components and theme
├── MainActivity.kt
└── CharactersApp.kt # Application class
```

### Architectural Layers

**Data Layer:**
- `local/` → Room database with entities, DAOs, and database configuration
- `remote/` → Retrofit service, API interfaces, and network DTOs
- `mapper/` → Bidirectional mapping between DTOs, entities, and domain models
- `mediator/` → Paging 3 RemoteMediator for coordinating network and cache
- `repository/` → Repository pattern implementations

**Domain Layer:**
- `model/` → Pure Kotlin domain models (no Android dependencies)
- `repository/` → Repository contracts/interfaces
- `usecase/` → Business logic encapsulation (e.g., `GetCharactersUseCase`, `GetCharacterByIdUseCase`)

**Presentation Layer:**
- Each feature follows MVI with UDF pattern
- `State` → Immutable UI state representation
- `Event` → User interactions and system events
- `Effect` → One-time UI effects (navigation, snackbars)
- `ViewModel` → State management and business logic coordination

**DI Layer:**
- Hilt modules providing instances of Retrofit, Room, repositories, and use cases
- Scoped dependencies for proper lifecycle management

---

## 🔄 UDF (Unidirectional Data Flow) Pattern

The presentation layer implements a robust UDF pattern:

```
User Action → Event → ViewModel → UseCase → Repository → Data Source (API/DB)
           ↑                                                              ↓
          UI ←─────────────────── State ←─────────────────── Response ←────┘
```

**Flow:**
- `Event` → Represents user interactions and system actions
- `State` → Immutable snapshot of UI state
- `Effect` → One-time events (navigation, notifications)
- `ViewModel` → Processes events and emits new states

---

## 🗄️ Offline-First Caching Strategy

The app implements a sophisticated caching strategy using **Room** and **Paging 3 RemoteMediator**:

**Caching Flow:**
1. **Initial Load:** Check local cache first
2. **Cache Miss:** Fetch from API and cache locally
3. **Cache Hit:** Serve cached data immediately, optionally refresh in background
4. **Pagination:** RemoteMediator handles seamless loading of additional pages
5. **Search:** Direct API calls for search results

**Benefits:**
- ✅ Instant app startup with cached data
- ✅ Offline functionality
- ✅ Reduced network usage
- ✅ Improved user experience

---

## 🛠️ Tech Stack

**Core Technologies:**
- **Language:** Kotlin 2.2.20
- **UI Framework:** Jetpack Compose (BOM 2025.09.00)
- **Architecture:** Clean Architecture + MVI + UDF
- **Minimum SDK:** 24 (Android 7.0)
- **Target SDK:** 36

**Libraries & Frameworks:**
- **Dependency Injection:** Hilt 2.57.1
- **Networking:** Retrofit 3.0.0 + OkHttp 5.1.0
- **JSON Parsing:** Moshi 1.15.2
- **Local Database:** Room 2.8.0
- **Pagination:** Paging 3 (3.3.6)
- **Image Loading:** Coil 2.7.0
- **Navigation:** Compose Navigation 2.9.4
- **Reactive Programming:** Coroutines + Flow
- **Material Design:** Material 3
- **Splash Screen:** Android 12+ Splash Screen API

**Testing:**
- **Unit Testing:** JUnit 4.13.2, Mockito 5.19.0
- **Coroutine Testing:** Kotlinx Coroutines Test 1.10.2
- **Paging Testing:** Paging Testing 3.3.6
- **Flow Testing:** Turbine 1.2.1
- **Architecture Testing:** AndroidX Core Testing 2.2.0

---

## 🚀 Building and Running the Application

### Prerequisites
- **Android Studio:** Arctic Fox or newer
- **JDK:** 11 or higher
- **Minimum Android Version:** Android 7.0 (API level 24)

### Build Instructions

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd rick-and-morty-characters
   ```

2. **Open in Android Studio:**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the project directory and select it

3. **Sync the project:**
   - Android Studio will automatically start syncing Gradle
   - Wait for all dependencies to download

4. **Build the project:**
   ```bash
   ./gradlew assembleDebug
   ```

5. **Run on device/emulator:**
   - Connect an Android device or start an emulator
   - Click the "Run" button in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

### Running Tests
```bash
# Run all unit tests
./gradlew testDebugUnitTest

# Run instrumented tests
./gradlew connectedAndroidTest

# Generate test coverage report
./gradlew testDebugUnitTestCoverage
```

---

## 🏛️ Architectural Choices

### 1. **Clean Architecture**
**Choice:** Implemented Clean Architecture with clear layer separation
**Rationale:** 
- Ensures high testability and maintainability
- Facilitates team collaboration with clear boundaries
- Makes the codebase scalable and adaptable to changing requirements
- Enables easy mocking and unit testing

### 2. **MVI + UDF Pattern**
**Choice:** Combined MVI with Unidirectional Data Flow
**Rationale:**
- MVI provides clear separation between UI and business logic
- UDF ensures predictable state management and easier debugging
- Reactive programming with Flows provides real-time UI updates
- Excellent integration with Jetpack Compose

### 3. **Paging 3 with RemoteMediator**
**Choice:** Used Paging 3 library with custom RemoteMediator
**Rationale:**
- Handles large datasets efficiently with minimal memory usage
- RemoteMediator provides seamless offline/online coordination
- Built-in loading states and error handling
- Optimized for infinite scrolling scenarios

### 4. **Room + Retrofit Architecture**
**Choice:** Room for local storage, Retrofit for network calls
**Rationale:**
- Room provides compile-time SQL validation and excellent Kotlin integration
- Retrofit offers robust networking with minimal boilerplate
- Easy integration with Coroutines and Flow
- Excellent caching and offline capabilities

### 5. **Hilt for Dependency Injection**
**Choice:** Google's Hilt instead of pure Dagger
**Rationale:**
- Reduces DI boilerplate significantly
- Excellent Android integration and lifecycle awareness
- Built-in support for ViewModels and common Android components
- Better developer experience compared to pure Dagger

---

## 🤔 Assumptions and Key Decisions

### Technical Decisions

1. **Offline-First Approach**
   - **Assumption:** Users may have unreliable internet connections
   - **Decision:** Implemented comprehensive local caching with Room
   - **Impact:** App remains functional without internet connectivity

2. **Search Implementation**
   - **Assumption:** Search queries should be debounced to reduce API calls
   - **Decision:** Implemented 1-second debounce with direct API calls for search
   - **Impact:** Better user experience and reduced server load

3. **Pagination Strategy**
   - **Assumption:** API provides paginated data that should be cached locally
   - **Decision:** Used RemoteMediator to coordinate network and cache
   - **Impact:** Seamless infinite scrolling with offline support

4. **Error Handling**
   - **Assumption:** Network errors are common and should be handled gracefully
   - **Decision:** Implemented comprehensive error states with retry mechanisms
   - **Impact:** Robust user experience even with network issues

### UI/UX Decisions

5. **Material 3 Design**
   - **Decision:** Used latest Material 3 design system
   - **Rationale:** Modern, accessible, and consistent with Android design guidelines

6. **Image Loading Strategy**
   - **Decision:** Used Coil for image loading with proper error states
   - **Rationale:** Excellent Compose integration and performance

7. **Navigation Pattern**
   - **Decision:** Single-activity architecture with Compose Navigation
   - **Rationale:** Modern Android development best practices

### Data Management

8. **State Management**
   - **Decision:** Immutable state objects with sealed classes for events
   - **Rationale:** Predictable state changes and easier debugging

9. **Repository Pattern**
   - **Decision:** Single source of truth with repository coordinating cache and network
   - **Rationale:** Clean separation of concerns and easier testing

10. **Testing Strategy**
    - **Decision:** Focus on unit tests for business logic, integration tests for data layer
    - **Rationale:** High coverage with fast, reliable tests

---

## 📝 API Information

- **Base URL:** `https://rickandmortyapi.com/api/`
- **Endpoints Used:**
  - `GET /character` - Paginated character list
  - `GET /character/{id}` - Individual character details
  - `GET /character/?name={query}` - Character search

---

## 🔄 Current Status

The application successfully addresses all requirements:
- ✅ Character list with pagination
- ✅ Character details screen
- ✅ Search functionality
- ✅ Offline support
- ✅ Modern UI with Jetpack Compose
- ✅ Clean Architecture implementation
- ✅ Comprehensive error handling
- ✅ Unit tests
