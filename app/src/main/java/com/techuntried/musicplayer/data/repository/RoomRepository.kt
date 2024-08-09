package com.techuntried.musicplayer.data.repository

import com.techuntried.musicplayer.data.database.PlaylistSongsDao
import com.techuntried.musicplayer.data.database.PlaylistsDao
import com.techuntried.musicplayer.data.database.SongsDao
import com.techuntried.musicplayer.data.models.AlbumModel
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

    fun getAlbums(): Flow<List<AlbumModel>> {
        return songsDao.getAlbums().flowOn(Dispatchers.IO)
    }

    fun getArtists(): Flow<List<String>> {
        return songsDao.getArtists().flowOn(Dispatchers.IO)
    }

    fun getSongs(): Flow<List<SongEntity>> {
        return songsDao.getSongs().flowOn(Dispatchers.IO)
    }

    suspend fun getAllSongs(): List<SongEntity> {
        return withContext(Dispatchers.IO) {
            songsDao.getAllSongs()
        }
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

    fun getPlaylistSongsFlow(playlistId: Long): Flow<List<SongEntity>> {
        return playlistSongsDao.getSongsForPlaylistFlow(playlistId).flowOn(Dispatchers.IO)
    }

    fun getArtistSongsFlow(artistName: String): Flow<List<SongEntity>> {
        return songsDao.getArtistSongsFlow(artistName).flowOn(Dispatchers.IO)
    }

    suspend fun getArtistSongs(artistName: String): List<SongEntity> {
        return withContext(Dispatchers.IO) {
            songsDao.getArtistSongs(artistName)
        }
    }

    fun getAlbumSongsFlow(albumName: String): Flow<List<SongEntity>> {
        return songsDao.getAlbumSongsFlow(albumName).flowOn(Dispatchers.IO)
    }

    suspend fun getAlbumSongs(albumName: String): List<SongEntity> {
        return withContext(Dispatchers.IO) {
            songsDao.getAlbumSongs(albumName)
        }
    }

    suspend fun getPlaylistSongs(playlistId: Long): List<SongEntity> {
        return withContext(Dispatchers.IO) {
            playlistSongsDao.getSongsForPlaylist(playlistId)
        }
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