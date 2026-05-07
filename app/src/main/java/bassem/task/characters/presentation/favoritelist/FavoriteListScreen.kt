package bassem.task.characters.presentation.favoritelist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import bassem.task.characters.R
import bassem.task.characters.presentation.characterlist.CharactersListContent
import bassem.task.characters.ui.components.BaseScaffold
import bassem.task.characters.ui.components.EmptyView
import bassem.task.characters.ui.components.ErrorView
import bassem.task.characters.ui.components.LoadingView

@Composable
fun FavoriteListScreen(
    viewModel: FavoriteListViewModel = hiltViewModel(),
    onCharacterClick: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val characters = viewModel.favorites.collectAsLazyPagingItems()

    // Handle effects
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FavoriteListEffect.NavigateToCharacterDetail -> {
                    onCharacterClick(effect.id)
                }
            }
        }
    }

    BaseScaffold(
        title = stringResource(R.string.favorites_title),
        showBackButton = true,
        onBackClick = onNavigateBack
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val loadState = characters.loadState.refresh) {
                is LoadState.Loading -> {
                    LoadingView()
                }

                is LoadState.Error -> {
                    ErrorView(
                        message = loadState.error.message ?: stringResource(R.string.unknown_error),
                        onRetry = { characters.refresh() }
                    )
                }

                is LoadState.NotLoading -> {
                    if (characters.itemCount == 0) {
                        EmptyView(
                            title = stringResource(R.string.no_favorites_found),
                            description = stringResource(R.string.add_favorites_description),
                        )
                    } else {
                        CharactersListContent(
                            characters = characters,
                            onCharacterClick = { id ->
                                viewModel.onEvent(FavoriteListEvent.OnCharacterClicked(id))
                            },
                            onFavoriteToggle = { id ->
                                viewModel.onEvent(FavoriteListEvent.OnFavoriteToggle(id))
                            },
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}