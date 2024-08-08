package com.techuntried.musicplayer.ui.filter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techuntried.musicplayer.data.models.SongEntity
import com.techuntried.musicplayer.data.repository.RoomRepository
import com.techuntried.musicplayer.utils.FilterType
import com.techuntried.musicplayer.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _filteredSongs = MutableStateFlow<Response<List<SongEntity>>>(Response.Loading())
    val filteredSongs: StateFlow<Response<List<SongEntity>>>
        get() = _filteredSongs

    private var filter = savedStateHandle.get<FilterType>("filter")
    private var filterData = savedStateHandle.get<String>("filterData")

    init {
        fetchFilterSongs(filter, filterData)
    }

    private fun fetchFilterSongs(filter: FilterType?, filterData: String?) {
        viewModelScope.launch {
            if (filter != null && filterData != null) {
                when (filter) {
                    FilterType.Artist -> {
                        roomRepository.getArtistSongsFlow(filterData).collect {
                            _filteredSongs.value = Response.Success(it)
                        }
                    }

                    FilterType.Album -> {
                        roomRepository.getAlbumSongsFlow(filterData).collect {
                            _filteredSongs.value = Response.Success(it)
                        }
                    }
                }
            }
        }
    }

}