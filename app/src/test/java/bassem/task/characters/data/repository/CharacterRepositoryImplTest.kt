package bassem.task.characters.data.repository

import bassem.task.characters.data.local.dao.CharacterDao
import bassem.task.characters.data.local.entity.CharacterEntity
import bassem.task.characters.data.mediator.CharacterRemoteMediator
import bassem.task.characters.data.remote.api.CharacterApiService
import bassem.task.characters.data.remote.dto.CharacterDto
import bassem.task.characters.domain.model.CharacterStatus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

class CharacterRepositoryImplTest {

    @Mock
    private lateinit var api: CharacterApiService

    @Mock
    private lateinit var dao: CharacterDao

    @Mock
    private lateinit var remoteMediator: CharacterRemoteMediator

    private lateinit var repository: CharacterRepositoryImpl

    private val testCharacterDto = CharacterDto(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        type = "Scientist",
        gender = "Male",
        image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
    )

    private val testCharacterEntity = CharacterEntity(
        id = 1,
        name = "Rick Sanchez",
        status = "Alive",
        species = "Human",
        image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
        page = 1
    )

    private val deadCharacterDto = CharacterDto(
        id = 2,
        name = "Morty Smith",
        status = "Dead",
        species = "Human",
        type = "",
        gender = "Male",
        image = "https://rickandmortyapi.com/api/character/avatar/2.jpeg"
    )

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = CharacterRepositoryImpl(api, dao, remoteMediator)
    }

    // Test getCharacters method
    @Test
    fun `getCharacters should return paging flow configured with dao when name is null`() =
        runTest {
            // When
            val result = repository.getCharacters(null)

            // Then
            assertNotNull(result)
            // DAO is not called immediately due to lazy evaluation in Pager
            verifyNoInteractions(dao)
            verifyNoInteractions(api)
        }

    @Test
    fun `getCharacters should return paging flow configured with dao when name is empty`() =
        runTest {
            // When
            val result = repository.getCharacters("")

            // Then
            assertNotNull(result)
            // DAO is not called immediately due to lazy evaluation in Pager
            verifyNoInteractions(dao)
            verifyNoInteractions(api)
        }

    @Test
    fun `getCharacters should return paging flow configured with dao when name is blank`() =
        runTest {
            // When
            val result = repository.getCharacters("   ")

            // Then
            assertNotNull(result)
            // DAO is not called immediately due to lazy evaluation in Pager
            verifyNoInteractions(dao)
            verifyNoInteractions(api)
        }

    @Test
    fun `getCharacters should create search paging source when name is provided`() = runTest {
        // Given
        val searchName = "Rick"

        // When
        val result = repository.getCharacters(searchName)

        // Then
        assertNotNull(result)
        verifyNoInteractions(dao)
        verifyNoInteractions(api)
    }

    // Test getCharacterById method - Cache scenarios
    @Test
    fun `getCharacterById should return mapped character from cache when available`() = runTest {
        // Given
        val characterId = 1
        whenever(dao.getCharacterById(characterId)).thenReturn(testCharacterEntity)

        // When
        val result = repository.getCharacterById(characterId)

        // Then
        assertNotNull(result)
        assertEquals(testCharacterEntity.id, result?.id)
        assertEquals(testCharacterEntity.name, result?.name)
        assertEquals(CharacterStatus.fromString(testCharacterEntity.status), result?.status)
        assertEquals(testCharacterEntity.species, result?.species)
        assertEquals(testCharacterEntity.image, result?.image)

        verify(dao).getCharacterById(characterId)
        verifyNoInteractions(api)
    }

    // Test getCharacterById method - API fallback scenarios
    @Test
    fun `getCharacterById should handle different character statuses correctly`() = runTest {
        // Given
        val characterId = 2
        whenever(dao.getCharacterById(characterId)).thenReturn(null)
        whenever(api.getCharacterById(characterId)).thenReturn(deadCharacterDto)

        // When
        val result = repository.getCharacterById(characterId)

        // Then
        assertNotNull(result)
        assertEquals(deadCharacterDto.id, result?.id)
        assertEquals(deadCharacterDto.name, result?.name)
        assertEquals(CharacterStatus.fromString(deadCharacterDto.status), result?.status)
        assertEquals(deadCharacterDto.species, result?.species)

        verify(dao).getCharacterById(characterId)
        verify(api).getCharacterById(characterId)
    }

    @Test
    fun `getCharacterById should fallback to API when cache is empty`() = runTest {
        // Given
        val characterId = 1
        whenever(dao.getCharacterById(characterId)).thenReturn(null)
        whenever(api.getCharacterById(characterId)).thenReturn(testCharacterDto)

        // When
        val result = repository.getCharacterById(characterId)

        // Then
        assertNotNull(result)
        assertEquals(testCharacterDto.id, result?.id)
        assertEquals(testCharacterDto.name, result?.name)
        assertEquals(CharacterStatus.fromString(testCharacterDto.status), result?.status)
        assertEquals(testCharacterDto.species, result?.species)

        verify(dao).getCharacterById(characterId)
        verify(api).getCharacterById(characterId)
    }

    @Test
    fun `getCharacterById should throw exception when cache is empty and API throws exception`() =
        runTest {
            // Given
            val characterId = 999
            whenever(dao.getCharacterById(characterId)).thenReturn(null)
            whenever(api.getCharacterById(characterId)).thenThrow(RuntimeException("Character not found"))

            // When & Then
            try {
                repository.getCharacterById(characterId)
                assert(false) { "Expected exception to be thrown" }
            } catch (e: Exception) {
                // Exception should be thrown as expected
            }

            verify(dao).getCharacterById(characterId)
            verify(api).getCharacterById(characterId)
        }
}
