package com.techuntried.musicplayer.data.models


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey
    val id: Long,
    @ColumnInfo("songName")
    val songName: String,
    @ColumnInfo("artist")
    val artist: String,
    @ColumnInfo("albumId")
    val albumId: Long,
    @ColumnInfo("album")
    val album: String,
    @ColumnInfo("uri")
    val uri: String
) : Parcelable