package com.techuntried.musicplayer.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.techuntried.musicplayer.data.models.PlaylistEntity
import com.techuntried.musicplayer.data.models.SongEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface SongsDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSong(songEntity: SongEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Query("SELECT * FROM songs")
    fun getSongs(): Flow<List<SongEntity>>

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

