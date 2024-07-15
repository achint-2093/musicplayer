package com.techuntried.musicplayer.ui.player

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
class PlayerViewmodel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    private val songId = savedStateHandle.get<Long>("songId")

    private val _song = MutableStateFlow<Response<SongEntity>>(Response.Loading())
    val song: StateFlow<Response<SongEntity>>
        get() = _song

    init {
        fetchSong(songId)
    }

    private fun fetchSong(songId: Long?) {
        viewModelScope.launch {
            if (songId != null) {
                val song = roomRepository.getSong(songId)
                _song.value = Response.Success(song)
            }
        }
    }
}

