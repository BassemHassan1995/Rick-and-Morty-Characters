package bassem.task.characters.presentation.characterdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import bassem.task.characters.R
import bassem.task.characters.domain.model.Character
import bassem.task.characters.domain.model.CharacterStatus
import bassem.task.characters.ui.components.ErrorView
import bassem.task.characters.ui.components.LoadingView
import bassem.task.characters.ui.components.BaseScaffold
import bassem.task.characters.presentation.base.ResultState
import coil.compose.rememberAsyncImagePainter

@Composable
fun CharacterDetailsScreen(
    characterId: Int,
    viewModel: CharacterDetailsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Load character when screen is first composed
    LaunchedEffect(characterId) {
        viewModel.onEvent(CharacterDetailsEvent.LoadCharacter(characterId))
    }

    // Handle effects
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CharacterDetailsEffect.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    BaseScaffold(
        title = stringResource(R.string.character_details_title),
        showBackButton = true,
        onBackClick = onNavigateBack
    ) { paddingValues ->
        when (val result = state.characterDetailState) {
            is ResultState.Loading -> LoadingView()
            is ResultState.Error -> ErrorView(
                message = result.message,
                onRetry = { viewModel.onEvent(CharacterDetailsEvent.LoadCharacter(characterId)) }
            )

            is ResultState.Success -> {
                CharacterDetailsContent(
                    character = result.data,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is ResultState.Idle -> {}
        }
    }
}

@Composable
fun CharacterDetailsContent(character: Character, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Character Image Card with Status Footer
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Character Image
                Image(
                    painter = rememberAsyncImagePainter(model = character.image),
                    contentDescription = character.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )

                // Status Footer Overlay
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = when (character.status) {
                            CharacterStatus.ALIVE -> Color.Green
                            CharacterStatus.DEAD -> Color.Red
                            CharacterStatus.UNKNOWN -> Color.Gray
                        }.copy(alpha = 0.9f)
                    ),
                ) {
                    Text(
                        text = character.status.displayName(context = LocalContext.current),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Character Name and Species Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = character.species,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}