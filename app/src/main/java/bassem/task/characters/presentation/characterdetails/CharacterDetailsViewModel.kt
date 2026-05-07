package bassem.task.characters.presentation.characterdetails

import bassem.task.characters.domain.usecase.GetCharacterByIdUseCase
import bassem.task.characters.domain.usecase.IsCharacterFavoriteUseCase
import bassem.task.characters.domain.usecase.ToggleFavoriteUseCase
import bassem.task.characters.presentation.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailsViewModel @Inject constructor(
    private val getCharacterByIdUseCase: GetCharacterByIdUseCase,
    private val isCharacterFavoriteUseCase: IsCharacterFavoriteUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : BaseViewModel<CharacterDetailsEvent, CharacterDetailsState, CharacterDetailsEffect>(
    CharacterDetailsState()
) {
    private var favoriteJob: Job? = null
    private var currentCharacterId: Int? = null

    override fun onEvent(event: CharacterDetailsEvent) {
        when (event) {
            is CharacterDetailsEvent.LoadCharacter -> {
                currentCharacterId = event.characterId
                loadCharacter(event.characterId)
                observeFavoriteStatus(event.characterId)
            }
            CharacterDetailsEvent.OnFavoriteToggle -> {
                currentCharacterId?.let { id ->
                    viewModelScope.launch {
                        toggleFavoriteUseCase(id)
                    }
                }
            }
        }
    }

    private fun observeFavoriteStatus(characterId: Int) {
        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            isCharacterFavoriteUseCase(characterId).collectLatest { isFavorite ->
                setState { copy(isFavorite = isFavorite) }
            }
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
