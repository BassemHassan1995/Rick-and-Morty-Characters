package bassem.task.characters.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import bassem.task.characters.data.remote.api.CharacterApiService
import bassem.task.characters.data.remote.dto.CharacterDto
import retrofit2.HttpException
import javax.inject.Inject

class CharacterSearchPagingSource @Inject constructor(
    private val api: CharacterApiService,
    private val name: String
) : PagingSource<Int, CharacterDto>() {

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterDto> {
        val page = params.key ?: STARTING_PAGE_INDEX

        return try {
            val response = api.getCharacters(page = page, name = name)
            val characters = response.results
            val nextKey = if (response.info.next != null) page + 1 else null
            val prevKey = if (page > STARTING_PAGE_INDEX) page - 1 else null
            LoadResult.Page(
                data = characters,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (exception: HttpException) {
            if (exception.code() == 404) {
                // No results found for the search query
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            } else {
                LoadResult.Error(exception)
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CharacterDto>): Int? {
        // Try to find the page key of the closest loaded page to the anchor position
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}