package bassem.task.characters.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import bassem.task.characters.data.local.AppDatabase
import bassem.task.characters.data.mapper.toDomain
import bassem.task.characters.data.mapper.toEntity
import bassem.task.characters.data.mediator.CharacterRemoteMediator
import bassem.task.characters.data.remote.api.CharacterApiService
import bassem.task.characters.domain.model.Character
import bassem.task.characters.domain.repository.CharacterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class CharacterRepositoryImpl @Inject constructor(
    private val api: CharacterApiService,
    private val database: AppDatabase,
) : CharacterRepository {

    override fun getCharacters(): Flow<PagingData<Character>> {
        val pagingSourceFactory = { database.characterDao().getCharacters() }

        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            remoteMediator = CharacterRemoteMediator(api, database),
            pagingSourceFactory = pagingSourceFactory
        ).flow
            .map { pagingData ->
                pagingData.map { entity -> entity.toDomain() }
            }
    }

    override suspend fun getCharacterById(id: Int): Character? {
        // Use IO dispatcher for database operations to avoid main thread blocking
        return withContext(Dispatchers.IO) {
            try {
                // First try to get from database cache
                val cachedCharacter = database.characterDao().getCharacterById(id)
                if (cachedCharacter != null) {
                    return@withContext cachedCharacter.toDomain()
                }

                // If not found in cache, fetch from API
                try {
                    val apiCharacter = api.getCharacterById(id)
                    // Cache the API result in database for future use
                    database.characterDao().insertCharacter(apiCharacter.toEntity(page = 1))
                    apiCharacter.toDomain()
                } catch (apiException: Exception) {
                    null
                }
            } catch (e: Exception) {
                // If database fails completely, try API as fallback
                try {
                    api.getCharacterById(id).toDomain()
                } catch (apiException: Exception) {
                    null
                }
            }
        }
    }

}