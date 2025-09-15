package bassem.task.characters.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import bassem.task.characters.data.local.dao.CharacterDao
import bassem.task.characters.data.local.entity.CharacterEntity

import bassem.task.characters.data.local.dao.RemoteKeysDao
import bassem.task.characters.data.local.entity.RemoteKeyEntity

@Database(
    entities = [CharacterEntity::class, RemoteKeyEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}