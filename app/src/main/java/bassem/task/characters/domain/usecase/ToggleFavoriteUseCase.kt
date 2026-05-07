package bassem.task.characters.domain.usecase

import bassem.task.characters.domain.repository.CharacterRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(characterId: Int) {
        repository.toggleFavorite(characterId)
    }
}