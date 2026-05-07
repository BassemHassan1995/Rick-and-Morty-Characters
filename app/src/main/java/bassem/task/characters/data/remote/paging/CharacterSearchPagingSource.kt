package bassem.task.characters.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import bassem.task.characters.data.local.dao.FavoriteDao
import bassem.task.characters.data.mapper.toDomain
import bassem.task.characters.data.remote.api.CharacterApiService
import bassem.task.characters.data.remote.utils.toApiException
import bassem.task.characters.domain.model.Character
import kotlinx.coroutines.flow.first

class CharacterSearchPagingSource(
    private val api: CharacterApiService,
    private val favoriteDao: FavoriteDao,
    private val name: String
) : PagingSource<Int, Character>() {

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        val page = params.key ?: STARTING_PAGE_INDEX

        return try {
            val response = api.getCharacters(page = page, name = name)
            val favoriteIds = favoriteDao.getFavoriteIds().first()
            val characters = response.results.map { dto ->
                dto.toDomain(isFavorite = favoriteIds.contains(dto.id))
            }
            val nextKey = if (response.info.next != null) page + 1 else null
            val prevKey = if (page > STARTING_PAGE_INDEX) page - 1 else null
            LoadResult.Page(
                data = characters,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e.toApiException())
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
