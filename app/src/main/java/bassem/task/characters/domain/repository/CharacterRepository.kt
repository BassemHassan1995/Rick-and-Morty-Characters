package bassem.task.characters.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import bassem.task.characters.domain.model.Character

interface CharacterRepository {

    fun getCharacters(): Flow<PagingData<Character>>

    suspend fun getCharacterById(id: Int): Character?
}