package com.techuntried.musicplayer.data.repository

import com.techuntried.musicplayer.data.database.PlaylistsDao
import com.techuntried.musicplayer.data.database.SongsDao
import com.techuntried.musicplayer.data.models.PlaylistEntity
import com.techuntried.musicplayer.data.models.SongModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepository @Inject constructor(
    private val songsDao: SongsDao,
    private val playlistsDao: PlaylistsDao
) {

    fun getAllSongs(): Flow<List<SongModel>> {
        return songsDao.getSongs().map { songEntities ->
            val songsList = songEntities.map {
                SongModel(it.id, it.songName)
            }
            songsList
        }.flowOn(Dispatchers.IO)
    }


    suspend fun insertPlaylist(playlistEntity: PlaylistEntity) {
        withContext(Dispatchers.IO) {
            playlistsDao.insertPlaylist(playlistEntity)
        }
    }

    suspend fun updatePlaylist(playlistEntity: PlaylistEntity) {
        withContext(Dispatchers.IO) {
            playlistsDao.updatePlaylist(playlistEntity)
        }
    }

    fun getPlaylists(searchQuery: String): Flow<List<PlaylistEntity>> {
        return playlistsDao.getPlaylistFlow(searchQuery)
    }

    suspend fun deletePlaylist(playlistEntity: PlaylistEntity) {
        withContext(Dispatchers.IO) {
            playlistsDao.deletePlaylist(playlistEntity)
        }
    }
}