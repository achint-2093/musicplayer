package com.techuntried.musicplayer.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.techuntried.musicplayer.data.models.PlaylistEntity
import com.techuntried.musicplayer.data.models.PlaylistSongsRef
import com.techuntried.musicplayer.data.models.SongEntity


@Database(
    entities = [SongEntity::class, PlaylistEntity::class,PlaylistSongsRef::class],
    version = 1
)
abstract class MyAppDatabase : RoomDatabase() {

    abstract fun songDao(): SongsDao
    abstract fun playlistsDao(): PlaylistsDao
    abstract fun playlistsSongsDao(): PlaylistSongsDao


}
