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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import bassem.task.characters.domain.model.Character
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
        title = "Character Details",
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
        // Character Image
        Image(
            painter = rememberAsyncImagePainter(model = character.image),
            contentDescription = character.name,
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Character Name
        Text(
            text = character.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Character Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailRow(label = "Status", value = character.status)
                DetailRow(label = "Species", value = character.species)
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal
        )
    }
}
