package com.techuntried.musicplayer.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.techuntried.musicplayer.data.database.SongsDao
import com.techuntried.musicplayer.data.models.SongEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val songsDao: SongsDao
) {

    suspend fun updateSongs() {
        val songs = fetchMusicFiles()
        insertSongs(songs)
    }

    suspend fun refreshSongs() {
        val songs = fetchMusicFiles()
        val savedSongs = fetchSongsFromDatabase()
        insertSongs(songs)
        val findDeletedSongs = findDeletedSongs(songs, savedSongs)
        removeDeletedSongsFromDatabase(findDeletedSongs)
    }

    private suspend fun fetchMusicFiles(): List<SongEntity> {
        return withContext(Dispatchers.IO) {
            val songs = mutableListOf<SongEntity>()
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID
            )

            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
            val selectionArgs = arrayOf("%/Download/%")
            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
            val cursor = context.contentResolver.query(uri, projection, selection, null, sortOrder)

            cursor?.use { c ->
                val idColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumIdColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val albumColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                /*  val durationColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                 val dataColumn = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)*/

                while (c.moveToNext()) {
                    val id = c.getLong(idColumn)
                    val title = c.getString(titleColumn)
                    val artist = c.getString(artistColumn)
                    val album = c.getString(albumColumn)
                    val albumId = c.getLong(albumIdColumn)
                    /*  val duration = c.getLong(durationColumn)
                    val data = c.getString(dataColumn)*/
                    val selectedAudioUri = ContentUris.withAppendedId(uri, id).toString()
                    val song = SongEntity(
                        id = id,
                        songName = title,
                        artist = artist,
                        albumId = albumId,
                        album = album,
                        uri = selectedAudioUri
                    )
                    songs.add(song)
                }
            }
            songs
        }
    }



private suspend fun fetchSongsFromDatabase(): List<SongEntity> {
    return withContext(Dispatchers.IO) {
        songsDao.getAllSongs()
    }
}

private fun findDeletedSongs(
    deviceSongs: List<SongEntity>,
    databaseSongs: List<SongEntity>
): List<SongEntity> {
    return databaseSongs.filter { dbSong -> deviceSongs.none { it.uri == dbSong.uri } }
}

private suspend fun removeDeletedSongsFromDatabase(deletedSongs: List<SongEntity>) {
    return withContext(Dispatchers.IO) {
        songsDao.deleteSongs(deletedSongs)
    }
}

private suspend fun insertSongs(songs: List<SongEntity>) {
    return withContext(Dispatchers.IO) {
        songsDao.insertSongs(songs)
    }
}
}