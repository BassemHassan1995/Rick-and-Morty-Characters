package bassem.task.characters.presentation.characterlist

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import bassem.task.characters.domain.model.Character
import bassem.task.characters.domain.usecase.GetCharactersUseCase
import bassem.task.characters.presentation.base.BaseViewModel
import bassem.task.characters.presentation.characterlist.CharacterListEffect.NavigateToCharacterDetail
import bassem.task.characters.presentation.characterlist.CharacterListEffect.ShowError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase
) : BaseViewModel<CharacterListEvent, CharacterListState, CharacterListEffect>(
    CharacterListState()
) {
    private val searchQueryFlow = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val characters: Flow<PagingData<Character>> = searchQueryFlow
        .debounce(400)
        .flatMapLatest { query ->
            getCharactersUseCase(query.ifBlank { null })
        }
        .cachedIn(viewModelScope)

    init {
        onEvent(CharacterListEvent.LoadInitial)
    }

    override fun onEvent(event: CharacterListEvent) {
        when (event) {
            is CharacterListEvent.LoadInitial -> loadCharacters()
            is CharacterListEvent.OnCharacterClicked ->
                sendEffect { NavigateToCharacterDetail(event.id) }
            is CharacterListEvent.OnSearchQueryChanged -> {
                setState { copy(searchQuery = event.query) }
                searchQueryFlow.value = event.query
            }
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
