package bassem.task.characters.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import bassem.task.characters.data.local.dao.CharacterDao
import bassem.task.characters.data.local.entity.CharacterEntity

import bassem.task.characters.data.local.dao.RemoteKeysDao
import bassem.task.characters.data.local.entity.RemoteKeyEntity

import bassem.task.characters.data.local.dao.FavoriteDao
import bassem.task.characters.data.local.entity.FavoriteEntity

@Database(
    entities = [CharacterEntity::class, RemoteKeyEntity::class, FavoriteEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun remoteKeysDao(): RemoteKeysDao
    abstract fun favoriteDao(): FavoriteDao
}