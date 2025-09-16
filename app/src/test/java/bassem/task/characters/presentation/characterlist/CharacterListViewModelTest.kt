package bassem.task.characters.presentation.characterlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import app.cash.turbine.test
import bassem.task.characters.domain.usecase.GetCharactersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var getCharactersUseCase: GetCharactersUseCase

    private lateinit var viewModel: CharacterListViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // Setup default mock behavior
        whenever(getCharactersUseCase("")).thenReturn(flowOf(PagingData.empty()))

        viewModel = CharacterListViewModel(getCharactersUseCase)
    }

    @Test
    fun `initial state should have empty search query`() {
        // When
        val state = viewModel.state.value

        // Then
        assertEquals("", state.searchQuery)
        assertFalse(state.isSearching())
    }

    @Test
    fun `onSearchQueryChanged should update search query in state`() = runTest {
        // Given
        val searchQuery = "Rick"

        // When
        viewModel.onEvent(CharacterListEvent.OnSearchQueryChanged(searchQuery))

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(searchQuery, state.searchQuery)
            assertTrue(state.isSearching())
        }
    }

    @Test
    fun `onSearchQueryChanged should emit search query to flow`() = runTest {
        // Given
        val searchQuery = "Morty"
        whenever(getCharactersUseCase(searchQuery)).thenReturn(flowOf(PagingData.empty()))

        // When
        viewModel.onEvent(CharacterListEvent.OnSearchQueryChanged(searchQuery))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(searchQuery, state.searchQuery)
        }
    }

    @Test
    fun `onCharacterClicked should send NavigateToCharacterDetail effect`() = runTest {
        // Given
        val characterId = 42

        // When
        viewModel.onEvent(CharacterListEvent.OnCharacterClicked(characterId))

        // Then
        viewModel.effect.test {
            val effect = awaitItem()
            assertTrue(effect is CharacterListEffect.NavigateToCharacterDetail)
            assertEquals(characterId, (effect as CharacterListEffect.NavigateToCharacterDetail).id)
        }
    }

    @Test
    fun `empty search query should not be considered as searching`() = runTest {
        // When
        viewModel.onEvent(CharacterListEvent.OnSearchQueryChanged(""))

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("", state.searchQuery)
            assertFalse(state.isSearching())
        }
    }

    @Test
    fun `blank search query should not be considered as searching`() = runTest {
        // When
        viewModel.onEvent(CharacterListEvent.OnSearchQueryChanged("   "))

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("   ", state.searchQuery)
            assertFalse(state.isSearching())
        }
    }

    @Test
    fun `non-empty search query should be considered as searching`() = runTest {
        // When
        viewModel.onEvent(CharacterListEvent.OnSearchQueryChanged("Rick"))

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("Rick", state.searchQuery)
            assertTrue(state.isSearching())
        }
    }

    @Test
    fun `multiple search query changes should update state correctly`() = runTest {
        // When
        viewModel.onEvent(CharacterListEvent.OnSearchQueryChanged("Rick"))
        viewModel.onEvent(CharacterListEvent.OnSearchQueryChanged("Morty"))

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("Morty", state.searchQuery)
            assertTrue(state.isSearching())
        }
    }

    @Test
    fun `clearing search query should update state to not searching`() = runTest {
        // Given - First set a search query
        viewModel.onEvent(CharacterListEvent.OnSearchQueryChanged("Rick"))

        // When - Clear the search query
        viewModel.onEvent(CharacterListEvent.OnSearchQueryChanged(""))

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals("", state.searchQuery)
            assertFalse(state.isSearching())
        }
    }
}
