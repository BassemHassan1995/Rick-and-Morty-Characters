package bassem.task.characters.data.mapper

import bassem.task.characters.data.local.entity.CharacterEntity
import bassem.task.characters.data.remote.dto.CharacterDto
import bassem.task.characters.domain.model.Character
import bassem.task.characters.domain.model.CharacterStatus

// Convert from API DTO to Domain Model
fun CharacterDto.toDomain(isFavorite: Boolean = false): Character {
    return Character(
        id = id,
        name = name,
        status = CharacterStatus.fromString(status),
        species = species,
        image = image,
        isFavorite = isFavorite
    )
}

// Convert from API DTO to Entity (for caching)
fun CharacterDto.toEntity(page: Int): CharacterEntity {
    return CharacterEntity(
        id = id,
        name = name,
        status = status,
        species = species,
        image = image,
        page = page
    )
}

// Convert from Entity to Domain Model
fun CharacterEntity.toDomain(isFavorite: Boolean = false): Character {
    return Character(
        id = id,
        name = name,
        status = CharacterStatus.fromString(status),
        species = species,
        image = image,
        isFavorite = isFavorite
    )
}
