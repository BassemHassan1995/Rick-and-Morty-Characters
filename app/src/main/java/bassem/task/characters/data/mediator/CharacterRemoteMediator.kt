package bassem.task.characters.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import bassem.task.characters.data.local.AppDatabase
import bassem.task.characters.data.local.entity.CharacterEntity
import bassem.task.characters.data.local.entity.RemoteKeyEntity
import bassem.task.characters.data.mapper.toEntity
import bassem.task.characters.data.remote.api.CharacterApiService
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class CharacterRemoteMediator @Inject constructor(
    private val api: CharacterApiService,
    private val database: AppDatabase
) : RemoteMediator<Int, CharacterEntity>() {

    override suspend fun initialize(): InitializeAction {
        return try {
            // Check if we have any cached characters, skip refresh
            val cachedCount = database.characterDao().getCharacterCount()

            if (cachedCount > 0) {
                InitializeAction.SKIP_INITIAL_REFRESH
            } else {
                InitializeAction.LAUNCH_INITIAL_REFRESH
            }
        } catch (e: Exception) {
            // If error checking cache, do initial refresh
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                    ?: database.characterDao().getLastCharacter()
                    ?: return MediatorResult.Success(endOfPaginationReached = true)

                val remoteKey = database.remoteKeysDao().remoteKeysCharacterId(lastItem.id)
                remoteKey?.nextPage ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        return try {
            val response = api.getCharacters(page)
            val characters = response.results.map { it.toEntity(page) }
            val endOfPaginationReached = response.info.next == null

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.characterDao().clearAll()
                }

                val keys = characters.map {
                    RemoteKeyEntity(
                        characterId = it.id,
                        prevPage = if (page == 1) null else page - 1,
                        nextPage = if (endOfPaginationReached) null else page + 1
                    )
                }

                database.remoteKeysDao().insertAll(keys)
                database.characterDao().insertCharacters(characters)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
