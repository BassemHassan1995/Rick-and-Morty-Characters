package bassem.task.characters.domain.usecase

import bassem.task.characters.domain.repository.CharacterRepository
import bassem.task.characters.domain.model.Character
import javax.inject.Inject

class GetCharacterByIdUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(id: Int): Character? {
        return repository.getCharacterById(id)
    }
}