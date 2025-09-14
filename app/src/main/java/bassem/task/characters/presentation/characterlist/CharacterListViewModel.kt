package bassem.task.characters.presentation.characterlist

import bassem.task.characters.domain.usecase.GetCharactersUseCase
import bassem.task.characters.presentation.base.BaseViewModel

import androidx.lifecycle.viewModelScope
import bassem.task.characters.presentation.base.ResultState
import bassem.task.characters.presentation.characterlist.CharacterListEffect.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase
) : BaseViewModel<CharacterListEvent, CharacterListState, CharacterListEffect>(
    CharacterListState()
) {

    init {
        onEvent(CharacterListEvent.LoadInitial)
    }

    override fun onEvent(event: CharacterListEvent) {
        when (event) {
            is CharacterListEvent.LoadInitial -> loadCharacters(isInitialLoad = true)
            is CharacterListEvent.LoadNextPage -> loadCharacters(isInitialLoad = false)
            is CharacterListEvent.OnCharacterClicked ->
                sendEffect { NavigateToCharacterDetail(event.id) }
        }
    }

    private fun loadCharacters(isInitialLoad: Boolean) {
        viewModelScope.launch {
            val pageToLoad = if (isInitialLoad) 1 else state.value.page

            getCharactersUseCase(pageToLoad)
                .onStart {
                    if (isInitialLoad) {
                        setState {
                            copy(charactersState = ResultState.Loading)
                        }
                    }
                }
                .catch { throwable ->
                    if (isInitialLoad) {
                        // For initial load, show full screen error
                        setState {
                            copy(
                                charactersState = ResultState.Error(
                                    throwable.message ?: "Unknown error", throwable
                                )
                            )
                        }
                    } else {
                        // For pagination, show snackbar and keep existing data
                        sendEffect {
                            ShowError(
                                throwable.message ?: "Failed to load more characters"
                            )
                        }
                    }
                }
                .collect { newCharacters ->
                    setState {
                        val existingCharacters = if (isInitialLoad) {
                            emptyList()
                        } else {
                            (charactersState as? ResultState.Success)?.data ?: emptyList()
                        }

                        copy(
                            charactersState = ResultState.Success(existingCharacters + newCharacters),
                            page = if (newCharacters.isNotEmpty()) pageToLoad + 1 else pageToLoad,
                            endReached = newCharacters.isEmpty()
                        )
                    }
                }
        }
    }

}
