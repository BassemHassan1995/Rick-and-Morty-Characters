package bassem.task.characters.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import bassem.task.characters.data.local.dao.CharacterDao
import bassem.task.characters.data.local.dao.FavoriteDao
import bassem.task.characters.data.local.entity.CharacterEntityWithFavorite
import bassem.task.characters.data.local.entity.FavoriteEntity
import bassem.task.characters.data.mapper.toDomain
import bassem.task.characters.data.mediator.CharacterRemoteMediator
import bassem.task.characters.data.remote.api.CharacterApiService
import bassem.task.characters.data.remote.paging.CharacterSearchPagingSource
import bassem.task.characters.data.remote.utils.toApiException
import bassem.task.characters.domain.model.Character
import bassem.task.characters.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class CharacterRepositoryImpl @Inject constructor(
    private val api: CharacterApiService,
    private val dao: CharacterDao,
    private val favoriteDao: FavoriteDao,
    private val remoteMediator: CharacterRemoteMediator
) : CharacterRepository {

    companion object {
        private const val PAGE_SIZE = 20
        private const val PREFETCH_DISTANCE = 5
        private const val INITIAL_LOAD_SIZE = PAGE_SIZE

        val pagingConfig = PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = false,
            prefetchDistance = PREFETCH_DISTANCE,
            initialLoadSize = INITIAL_LOAD_SIZE
        )
    }

    override fun getCharacters(name: String?): Flow<PagingData<Character>> {
        return if (name.isNullOrBlank()) {
            val pagingSourceFactory = { dao.getCharactersWithFavorite() }
            Pager(
                config = pagingConfig,
                remoteMediator = remoteMediator,
                pagingSourceFactory = pagingSourceFactory
            ).flow
                .map { pagingData ->
                    pagingData.map { entityWithFavorite ->
                        entityWithFavorite.character.toDomain(isFavorite = entityWithFavorite.isFavorite)
                    }
                }
        } else {
            val pagingSourceFactory = { CharacterSearchPagingSource(api, favoriteDao, name) }
            Pager(
                config = pagingConfig,
                pagingSourceFactory = pagingSourceFactory
            ).flow
        }
    }

    override suspend fun getCharacterById(id: Int): Character? {
        // First try DB
        val local = dao.getCharacterById(id)
        val isFavorite = favoriteDao.isFavorite(id).first()
        // Only call API if DB is empty
        return local?.toDomain(isFavorite) ?: getCharacterFromApi(id, isFavorite)
    }

    override suspend fun toggleFavorite(characterId: Int) {
        val isFav = favoriteDao.isFavorite(characterId).first()
        if (isFav) {
            favoriteDao.deleteFavorite(characterId)
        } else {
            favoriteDao.insertFavorite(FavoriteEntity(characterId))
        }
    }

    override fun isFavorite(id: Int): Flow<Boolean> {
        return favoriteDao.isFavorite(id)
    }

    override fun getFavoriteCharacters(): Flow<PagingData<Character>> {
        val pagingSourceFactory = { dao.getFavoriteCharacters() }
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = pagingSourceFactory
        ).flow
            .map { pagingData ->
                pagingData.map { entity -> entity.toDomain(isFavorite = true) }
            }
    }

    private suspend fun getCharacterFromApi(id: Int, isFavorite: Boolean): Character? {
        return try {
            api.getCharacterById(id).toDomain(isFavorite)
        } catch (e: Exception) {
            throw e.toApiException()
        }
    }

}
