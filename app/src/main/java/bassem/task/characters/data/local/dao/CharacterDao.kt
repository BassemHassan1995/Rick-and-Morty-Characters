package bassem.task.characters.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bassem.task.characters.data.local.entity.CharacterEntity
import bassem.task.characters.data.local.entity.CharacterEntityWithFavorite

@Dao
interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity)

    @Query("SELECT * FROM characters")
    fun getCharacters(): PagingSource<Int, CharacterEntity>

    @Query("""
        SELECT characters.*, favorites.id IS NOT NULL as isFavorite 
        FROM characters 
        LEFT JOIN favorites ON characters.id = favorites.id
    """)
    fun getCharactersWithFavorite(): PagingSource<Int, CharacterEntityWithFavorite>

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getCharacterById(id: Int): CharacterEntity?

    @Query("SELECT * FROM characters INNER JOIN favorites ON characters.id = favorites.id")
    fun getFavoriteCharacters(): PagingSource<Int, CharacterEntity>

    @Query("SELECT COUNT(*) FROM characters")
    suspend fun getCharacterCount(): Int

    @Query("SELECT * FROM characters ORDER BY id DESC LIMIT 1")
    suspend fun getLastCharacter(): CharacterEntity?

    @Query("DELETE FROM characters")
    suspend fun clearAll()
}