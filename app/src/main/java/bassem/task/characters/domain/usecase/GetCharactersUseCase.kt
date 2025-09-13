package bassem.task.characters.domain.usecase

import bassem.task.characters.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import bassem.task.characters.domain.model.Character
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(page: Int): Flow<List<Character>> {
        return repository.getCharacters(page)
    }
}