package bassem.task.characters.data.repository

import bassem.task.characters.data.local.dao.CharacterDao
import bassem.task.characters.data.remote.api.CharacterApiService
import bassem.task.characters.domain.repository.CharacterRepository

class CharacterRepositoryImpl(
    private val api: CharacterApiService,
    private val dao: CharacterDao
) : CharacterRepository {

}