package bassem.task.characters.domain.usecase

import androidx.paging.PagingData
import bassem.task.characters.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import bassem.task.characters.domain.model.Character
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(name: String? = null): Flow<PagingData<Character>> {
        return repository.getCharacters(name)
    }
}