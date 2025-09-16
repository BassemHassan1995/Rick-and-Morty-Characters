package bassem.task.characters.presentation.characterdetails

import bassem.task.characters.domain.usecase.GetCharacterByIdUseCase
import bassem.task.characters.presentation.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailsViewModel @Inject constructor(
    private val getCharacterByIdUseCase: GetCharacterByIdUseCase
) : BaseViewModel<CharacterDetailsEvent, CharacterDetailsState, CharacterDetailsEffect>(
    CharacterDetailsState()
) {

    override fun onEvent(event: CharacterDetailsEvent) {
        when (event) {
            is CharacterDetailsEvent.LoadCharacter -> loadCharacter(event.characterId)
        }
    }

    private fun loadCharacter(characterId: Int) {
        viewModelScope.launch {
            setState {
                copy(isLoading = true, error = null)
            }

            try {
                val character = getCharacterByIdUseCase(characterId)
                setState {
                    copy(
                        character = character,
                        isLoading = false,
                        error = if (character != null) null else CharacterDetailsError.CharacterNotFound,
                    )
                }
            } catch (throwable: Throwable) {
                setState {
                    copy(
                        isLoading = false,
                        error = CharacterDetailsError.GeneralError(throwable.message)
                    )
                }
            }
        }
    }
}
