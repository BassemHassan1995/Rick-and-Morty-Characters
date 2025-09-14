package bassem.task.characters.ui.navigation

import bassem.task.characters.presentation.characterlist.CharacterListViewModel
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import bassem.task.characters.presentation.characterlist.CharacterListScreen
import bassem.task.characters.presentation.characterdetails.CharacterDetailsScreen
import bassem.task.characters.presentation.characterdetails.CharacterDetailsViewModel

object Destinations {
    const val CHARACTER_LIST = "character_list"
    const val CHARACTER_DETAIL_WITH_ID = "character_detail/{characterId}"

    fun characterDetail(characterId: Int) = "character_detail/$characterId"
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
                    navController.navigate(Destinations.characterDetail(characterId))
                }
            )
        }

        composable(
            route = Destinations.CHARACTER_DETAIL_WITH_ID,
            arguments = listOf(
                navArgument("characterId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getInt("characterId") ?: 0
            val viewModel: CharacterDetailsViewModel = hiltViewModel()
            CharacterDetailsScreen(
                characterId = characterId,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
