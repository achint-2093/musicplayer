package com.techuntried.musicplayer.ui.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.musicplayer.data.models.AlbumModel
import com.techuntried.musicplayer.data.models.ArtistModel
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

    private val _albums = MutableStateFlow<Response<List<AlbumModel>>>(Response.Loading())
    val albums: StateFlow<Response<List<AlbumModel>>>
        get() = _albums


    init {
        fetchAlbums()
    }


    private fun fetchAlbums() {
        viewModelScope.launch {
            try {
                roomRepository.getAlbums().collect {
                    val albums = it.map { albumName ->
                        AlbumModel(0, albumName)
                    }
                    _albums.value = Response.Success(albums)
                }
            } catch (e: Exception) {
                _albums.value = Response.Error(e.message ?: "An error occurred")
            }
        }
    }

}

