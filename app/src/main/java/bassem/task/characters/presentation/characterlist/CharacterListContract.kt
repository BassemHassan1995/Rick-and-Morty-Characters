package bassem.task.characters.presentation.characterlist

import bassem.task.characters.presentation.base.ViewEffect
import bassem.task.characters.presentation.base.ViewEvent
import bassem.task.characters.presentation.base.ViewState

// ---------- State ----------
data class CharacterListState(
    val searchQuery: String = "",
) : ViewState {
    fun isSearching() = searchQuery.isNotBlank()
}

// ---------- Events ----------
sealed interface CharacterListEvent : ViewEvent {
    data class OnCharacterClicked(val id: Int) : CharacterListEvent
    data class OnSearchQueryChanged(val query: String) : CharacterListEvent
}

// ---------- Effects ----------
sealed interface CharacterListEffect : ViewEffect {
    data class NavigateToCharacterDetail(val id: Int) : CharacterListEffect
}
