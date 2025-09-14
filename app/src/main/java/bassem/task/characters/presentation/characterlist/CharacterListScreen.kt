package bassem.task.characters.presentation.characterlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import bassem.task.characters.domain.model.Character
import bassem.task.characters.ui.components.ErrorView
import bassem.task.characters.ui.components.LoadingView
import bassem.task.characters.ui.components.EmptyView
import bassem.task.characters.ui.components.BaseScaffold
import bassem.task.characters.presentation.base.ResultState
import coil.compose.rememberAsyncImagePainter

@Composable
fun CharacterListScreen(
    viewModel: CharacterListViewModel = hiltViewModel(),
    onCharacterClick: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle effects
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CharacterListEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is CharacterListEffect.NavigateToCharacterDetail -> {
                    onCharacterClick(effect.id)
                }
            }
        }
    }

    BaseScaffold(
        title = "Characters",
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        when (val result = state.charactersState) {
            is ResultState.Loading -> LoadingView()
            is ResultState.Error -> ErrorView(message = result.message)
            is ResultState.Success -> {
                val characters = result.data
                if (characters.isEmpty()) {
                    EmptyView(
                        title = "No characters found.",
                        description = "Try again later or check your connection.",
                        onAction = { viewModel.onEvent(CharacterListEvent.LoadInitial) }
                    )
                } else {
                    CharactersListContent(
                        characters = characters,
                        onCharacterClick = { id ->
                            viewModel.onEvent(CharacterListEvent.OnCharacterClicked(id))
                        },
                        onLoadMore = { viewModel.onEvent(CharacterListEvent.LoadNextPage) },
                        endReached = state.endReached,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }

            is ResultState.Idle -> {}
        }
    }
}

@Composable
fun CharactersListContent(
    characters: List<Character>,
    onCharacterClick: (Int) -> Unit,
    onLoadMore: () -> Unit,
    endReached: Boolean,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Detect when we're near the end of the list
    val shouldLoadMore by remember {
        derivedStateOf {
            if (endReached || characters.isEmpty()) {
                false
            } else {
                val layoutInfo = listState.layoutInfo
                val visibleItems = layoutInfo.visibleItemsInfo

                if (visibleItems.isEmpty()) {
                    false
                } else {
                    val lastVisibleIndex = visibleItems.lastOrNull()?.index ?: -1
                    val totalItems = characters.size

                    // Use a dynamic threshold based on visible items count, with a minimum of 2 and maximum of 5
                    val threshold = (visibleItems.size / 2).coerceIn(2, 5)

                    // Trigger loading when we're within the threshold of the end
                    lastVisibleIndex >= totalItems - threshold
                }
            }
        }
    }

    // Trigger load more when we're near the end
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(characters) { character ->
            CharacterItem(
                character = character,
                onCharacterClick = onCharacterClick
            )
        }

        // Show loading indicator at the bottom when loading more
        if (!endReached) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun CharacterItem(
    character: Character,
    onCharacterClick: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCharacterClick(character.id) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = character.image),
            contentDescription = character.name,
            modifier = Modifier.size(64.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(text = character.name, style = MaterialTheme.typography.titleMedium)
            Text(text = character.species, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
