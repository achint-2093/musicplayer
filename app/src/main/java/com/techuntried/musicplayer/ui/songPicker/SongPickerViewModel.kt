package com.techuntried.musicplayer.ui.songPicker

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.musicplayer.data.models.SongPickerModel
import com.techuntried.musicplayer.data.repository.RoomRepository
import com.techuntried.musicplayer.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongPickerViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private val _songList = MutableStateFlow<Response<List<SongPickerModel>>>(Response.Loading())
    val songList: StateFlow<Response<List<SongPickerModel>>>
        get() = _songList

    private val playlistId = savedStateHandle.get<Long>("playlistId") ?: 1

    init {
        getAllSongs()
    }

    private fun getAllSongs() {
        viewModelScope.launch {
            val allSongs = roomRepository.getAllSongs().map {
                SongPickerModel(
                    songId = it.id,
                    name = it.songName,
                    artistName = it.artist
                )
            }

            val alreadyAddedSongs = roomRepository.getPlaylistSongs(playlistId).map { it.id }.toSet()

            val updatedSongs = allSongs.map { song ->
                if (song.songId in alreadyAddedSongs) {
                    song.copy(isInPlaylist = true)
                } else {
                    song
                }
            }

            _songList.value = Response.Success(updatedSongs)

        }
    }


    fun removeSongFromPlaylist(songId: Long) {
        viewModelScope.launch {
            roomRepository.deleteSongFromPlaylist(playlistId = playlistId, songId = songId)
        }
    }

    fun addSongToPlaylist(songId: Long) {
        viewModelScope.launch {
            roomRepository.insertSongToPlaylist(playlistId = playlistId, songId = songId)
        }
    }

//    fun updateSongsWithSelection() {
//        val selection = mutableListOf<SongDataClass>()
//        val data = musicFiles.value!!
//
//        for (music in data) {
//            if (playlistSongs.value?.data?.isEmpty()!!) {
//                selection.add(
//                    SongDataClass(
//                        music.songId,
//                        music.songName,
//                        music.artist,
//                        music.uri,
//                        false
//                    )
//                )
//            } else {
//                val find = playlistSongs.value?.data?.find { it.songId == music.songId }
//                if (find != null) {
//                    selection.add(
//                        SongDataClass(
//                            music.songId,
//                            music.songName,
//                            music.artist,
//                            music.uri,
//                            true
//                        )
//                    )
//                } else {
//                    selection.add(
//                        SongDataClass(
//                            music.songId,
//                            music.songName,
//                            music.artist,
//                            music.uri,
//                            false
//                        )
//                    )
//                }
//            }
//        }
//        _updatedSongs.value = selection
//    }

}