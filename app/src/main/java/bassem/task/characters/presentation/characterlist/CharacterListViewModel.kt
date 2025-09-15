package bassem.task.characters.presentation.characterlist

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import bassem.task.characters.domain.model.Character
import bassem.task.characters.domain.usecase.GetCharactersUseCase
import bassem.task.characters.presentation.base.BaseViewModel
import bassem.task.characters.presentation.characterlist.CharacterListEffect.NavigateToCharacterDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase,
) : BaseViewModel<CharacterListEvent, CharacterListState, CharacterListEffect>(
    CharacterListState()
) {
    private val searchQueryFlow = MutableSharedFlow<String>(
        replay = 1,
        extraBufferCapacity = 1
    )

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val characters: Flow<PagingData<Character>> = searchQueryFlow
        .onStart { emit("") }
        .distinctUntilChanged()
        .debounce(1000)
        .flatMapLatest { query ->
            getCharactersUseCase(query)
        }
        .cachedIn(viewModelScope)

    override fun onEvent(event: CharacterListEvent) {
        when (event) {
            is CharacterListEvent.OnCharacterClicked ->
                sendEffect { NavigateToCharacterDetail(event.id) }

            is CharacterListEvent.OnSearchQueryChanged -> {
                setState { copy(searchQuery = event.query) }
                searchQueryFlow.tryEmit(event.query)
            }
        }
    }
}
