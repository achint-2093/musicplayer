package com.techuntried.musicplayer.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.techuntried.musicplayer.R

fun showToast(message: String, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun showSnackBar(view: View, message: String) {
    val snackBar = Snackbar.make(view, message, 500)
    snackBar.setBackgroundTint(view.resources.getColor(R.color.box_background, null))
    snackBar.setTextColor(view.resources.getColor(R.color.text_color, null))
    snackBar.show()
}

enum class PlaylistType {
    Add, Update
}

enum class SongOptions {
    Share, Delete
}
enum class PlaylistOptions {
    Share, Delete,Edit
}

enum class FilterType{
    Artist,Album
}

fun PlaylistType.title(): String {
    return when (this) {
        PlaylistType.Add -> "Add to Playlist"
        PlaylistType.Update -> "Update Playlist"
    }
}

fun Long.formatDuration(): String {
    val durationMillis = this
    val hours = durationMillis / 3600000
    val minutes = (durationMillis % 3600000) / 60000
    val seconds = (durationMillis % 60000) / 1000
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun getAlbumArtUri(albumId: Long): Uri {
    return ContentUris.withAppendedId(
        Uri.parse("content://media/external/audio/albumart"),
        albumId
    )
}