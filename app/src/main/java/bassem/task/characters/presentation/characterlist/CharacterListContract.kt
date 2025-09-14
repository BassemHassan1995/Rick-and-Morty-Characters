package bassem.task.characters.presentation.characterlist

import bassem.task.characters.domain.model.Character
import bassem.task.characters.presentation.base.ViewEffect
import bassem.task.characters.presentation.base.ViewEvent
import bassem.task.characters.presentation.base.ViewState
import bassem.task.characters.presentation.base.ResultState

// ---------- State ----------
data class CharacterListState(
    val charactersState: ResultState<List<Character>> = ResultState.Idle,
    val page: Int = 1,
    val endReached: Boolean = false
) : ViewState

// ---------- Events ----------
sealed interface CharacterListEvent : ViewEvent {
    object LoadInitial : CharacterListEvent
    object LoadNextPage : CharacterListEvent
    data class OnCharacterClicked(val id: Int) : CharacterListEvent
}

// ---------- Effects ----------
sealed interface CharacterListEffect : ViewEffect {
    data class ShowError(val message: String) : CharacterListEffect
    data class NavigateToCharacterDetail(val id: Int) : CharacterListEffect
}
