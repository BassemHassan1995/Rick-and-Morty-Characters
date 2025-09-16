package bassem.task.characters.presentation.characterdetails

import bassem.task.characters.presentation.base.ViewEffect
import bassem.task.characters.presentation.base.ViewEvent
import bassem.task.characters.presentation.base.ViewState
import bassem.task.characters.domain.model.Character

// State
data class CharacterDetailsState(
    val character: Character? = null,
    val isLoading: Boolean = false,
    val error: CharacterDetailsError? = null
) : ViewState

sealed class CharacterDetailsError {
    object CharacterNotFound : CharacterDetailsError()
    data class GeneralError(val message: String?) : CharacterDetailsError()
}

// Events (what the user triggers / screen inputs)
sealed interface CharacterDetailsEvent : ViewEvent {
    data class LoadCharacter(val characterId: Int) : CharacterDetailsEvent
}

sealed interface CharacterDetailsEffect : ViewEffect {
    object NavigateBack : CharacterDetailsEffect
}
