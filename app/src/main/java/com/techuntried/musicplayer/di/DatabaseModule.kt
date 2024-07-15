package com.techuntried.musicplayer.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.techuntried.musicplayer.data.database.MyAppDatabase
import com.techuntried.musicplayer.utils.Constants.USER_PREFERENCES
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideMyAppDb(
        @ApplicationContext context: Context
    ): MyAppDatabase {

        return Room.databaseBuilder(context, MyAppDatabase::class.java, "myAppDb")
            .build()
    }

    @Provides
    fun provideSongDao(db: MyAppDatabase) = db.songDao()

    @Provides
    fun providePlaylistsDao(db: MyAppDatabase) = db.playlistsDao()

    @Provides
    fun providePlaylistSongsDao(db: MyAppDatabase) = db.playlistsSongsDao()

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            produceFile = { appContext.preferencesDataStoreFile(USER_PREFERENCES) }
        )
    }
}
