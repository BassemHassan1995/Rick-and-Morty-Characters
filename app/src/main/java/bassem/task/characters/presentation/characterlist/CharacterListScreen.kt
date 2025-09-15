package bassem.task.characters.presentation.characterlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import bassem.task.characters.R
import bassem.task.characters.domain.model.Character
import bassem.task.characters.ui.components.ErrorView
import bassem.task.characters.ui.components.LoadingView
import bassem.task.characters.ui.components.EmptyView
import bassem.task.characters.ui.components.BaseScaffold
import coil.compose.rememberAsyncImagePainter

@Composable
fun CharacterListScreen(
    viewModel: CharacterListViewModel = hiltViewModel(),
    onCharacterClick: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val characters = viewModel.characters.collectAsLazyPagingItems()
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
        title = stringResource(R.string.characters_title),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        when (characters.loadState.refresh) {
            is LoadState.Loading -> {
                if (state.isLoading) {
                    LoadingView()
                }
            }
            is LoadState.Error -> {
                val error = characters.loadState.refresh as LoadState.Error
                ErrorView(
                    message = error.error.message ?: stringResource(R.string.unknown_error),
                    onRetry = { characters.refresh() }
                )
            }
            is LoadState.NotLoading -> {
                if (characters.itemCount == 0) {
                    EmptyView(
                        title = stringResource(R.string.no_characters_found),
                        description = stringResource(R.string.try_again_later),
                        onAction = { characters.refresh() }
                    )
                } else {
                    CharactersListContent(
                        characters = characters,
                        onCharacterClick = { id ->
                            viewModel.onEvent(CharacterListEvent.OnCharacterClicked(id))
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

@Composable
fun CharactersListContent(
    characters: LazyPagingItems<Character>,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(characters.itemCount) { index ->
            val character = characters[index]
            if (character != null) {
                CharacterItem(
                    character = character,
                    onCharacterClick = onCharacterClick
                )
            }
        }

        // Show loading indicator at the bottom when loading more
        when (characters.loadState.append) {
            is LoadState.Loading -> {
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
            is LoadState.Error -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.error_loading_more),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            is LoadState.NotLoading -> { /* No additional UI needed */ }
        }
    }
}

@Composable
fun CharacterItem(
    character: Character,
    onCharacterClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCharacterClick(character.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Character Image with rounded corners
            Image(
                painter = rememberAsyncImagePainter(model = character.image),
                contentDescription = character.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Character Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = character.species,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
