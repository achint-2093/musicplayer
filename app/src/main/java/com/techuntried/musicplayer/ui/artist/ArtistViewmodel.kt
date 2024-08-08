package com.techuntried.musicplayer.ui.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.musicplayer.data.models.ArtistModel
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
class ArtistViewmodel @Inject constructor(
    private val roomRepository: RoomRepository,
) :
    ViewModel() {

    private val _artists = MutableStateFlow<Response<List<ArtistModel>>>(Response.Loading())
    val artists: StateFlow<Response<List<ArtistModel>>>
        get() = _artists


    init {
        fetchArtists()
    }


    private fun fetchArtists() {
        viewModelScope.launch {
            try {
                roomRepository.getArtists().collect {
                    val artists = it.map { artistName ->
                        ArtistModel(0, artistName)
                    }
                    _artists.value = Response.Success(artists)
                }
            } catch (e: Exception) {
                _artists.value = Response.Error(e.message ?: "An error occurred")
            }
        }
    }

}

