package bassem.task.characters.presentation.characterlist

import bassem.task.characters.presentation.base.ViewEffect
import bassem.task.characters.presentation.base.ViewEvent
import bassem.task.characters.presentation.base.ViewState

// ---------- State ----------
data class CharacterListState(
    val isLoading: Boolean = false
) : ViewState

// ---------- Events ----------
sealed interface CharacterListEvent : ViewEvent {
    object LoadInitial : CharacterListEvent
    data class OnCharacterClicked(val id: Int) : CharacterListEvent
}

// ---------- Effects ----------
sealed interface CharacterListEffect : ViewEffect {
    data class ShowError(val message: String) : CharacterListEffect
    data class NavigateToCharacterDetail(val id: Int) : CharacterListEffect
}
