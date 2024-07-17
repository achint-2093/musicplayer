package com.techuntried.musicplayer.data.models


data class SongPickerModel(
    val songId: Long,
    val name: String,
    val artistName:String,
    var isInPlaylist: Boolean = false
)
