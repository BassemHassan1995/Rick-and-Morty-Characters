# Characters App (Take-Home Test)

A simple Android app built with **Kotlin** and **Jetpack Compose** that fetches data from the [Rick and Morty API](https://rickandmortyapi.com/).  
The app demonstrates **clean architecture**, **UDF (Unidirectional Data Flow)**, **Room caching**, and **Hilt dependency injection**.  

---

## 📱 Features

- **Character List Screen**
  - Displays a paginated list of characters (20 per page).
  - Shows character **image, name, and species**.
  - Search by character name.
  - Handles loading, error, and empty states.
  - Navigate to details on item tap.

- **Character Details Screen**
  - Displays **name, image, species, and status** of the selected character.

- **Offline Support**
  - Characters are cached locally using **Room database**.
  - Data available even when offline.

---

## 🏗️ Architecture

This app follows **Clean Architecture** with 3 layers:

```
com.example.rickandmorty/
│
├── data/       # Handles local (Room) + remote (Retrofit) data sources
├── domain/     # Business logic (models, repository interfaces, use cases)
├── ui/         # Jetpack Compose + UDF (state, events, reducer per screen)
├── di/         # Hilt modules for DI
└── CharactersApp.kt
```

### Layers

- **Data Layer**
  - `remote/` → Retrofit service + DTOs
  - `local/` → Room entities + DAO
  - `repository/` → RepositoryImpl (API + DB + mapping)

- **Domain Layer**
  - `model/` → Pure Kotlin models
  - `repository/` → Repository interfaces
  - `usecase/` → Business actions (e.g., `GetCharactersUseCase`)

- **UI Layer (feature-based)**
  - `characterlist/` → `Screen`, `ViewModel/Reducer`, `State`, `Event`
  - `characterdetails/` → `Screen`, `ViewModel/Reducer`, `State`, `Event`

- **DI Layer**
  - Provides Retrofit, Room, Repository, and UseCases using **Hilt**.

---

## 🔄 UDF Pattern

The UI layer is built with **Unidirectional Data Flow**:

```
User Event → ViewModel/Reducer → UseCase → Repository → Data Source (API/DB)
          ↑---------------------------------------------------------------↓
                              Updated State → UI
```

- `Event` → represents user/system actions  
- `State` → immutable snapshot of UI  
- `Reducer` (ViewModel) → maps Events → new State  

---

## 🗄️ Caching

- **Room Database** used for local caching.  
- When fetching:
  - Load cached characters first.  
  - Fetch from API and update DB.  
  - Serve fresh + cached data seamlessly.  

---

## 🛠️ Tech Stack

- **Language:** Kotlin  
- **UI:** Jetpack Compose  
- **Architecture:** Clean Architecture + UDF  
- **DI:** Hilt  
- **Networking:** Retrofit + OkHttp  
- **Local Storage:** Room  
- **Coroutines & Flow** for async + reactive streams  
- **Pagination:** API paginated + lazy list loading  


