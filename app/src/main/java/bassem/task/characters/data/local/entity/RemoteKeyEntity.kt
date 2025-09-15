package bassem.task.characters.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey val characterId: Int,
    val prevPage: Int?,
    val nextPage: Int?
)