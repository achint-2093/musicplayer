package com.techuntried.musicplayer.ui.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.musicplayer.data.models.AlbumModel
import com.techuntried.musicplayer.data.repository.RoomRepository
import com.techuntried.musicplayer.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewmodel @Inject constructor(
    private val roomRepository: RoomRepository,
) : ViewModel() {

    private val _albums = MutableStateFlow<Response<List<AlbumModel>>?>(null)
    val albums: StateFlow<Response<List<AlbumModel>>?>
        get() = _albums


    fun fetchAlbums() {
        viewModelScope.launch {
            try {
                _albums.value=Response.Loading()
                roomRepository.getAlbums().collect {
                    _albums.value = Response.Success(it)
                }
            } catch (e: Exception) {
                _albums.value = Response.Error(e.message ?: "An error occurred")
            }
        }
    }

}

