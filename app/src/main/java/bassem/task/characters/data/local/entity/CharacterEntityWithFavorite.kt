package bassem.task.characters.data.local.entity

import androidx.room.Embedded

data class CharacterEntityWithFavorite(
    @Embedded val character: CharacterEntity,
    val isFavorite: Boolean
)
