package com.techuntried.musicplayer.data.repository

import com.techuntried.musicplayer.data.database.SongsDao
import com.techuntried.musicplayer.data.models.SongModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepository @Inject constructor(private val songsDao: SongsDao) {

    fun getAllSongs(): Flow<List<SongModel>> {
        return songsDao.getSongs().map { songEntities ->
            val songsList = songEntities.map {
                SongModel(it.id, it.songName)
            }
            songsList
        }.flowOn(Dispatchers.IO)
    }
}