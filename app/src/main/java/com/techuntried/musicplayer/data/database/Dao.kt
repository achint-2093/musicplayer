package com.techuntried.musicplayer.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.techuntried.musicplayer.data.models.PlaylistEntity
import com.techuntried.musicplayer.data.models.PlaylistSongsRef
import com.techuntried.musicplayer.data.models.SongEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface SongsDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSong(songEntity: SongEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Query("SELECT * FROM songs")
    fun getSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs")
    fun getAllSongs(): List<SongEntity>

    @Query("SELECT * FROM songs where artist=:artistName")
    suspend fun getArtistSongs(artistName: String): List<SongEntity>

    @Query("SELECT * FROM songs where artist=:artistName")
    fun getArtistSongsFlow(artistName: String): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs where album=:albumName")
    suspend fun getAlbumSongs(albumName: String): List<SongEntity>

    @Query("SELECT * FROM songs where album=:albumName")
    fun getAlbumSongsFlow(albumName: String): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE id=:songId")
    fun getSongById(songId: Long): SongEntity

    @Delete
    suspend fun deleteSongs(songEntity: List<SongEntity>)

    @Query("SELECT DISTINCT artist FROM songs")
    fun getArtists(): Flow<List<String>>

    @Query("SELECT DISTINCT album FROM songs")
    fun getAlbums(): Flow<List<String>>

}

@Dao
interface PlaylistsDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity): Long

    @Update
    suspend fun updatePlaylist(playlistEntity: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlistEntity: PlaylistEntity)

    @Query("SELECT * FROM playlists WHERE playListName LIKE '%' || :searchQuery || '%'")
    fun getPlaylistFlow(searchQuery: String): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

}

@Dao
interface PlaylistSongsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playlistSongsRef: PlaylistSongsRef)

    @Query("SELECT * FROM songs INNER JOIN playlistsongsref ON songs.id = playlistsongsref.songId WHERE playlistsongsref.playlistId = :playlistId")
    fun getSongsForPlaylistFlow(playlistId: Long): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs INNER JOIN playlistsongsref ON songs.id = playlistsongsref.songId WHERE playlistsongsref.playlistId = :playlistId")
    fun getSongsForPlaylist(playlistId: Long): List<SongEntity>

    @Query("SELECT * FROM playlists INNER JOIN playlistsongsref ON playlists.id = playlistsongsref.playlistId WHERE playlistsongsref.songId = :songId")
    suspend fun getPlaylistsForSong(songId: Int): List<PlaylistEntity>

    @Delete
    suspend fun delete(playlistSongsRef: PlaylistSongsRef)
}
