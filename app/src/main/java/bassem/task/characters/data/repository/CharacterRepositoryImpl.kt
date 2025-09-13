package bassem.task.characters.data.repository

import bassem.task.characters.data.local.dao.CharacterDao
import bassem.task.characters.data.remote.api.CharacterApiService
import bassem.task.characters.domain.model.Character
import bassem.task.characters.domain.repository.CharacterRepository
import bassem.task.characters.data.mapper.toDomain
import bassem.task.characters.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CharacterRepositoryImpl @Inject constructor(
    private val api: CharacterApiService,
    private val dao: CharacterDao
) : CharacterRepository {

    override fun getCharacters(page: Int): Flow<List<Character>> = flow {
        // Emit cached data first if available
        val cachedCharacters = dao.getCharactersByPage(page).first()
        if (cachedCharacters.isNotEmpty()) {
            emit(cachedCharacters.map { it.toDomain() })
        }

        // Fetch from API and update cache
        try {
            val response = api.getCharacters(page)
            val characters = response.results.map { it.toDomain() }

            // Cache the characters with page info
            dao.insertCharacters(response.results.map { it.toEntity(page) })

            emit(characters)
        } catch (e: Exception) {
            // If API fails and no cache, throw error
            if (cachedCharacters.isEmpty()) {
                throw e
            }
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