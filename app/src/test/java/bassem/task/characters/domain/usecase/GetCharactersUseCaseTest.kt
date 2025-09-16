package bassem.task.characters.domain.usecase

import androidx.paging.PagingData
import bassem.task.characters.domain.model.Character
import bassem.task.characters.domain.repository.CharacterRepository
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetCharactersUseCaseTest {

    @Mock
    private lateinit var repository: CharacterRepository

    private lateinit var useCase: GetCharactersUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        useCase = GetCharactersUseCase(repository)
    }

    @Test
    fun `invoke should call repository getCharacters with null name when no name provided`() = runTest {
        // Given
        val mockPagingData = PagingData.empty<Character>()
        whenever(repository.getCharacters(null)).thenReturn(flowOf(mockPagingData))

        // When
        val result = useCase.invoke()

        // Then
        verify(repository).getCharacters(null)
        assertNotNull(result)
        // Consume the flow to ensure it's used
        result.collect { pagingData ->
            assertNotNull(pagingData)
        }
    }

    @Test
    fun `invoke should call repository getCharacters with provided name`() = runTest {
        // Given
        val name = "Rick"
        val mockPagingData = PagingData.empty<Character>()
        whenever(repository.getCharacters(name)).thenReturn(flowOf(mockPagingData))

        // When
        val result = useCase.invoke(name)

        // Then
        verify(repository).getCharacters(name)
        assertNotNull(result)
        // Consume the flow to ensure it's used
        result.collect { pagingData ->
            assertNotNull(pagingData)
        }
    }

    @Test
    fun `invoke should call repository getCharacters with empty string`() = runTest {
        // Given
        val name = ""
        val mockPagingData = PagingData.empty<Character>()
        whenever(repository.getCharacters(name)).thenReturn(flowOf(mockPagingData))

        // When
        val result = useCase.invoke(name)

        // Then
        verify(repository).getCharacters(name)
        assertNotNull(result)
        // Consume the flow to ensure it's used
        result.collect { pagingData ->
            assertNotNull(pagingData)
        }
    }
}
