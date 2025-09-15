package bassem.task.characters.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import bassem.task.characters.data.local.dao.CharacterDao
import bassem.task.characters.data.mapper.toDomain
import bassem.task.characters.data.mediator.CharacterRemoteMediator
import bassem.task.characters.data.remote.api.CharacterApiService
import bassem.task.characters.data.remote.paging.CharacterSearchPagingSource
import bassem.task.characters.domain.model.Character
import bassem.task.characters.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class CharacterRepositoryImpl @Inject constructor(
    private val api: CharacterApiService,
    private val dao: CharacterDao,
    private val remoteMediator: CharacterRemoteMediator
) : CharacterRepository {

    companion object {
        private const val PAGE_SIZE = 20
        private const val PREFETCH_DISTANCE = 5
        private const val INITIAL_LOAD_SIZE = 40
    }

    override fun getCharacters(name: String?): Flow<PagingData<Character>> {
        return if (name.isNullOrBlank()) {
            val pagingSourceFactory = { dao.getCharacters() }
            Pager(
                config = PagingConfig(
                    pageSize = PAGE_SIZE,
                    enablePlaceholders = false,
                    prefetchDistance = PREFETCH_DISTANCE,
                    initialLoadSize = INITIAL_LOAD_SIZE
                ),
                remoteMediator = remoteMediator,
                pagingSourceFactory = pagingSourceFactory
            ).flow
                .map { pagingData ->
                    pagingData.map { entity -> entity.toDomain() }
                }
        } else {
            Pager(
                config = PagingConfig(
                    pageSize = PAGE_SIZE,
                    enablePlaceholders = false,
                    prefetchDistance = PREFETCH_DISTANCE,
                    initialLoadSize = INITIAL_LOAD_SIZE
                ),
                pagingSourceFactory = { CharacterSearchPagingSource(api, name) }
            ).flow
        }
    }

    override suspend fun getCharacterById(id: Int): Character? {
        // First try to get from cache
        return try {
            dao.getCharacterById(id)?.toDomain()
        } catch (e: Exception) {
            // If cache fails, try API
            try {
                api.getCharacterById(id).toDomain()
            } catch (apiException: Exception) {
                null
            }
        }
    }

}