package com.techuntried.musicplayer.data.repository


import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.techuntried.musicplayer.utils.PreferenceKey
import kotlinx.coroutines.flow.first
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun isFirstTime(): Boolean? {
        return try {
            val preferences = dataStore.data.first()
            preferences[PreferenceKey.KEY_FIRST_TIME]
        } catch (e: IOException) {
            Log.e("getAppTheme", "Error reading preferences", e)
            null
        } catch (e: Exception) {
            Log.e("getAppTheme", "Unexpected error", e)
            null
        }
    }

    suspend fun saveFirstTime(firstTime: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKey.KEY_FIRST_TIME] = firstTime
        }
    }

    suspend fun saveCurrentSong(songId: Long, playlistId: Long) {
        dataStore.edit { preferences ->
            preferences[PreferenceKey.KEY_CURRENT_SONG] = songId
            preferences[PreferenceKey.KEY_CURRENT_PLAYLIST] = playlistId
        }
    }

    suspend fun getCurrentSong(): Pair<Long?, Long?>? {
        return try {
            val preferences = dataStore.data.first()
            val songId = preferences[PreferenceKey.KEY_CURRENT_SONG]
            val playlistId = preferences[PreferenceKey.KEY_CURRENT_PLAYLIST]
            Pair(songId, playlistId)
        } catch (e: IOException) {
            Log.e("getAppTheme", "Error reading preferences", e)
            null
        } catch (e: Exception) {
            Log.e("getAppTheme", "Unexpected error", e)
            null
        }
    }


}