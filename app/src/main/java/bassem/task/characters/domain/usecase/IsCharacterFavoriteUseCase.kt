package bassem.task.characters.domain.usecase

import bassem.task.characters.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsCharacterFavoriteUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(characterId: Int): Flow<Boolean> {
        return repository.isFavorite(characterId)
    }
}