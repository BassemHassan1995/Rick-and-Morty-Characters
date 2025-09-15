package bassem.task.characters.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import bassem.task.characters.data.remote.api.CharacterApiService
import bassem.task.characters.domain.model.Character
import bassem.task.characters.data.mapper.toDomain

class CharacterSearchPagingSource (
    private val api: CharacterApiService,
    private val name: String
) : PagingSource<Int, Character>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        val page = params.key ?: 1
        return try {
            val response = api.getCharacters(page = page, name = name)
            val characters = response.results.map { it.toDomain() }
            val nextKey = if (response.info.next != null) page + 1 else null
            val prevKey = if (page > 1) page - 1 else null
            LoadResult.Page(
                data = characters,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}

