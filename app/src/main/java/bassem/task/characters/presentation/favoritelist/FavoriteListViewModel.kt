package bassem.task.characters.presentation.favoritelist

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import bassem.task.characters.domain.model.Character
import bassem.task.characters.domain.usecase.GetFavoriteCharactersUseCase
import bassem.task.characters.domain.usecase.ToggleFavoriteUseCase
import bassem.task.characters.presentation.base.BaseViewModel
import bassem.task.characters.presentation.favoritelist.FavoriteListEffect.NavigateToCharacterDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteListViewModel @Inject constructor(
    getFavoriteCharactersUseCase: GetFavoriteCharactersUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : BaseViewModel<FavoriteListEvent, FavoriteListState, FavoriteListEffect>(
    FavoriteListState()
) {
    val favorites: Flow<PagingData<Character>> = getFavoriteCharactersUseCase()
        .cachedIn(viewModelScope)

    override fun onEvent(event: FavoriteListEvent) {
        when (event) {
            is FavoriteListEvent.OnCharacterClicked ->
                sendEffect { NavigateToCharacterDetail(event.id) }

            is FavoriteListEvent.OnFavoriteToggle -> {
                viewModelScope.launch {
                    toggleFavoriteUseCase(event.id)
                }
            }
        }
    }
}