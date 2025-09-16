package bassem.task.characters.presentation.characterdetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import bassem.task.characters.domain.model.Character
import bassem.task.characters.domain.model.CharacterStatus
import bassem.task.characters.domain.usecase.GetCharacterByIdUseCase
import bassem.task.characters.presentation.base.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterDetailsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var getCharacterByIdUseCase: GetCharacterByIdUseCase

    private lateinit var viewModel: CharacterDetailsViewModel

    private val testCharacter = Character(
        id = 1,
        name = "Rick Sanchez",
        status = CharacterStatus.ALIVE,
        species = "Human",
        image = "https://example.com/rick.png"
    )

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = CharacterDetailsViewModel(getCharacterByIdUseCase)
    }

    @Test
    fun `initial state should be loading`() {
        // When
        val state = viewModel.state.value

        // Then
        assertTrue(state.characterDetailState is ResultState.Loading)
    }

    @Test
    fun `loadCharacter should update state to loading then success when character found`() =
        runTest {
            // Given
            val characterId = 1
            whenever(getCharacterByIdUseCase(characterId)).thenReturn(testCharacter)

            // When
            viewModel.onEvent(CharacterDetailsEvent.LoadCharacter(characterId))

            // Then
            viewModel.state.test {
                val loadingState = awaitItem()
                assertTrue(loadingState.characterDetailState is ResultState.Loading)

                val successState = awaitItem()
                assertTrue(successState.characterDetailState is ResultState.Success)
                assertEquals(
                    testCharacter,
                    (successState.characterDetailState as ResultState.Success).data
                )
            }
        }

    @Test
    fun `loadCharacter should update state to loading then error when character not found`() =
        runTest {
            // Given
            val characterId = 999
            whenever(getCharacterByIdUseCase(characterId)).thenReturn(null)

            // When
            viewModel.onEvent(CharacterDetailsEvent.LoadCharacter(characterId))

            // Then
            viewModel.state.test {
                val loadingState = awaitItem()
                assertTrue(loadingState.characterDetailState is ResultState.Loading)

                val errorState = awaitItem()
                assertTrue(errorState.characterDetailState is ResultState.Error)
            }
        }

    @Test
    fun `loadCharacter should update state to loading then error when exception occurs`() =
        runTest {
            // Given
            val characterId = 1
            val errorMessage = "Network error"
            whenever(getCharacterByIdUseCase(characterId)).thenThrow(RuntimeException(errorMessage))

            // When
            viewModel.onEvent(CharacterDetailsEvent.LoadCharacter(characterId))

            // Then
            viewModel.state.test {
                val loadingState = awaitItem()
                assertTrue(loadingState.characterDetailState is ResultState.Loading)

                val errorState = awaitItem()
                assertTrue(errorState.characterDetailState is ResultState.Error)
                assertEquals(
                    errorMessage,
                    (errorState.characterDetailState as ResultState.Error).message
                )
            }
        }

    @Test
    fun `loadCharacter should handle different character IDs correctly`() = runTest {
        // Given
        val characterId = 42
        val character = testCharacter.copy(id = characterId, name = "Morty Smith")
        whenever(getCharacterByIdUseCase(characterId)).thenReturn(character)

        // When
        viewModel.onEvent(CharacterDetailsEvent.LoadCharacter(characterId))

        // Then
        viewModel.state.test {
            val loadingState = awaitItem()
            assertTrue(loadingState.characterDetailState is ResultState.Loading)

            val successState = awaitItem()
            assertTrue(successState.characterDetailState is ResultState.Success)
            val resultCharacter = (successState.characterDetailState as ResultState.Success).data
            assertEquals(characterId, resultCharacter.id)
            assertEquals("Morty Smith", resultCharacter.name)
        }
    }

    @Test
    fun `loadCharacter should handle exception with null message`() = runTest {
        // Given
        val characterId = 1
        whenever(getCharacterByIdUseCase(characterId)).thenThrow(RuntimeException())

        // When
        viewModel.onEvent(CharacterDetailsEvent.LoadCharacter(characterId))

        // Then
        viewModel.state.test {
            val loadingState = awaitItem()
            assertTrue(loadingState.characterDetailState is ResultState.Loading)

            val errorState = awaitItem()
            assertTrue(errorState.characterDetailState is ResultState.Error)
            assertNull((errorState.characterDetailState as ResultState.Error).message)
        }
    }
}
