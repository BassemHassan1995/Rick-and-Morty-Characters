package bassem.task.characters.presentation.characterdetails

import bassem.task.characters.domain.usecase.GetCharacterByIdUseCase
import bassem.task.characters.presentation.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import bassem.task.characters.presentation.base.ResultState
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
                copy(characterDetailState = ResultState.Loading)
            }

            try {
                val character = getCharacterByIdUseCase(characterId)
                if (character != null) {
                    setState {
                        copy(characterDetailState = ResultState.Success(character))
                    }
                } else {
                    setState {
                        copy(
                            characterDetailState = ResultState.Error()
                        )
                    }
                }
            } catch (throwable: Throwable) {
                setState {
                    copy(
                        characterDetailState = ResultState.Error(
                            throwable.message
                        )
                    )
                }
            }
        }
    }
}
