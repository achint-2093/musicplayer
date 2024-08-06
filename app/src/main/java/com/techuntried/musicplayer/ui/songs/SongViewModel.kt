package com.techuntried.musicplayer.ui.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.musicplayer.data.models.SongEntity
import com.techuntried.musicplayer.data.repository.DataStoreRepository
import com.techuntried.musicplayer.data.repository.RoomRepository
import com.techuntried.musicplayer.data.repository.SongsRepository
import com.techuntried.musicplayer.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
    private val roomRepository: RoomRepository,
    private val dataStoreRepository: DataStoreRepository
) :
    ViewModel() {

    private val _songs = MutableStateFlow<Response<List<SongEntity>>>(Response.Loading())
    val songs: StateFlow<Response<List<SongEntity>>>
        get() = _songs


    init {
        fetchMusicFiles()
    }

    fun refreshSongs() {
        viewModelScope.launch {
            try {
                songsRepository.refreshSongs()
            } catch (e: Exception) {
                _songs.value = Response.Error(e.message.toString())
            }
        }
    }

    private fun fetchMusicFiles() {
        viewModelScope.launch {
            try {
                val isFirstTime = dataStoreRepository.isFirstTime() ?: true
                if (isFirstTime) {
                    songsRepository.updateSongs()
                    dataStoreRepository.saveFirstTime(false)
                }
                roomRepository.getSongs().collect {
                    _songs.value = Response.Success(it)
                }
            } catch (e: Exception) {
                _songs.value = Response.Error(e.message ?: "An error occurred")
            }
        }
    }

}

