package bassem.task.characters.presentation.characterlist

import bassem.task.characters.domain.usecase.GetCharactersUseCase
import bassem.task.characters.presentation.base.BaseViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import bassem.task.characters.domain.model.Character
import bassem.task.characters.presentation.characterlist.CharacterListEffect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase
) : BaseViewModel<CharacterListEvent, CharacterListState, CharacterListEffect>(
    CharacterListState()
) {

    val characters: Flow<PagingData<Character>> = getCharactersUseCase()
        .cachedIn(viewModelScope)

    init {
        onEvent(CharacterListEvent.LoadInitial)
    }

    override fun onEvent(event: CharacterListEvent) {
        when (event) {
            is CharacterListEvent.LoadInitial -> loadCharacters()
            is CharacterListEvent.OnCharacterClicked ->
                sendEffect { NavigateToCharacterDetail(event.id) }
        }
    }

    private fun loadCharacters() {
        setState {
            copy(isLoading = true)
        }
        viewModelScope.launch {
            try {
                setState {
                    copy(isLoading = false)
                }
            } catch (throwable: Throwable) {
                setState {
                    copy(isLoading = false)
                }
                sendEffect {
                    ShowError(throwable.message ?: "Failed to load characters")
                }
            }
        }
    }
}
