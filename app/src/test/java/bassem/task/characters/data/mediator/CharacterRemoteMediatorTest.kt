package bassem.task.characters.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import bassem.task.characters.data.local.AppDatabase
import bassem.task.characters.data.local.dao.CharacterDao
import bassem.task.characters.data.local.dao.RemoteKeysDao
import bassem.task.characters.data.local.entity.CharacterEntity
import bassem.task.characters.data.local.entity.RemoteKeyEntity
import bassem.task.characters.data.remote.api.CharacterApiService
import bassem.task.characters.data.remote.dto.CharacterResponseDto
import bassem.task.characters.data.remote.dto.InfoDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*

@OptIn(ExperimentalPagingApi::class)
class CharacterRemoteMediatorTest {

    @Mock
    private lateinit var api: CharacterApiService

    @Mock
    private lateinit var database: AppDatabase

    @Mock
    private lateinit var characterDao: CharacterDao

    @Mock
    private lateinit var remoteKeysDao: RemoteKeysDao

    private lateinit var mediator: CharacterRemoteMediator

    // Common test data
    private val testCharacterEntity = CharacterEntity(
        id = 1,
        name = "Test Character",
        status = "Alive",
        species = "Human",
        image = "test.jpg",
        page = 1
    )

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        whenever(database.characterDao()).thenReturn(characterDao)
        whenever(database.remoteKeysDao()).thenReturn(remoteKeysDao)

        mediator = CharacterRemoteMediator(api, database)
    }

    @Test
    fun `test mediator handles PREPEND load type`() = runTest {
        val pagingState = createMockPagingState()

        val result = mediator.load(LoadType.PREPEND, pagingState)

        assertTrue("PREPEND should return success", result is RemoteMediator.MediatorResult.Success)
        assertTrue("PREPEND should reach end of pagination",
            (result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `test mediator handles APPEND with empty state`() = runTest {
        val emptyPagingState = createEmptyPagingState()

        val result = mediator.load(LoadType.APPEND, emptyPagingState)

        assertTrue("Empty APPEND should return success", result is RemoteMediator.MediatorResult.Success)
        assertTrue("Empty APPEND should reach end of pagination",
            (result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `test mediator returns error when API fails`() = runTest {
        val pagingState = createMockPagingState()
        val exception = RuntimeException("Network error")

        whenever(api.getCharacters(any(), isNull())).thenThrow(exception)

        val result = mediator.load(LoadType.REFRESH, pagingState)

        assertTrue("API failure should return error", result is RemoteMediator.MediatorResult.Error)
        assertEquals("Should return the same exception", exception,
            (result as RemoteMediator.MediatorResult.Error).throwable)
    }

    @Test
    fun `test mediator handles successful API response`() = runTest {
        val pagingState = createMockPagingState()
        val mockResponse = createMockSuccessfulApiResponse()

        whenever(api.getCharacters(any(), isNull())).thenReturn(mockResponse)

        val result = mediator.load(LoadType.REFRESH, pagingState)

        verify(api).getCharacters(1, null)
        assertNotNull("Result should not be null", result)
    }

    @Test
    fun `test mediator handles APPEND when no next page exists`() = runTest {
        val pagingStateWithData = createPagingStateWithData(testCharacterEntity)
        val mockRemoteKey = createMockRemoteKeyWithoutNext()

        whenever(database.remoteKeysDao().remoteKeysCharacterId(1)).thenReturn(mockRemoteKey)

        val result = mediator.load(LoadType.APPEND, pagingStateWithData)

        assertTrue("APPEND without next page should return success", result is RemoteMediator.MediatorResult.Success)
        assertTrue("Should reach end of pagination",
            (result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `test mediator creates correct page numbers for REFRESH`() = runTest {
        val pagingState = createMockPagingState()
        whenever(api.getCharacters(any(), any())).thenThrow(RuntimeException("Expected"))

        try {
            mediator.load(LoadType.REFRESH, pagingState)
        } catch (_: Exception) {
            // Expected
        }

        verify(api).getCharacters(1, null)
    }

    @Test
    fun `test mediator with different LoadType scenarios`() = runTest {
        val pagingState = createMockPagingState()

        val prependResult = mediator.load(LoadType.PREPEND, pagingState)
        assertTrue("PREPEND should return success", prependResult is RemoteMediator.MediatorResult.Success)
        assertTrue("PREPEND should reach end",
            (prependResult as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        val appendResult = mediator.load(LoadType.APPEND, createEmptyPagingState())
        assertTrue("Empty APPEND should return success", appendResult is RemoteMediator.MediatorResult.Success)
        assertTrue("Empty APPEND should reach end",
            (appendResult as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `test mediator verifies API call parameters`() = runTest {
        val pagingState = createMockPagingState()
        val mockResponse = createMockSuccessfulApiResponse()

        whenever(api.getCharacters(eq(1), isNull())).thenReturn(mockResponse)

        mediator.load(LoadType.REFRESH, pagingState)

        verify(api).getCharacters(1, null)
    }

    @Test
    fun `test mediator handles APPEND with existing data`() = runTest {
        val pagingStateWithData = createPagingStateWithData(testCharacterEntity)
        val mockRemoteKey = createMockRemoteKeyWithNext(2)
        val mockResponse = createMockSuccessfulApiResponse()

        whenever(database.remoteKeysDao().remoteKeysCharacterId(1)).thenReturn(mockRemoteKey)
        whenever(api.getCharacters(any(), isNull())).thenReturn(mockResponse)

        val result = mediator.load(LoadType.APPEND, pagingStateWithData)

        verify(database.remoteKeysDao()).remoteKeysCharacterId(1)
        assertNotNull("Result should not be null", result)
    }

    // Helper methods for creating test data
    private fun createMockSuccessfulApiResponse(): CharacterResponseDto {
        val mockInfo = createMockInfoDto(null)
        return mock<CharacterResponseDto> {
            on { results } doReturn emptyList()
            on { info } doReturn mockInfo
        }
    }

    private fun createMockInfoDto(nextPage: String? = null): InfoDto {
        return mock<InfoDto> {
            on { next } doReturn nextPage
        }
    }

    private fun createMockRemoteKeyWithNext(nextPage: Int): RemoteKeyEntity {
        return mock<RemoteKeyEntity> {
            on { this.nextPage } doReturn nextPage
        }
    }

    private fun createMockRemoteKeyWithoutNext(): RemoteKeyEntity {
        return mock<RemoteKeyEntity> {
            on { nextPage } doReturn null
        }
    }

    // Helper methods
    private fun createPagingState(
        anchorPosition: Int? = 0,
        pages: List<PagingSource.LoadResult.Page<Int, CharacterEntity>> = listOf()
    ): PagingState<Int, CharacterEntity> {
        return PagingState(
            pages = pages,
            anchorPosition = anchorPosition,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )
    }

    private fun createMockPagingState(): PagingState<Int, CharacterEntity> {
        return createPagingState(anchorPosition = 0)
    }

    private fun createEmptyPagingState(): PagingState<Int, CharacterEntity> {
        return createPagingState(anchorPosition = null)
    }

    private fun createPagingStateWithData(character: CharacterEntity): PagingState<Int, CharacterEntity> {
        val page = mock<PagingSource.LoadResult.Page<Int, CharacterEntity>> {
            on { data } doReturn listOf(character)
        }
        return PagingState(
            pages = listOf(page),
            anchorPosition = 0,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )
    }
}