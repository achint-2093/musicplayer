package com.techuntried.musicplayer.data.repository

import com.techuntried.musicplayer.data.database.PlaylistSongsDao
import com.techuntried.musicplayer.data.database.PlaylistsDao
import com.techuntried.musicplayer.data.database.SongsDao
import com.techuntried.musicplayer.data.models.PlaylistEntity
import com.techuntried.musicplayer.data.models.PlaylistSongsRef
import com.techuntried.musicplayer.data.models.SongEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepository @Inject constructor(
    private val songsDao: SongsDao,
    private val playlistsDao: PlaylistsDao,
    private val playlistSongsDao: PlaylistSongsDao
) {

    fun getAllSongs(): Flow<List<SongEntity>> {
        return songsDao.getSongs().flowOn(Dispatchers.IO)
    }

    suspend fun getSong(songId: Long): SongEntity {
        return withContext(Dispatchers.IO) {
            songsDao.getSongById(songId)
        }
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
        return playlistsDao.getPlaylistFlow(searchQuery).flowOn(Dispatchers.IO)
    }

    suspend fun deletePlaylist(playlistEntity: PlaylistEntity) {
        withContext(Dispatchers.IO) {
            playlistsDao.deletePlaylist(playlistEntity)
        }
    }

    fun getPlaylistSongs(playlistId: Long): Flow<List<SongEntity>> {
        return playlistSongsDao.getSongsForPlaylist(playlistId).flowOn(Dispatchers.IO)
    }

    suspend fun insertSongToPlaylist(playlistId: Long, songId: Long) {
        withContext(Dispatchers.IO) {
            playlistSongsDao.insert(PlaylistSongsRef(playlistId, songId))
        }
    }

    suspend fun deleteSongFromPlaylist(playlistId: Long, songId: Long) {
        withContext(Dispatchers.IO) {
            playlistSongsDao.delete(PlaylistSongsRef(playlistId, songId))
        }
    }

}