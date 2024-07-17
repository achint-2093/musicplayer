package com.techuntried.musicplayer.ui.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.musicplayer.data.models.PlaylistEntity
import com.techuntried.musicplayer.data.repository.RoomRepository
import com.techuntried.musicplayer.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayListsViewModel @Inject constructor(private val roomRepository: RoomRepository) :
    ViewModel() {

    private val _playlists = MutableStateFlow<Response<List<PlaylistEntity>>>(Response.Loading())
    val playlists: StateFlow<Response<List<PlaylistEntity>>>
        get() = _playlists

    private val _playlistAction = MutableStateFlow<Response<String>?>(null)
    val playlistAction: StateFlow<Response<String>?>
        get() = _playlistAction

    init {
        getPlaylists("")
    }

    fun getPlaylists(searchQuery: String) {
        viewModelScope.launch {
            roomRepository.getPlaylists(searchQuery).collect {
                _playlists.value = Response.Success(it)
            }
        }
    }

    fun addPlayList(playlistEntity: PlaylistEntity) {
        viewModelScope.launch {
            roomRepository.insertPlaylist(playlistEntity)
        }

    }

    fun deletePlayList(playlistEntity: PlaylistEntity) {
        viewModelScope.launch {
            try {
                roomRepository.deletePlaylist(playlistEntity)
                _playlistAction.value = Response.Success("Playlist Deleted")
            } catch (e: Exception) {
                _playlistAction.value = Response.Error(e.message.toString())
            }
        }
    }

    fun updatePlayList(playlistEntity: PlaylistEntity) {
        viewModelScope.launch {
            roomRepository.updatePlaylist(playlistEntity)
        }
    }

    fun clearPlaylistAction() {
        _playlistAction.value = null
    }

}