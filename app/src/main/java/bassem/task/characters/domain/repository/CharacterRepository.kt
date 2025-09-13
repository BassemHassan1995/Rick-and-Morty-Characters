package bassem.task.characters.domain.repository

import kotlinx.coroutines.flow.Flow
import bassem.task.characters.domain.model.Character

interface CharacterRepository {

    fun getCharacters(page: Int): Flow<List<Character>>

    suspend fun getCharacterById(id: Int): Character?
}