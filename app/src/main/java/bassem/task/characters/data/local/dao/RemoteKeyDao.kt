package bassem.task.characters.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bassem.task.characters.data.local.entity.RemoteKeyEntity

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<RemoteKeyEntity>)

    @Query("SELECT * FROM remote_keys WHERE characterId = :id")
    suspend fun remoteKeysCharacterId(id: Int): RemoteKeyEntity?

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}