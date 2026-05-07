package bassem.task.characters.presentation.favoritelist

import bassem.task.characters.presentation.base.ViewEffect
import bassem.task.characters.presentation.base.ViewEvent
import bassem.task.characters.presentation.base.ViewState

// ---------- State ----------
class FavoriteListState : ViewState

// ---------- Events ----------
sealed interface FavoriteListEvent : ViewEvent {
    data class OnCharacterClicked(val id: Int) : FavoriteListEvent
    data class OnFavoriteToggle(val id: Int) : FavoriteListEvent
}

// ---------- Effects ----------
sealed interface FavoriteListEffect : ViewEffect {
    data class NavigateToCharacterDetail(val id: Int) : FavoriteListEffect
}