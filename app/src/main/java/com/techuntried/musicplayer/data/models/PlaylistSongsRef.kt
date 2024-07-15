package com.techuntried.musicplayer.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = ["playlistId", "songId"])
data class PlaylistSongsRef(
    @ColumnInfo(name = "playlistId") val playlistId: Long,
    @ColumnInfo(name = "songId") val songId: Long
)
