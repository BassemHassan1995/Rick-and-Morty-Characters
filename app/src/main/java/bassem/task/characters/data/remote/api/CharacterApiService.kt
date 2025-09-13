package bassem.task.characters.data.remote.api

import bassem.task.characters.data.remote.dto.CharacterResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CharacterApiService {

    // Fetch characters with pagination
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int = 1
    ): CharacterResponseDto
}