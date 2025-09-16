package bassem.task.characters.domain.usecase

import bassem.task.characters.domain.model.Character
import bassem.task.characters.domain.model.CharacterStatus
import bassem.task.characters.domain.repository.CharacterRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetCharacterByIdUseCaseTest {

    @Mock
    private lateinit var repository: CharacterRepository

    private lateinit var useCase: GetCharacterByIdUseCase

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
        useCase = GetCharacterByIdUseCase(repository)
    }

    @Test
    fun `invoke should return character when repository returns character`() = runTest {
        // Given
        val characterId = 1
        whenever(repository.getCharacterById(characterId)).thenReturn(testCharacter)

        // When
        val result = useCase.invoke(characterId)

        // Then
        assertEquals(testCharacter, result)
        verify(repository).getCharacterById(characterId)
    }

    @Test
    fun `invoke should return null when repository returns null`() = runTest {
        // Given
        val characterId = 999
        whenever(repository.getCharacterById(characterId)).thenReturn(null)

        // When
        val result = useCase.invoke(characterId)

        // Then
        assertNull(result)
        verify(repository).getCharacterById(characterId)
    }

    @Test
    fun `invoke should call repository with correct id`() = runTest {
        // Given
        val characterId = 42
        whenever(repository.getCharacterById(characterId)).thenReturn(null)

        // When
        useCase.invoke(characterId)

        // Then
        verify(repository).getCharacterById(characterId)
    }
}
