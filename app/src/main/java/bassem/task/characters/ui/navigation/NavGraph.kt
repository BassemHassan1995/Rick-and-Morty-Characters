package bassem.task.characters.ui.navigation

import bassem.task.characters.presentation.characterlist.CharacterListViewModel
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import bassem.task.characters.presentation.characterlist.CharacterListScreen

object Destinations {
    const val CHARACTER_LIST = "character_list"
    const val CHARACTER_DETAIL = "character_detail"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Destinations.CHARACTER_LIST
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Destinations.CHARACTER_LIST) {
            val viewModel: CharacterListViewModel = hiltViewModel()
            CharacterListScreen(
                viewModel = viewModel,
                onCharacterClick = { characterId ->
                    // TODO: navigate to detail screen later
                    // navController.navigate("${Destinations.CHARACTER_DETAIL}/$characterId")
                }
            )
        }
    }
}
