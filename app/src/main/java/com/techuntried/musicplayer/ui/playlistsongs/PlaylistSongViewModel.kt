package com.techuntried.musicplayer.ui.playlistsongs

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.musicplayer.data.models.SongEntity
import com.techuntried.musicplayer.data.repository.RoomRepository
import com.techuntried.musicplayer.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistSongViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _playlistSongs = MutableStateFlow<Response<List<SongEntity>>>(Response.Loading())
    val playlistSongs: StateFlow<Response<List<SongEntity>>>
        get() = _playlistSongs
    private var playlistId = savedStateHandle.get<Long>("playlistId")

    init {
        getPlaylistSongs(playlistId = playlistId)
    }

    private fun getPlaylistSongs(playlistId: Long?) {
        viewModelScope.launch {
            if (playlistId != null) {
                val songs = roomRepository.getPlaylistSongs(playlistId = playlistId)
                _playlistSongs.value = Response.Success(songs)
            }
        }
    }

}