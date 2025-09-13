package bassem.task.characters.di


import android.content.Context
import androidx.room.Room
import bassem.task.characters.data.local.AppDatabase
import bassem.task.characters.data.local.dao.CharacterDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "characters_db"
        ).fallbackToDestructiveMigration(false).build()

    @Provides
    @Singleton
    fun provideCharacterDao(db: AppDatabase): CharacterDao = db.characterDao()
}