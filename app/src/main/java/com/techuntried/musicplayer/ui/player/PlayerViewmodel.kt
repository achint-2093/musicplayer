package com.techuntried.musicplayer.ui.player

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.techuntried.musicplayer.data.models.SongEntity
import com.techuntried.musicplayer.data.repository.DataStoreRepository
import com.techuntried.musicplayer.data.repository.RoomRepository
import com.techuntried.musicplayer.utils.Constants
import com.techuntried.musicplayer.utils.MediaControllerObject
import com.techuntried.musicplayer.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewmodel @Inject constructor(
    private val roomRepository: RoomRepository,
    private val savedStateHandle: SavedStateHandle,
    private val dataStoreRepository: DataStoreRepository,
    private val mediaControllerFuture: ListenableFuture<MediaController>
) : ViewModel() {

    private var savedSongId: Long? = null
    private var savedPlaylistId: Long? = null
    private var savedFilterData: String? = null

    private val _currentSong = MutableStateFlow<Response<SongEntity>>(Response.Loading())
    val currentSong: StateFlow<Response<SongEntity>>
        get() = _currentSong

    private val currentSongIndex = MutableStateFlow(0)

    private val _playlist = MutableStateFlow<List<SongEntity>>(emptyList())
    val playlist: StateFlow<List<SongEntity>>
        get() = _playlist

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long>
        get() = _currentPosition

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long>
        get() = _duration

    private val _shouldShowPlayButton = MutableStateFlow(true)
    val shouldShowPlayButton: StateFlow<Boolean>
        get() = _shouldShowPlayButton

    private var mediaController: MediaController? = null
    private val handler = Handler(Looper.getMainLooper())


    init {
        viewModelScope.launch {
            MediaControllerObject.initialize(mediaControllerFuture)
            mediaController = MediaControllerObject.getMediaController()
            Log.d("MYDEBUG", "get med $mediaController")
            val lastSongId = dataStoreRepository.getCurrentSong()
            val lastPlaylistId = dataStoreRepository.getCurrentPlaylist()
            val lastFilterData = dataStoreRepository.getCurrentFilterData()
            if (lastPlaylistId != null && lastSongId != null && lastFilterData != null) {
                fetchSongs(lastSongId, lastPlaylistId, lastFilterData)
            }
            listeners()
            setUi()
        }
    }


    fun fetchSongs(songId: Long, playlistId: Long, filterData: String) {
        savedSongId = songId
        savedPlaylistId = playlistId
        savedFilterData = filterData
        viewModelScope.launch {

            if (playlistId == Constants.PLAYLIST_ID_ALL) {
                val songs = roomRepository.getAllSongs()
                _playlist.value = songs
            } else if (playlistId == Constants.PLAYLIST_ID_ALBUM) {
                val songs = roomRepository.getAlbumSongs(filterData)
                _playlist.value = songs
            } else if (playlistId == Constants.PLAYLIST_ID_ARTIST) {
                val songs = roomRepository.getArtistSongs(filterData)
                _playlist.value = songs
            } else {
                val songs = roomRepository.getPlaylistSongs(playlistId)
                _playlist.value = songs
            }

            val song = playlist.value.find { it.id == songId }
            val songIndex = playlist.value.indexOf(song)
            currentSongIndex.value = songIndex

            val lastSongId = dataStoreRepository.getCurrentSong()
            val lastPlaylistId = dataStoreRepository.getCurrentPlaylist()
            mediaController?.let {
                if (!it.isPlaying || lastSongId != songId || lastPlaylistId != playlistId) {
                    Log.d("MYDEBUG", "different")
                    it.clearMediaItems()
                    setMediaItems()
                } else {
                    playCurrentSong()
                }
            }
        }
    }


    private fun setUi() {
        checkPlaybackPosition(100)
    }

    private fun listeners() {
        mediaController?.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                if (events.containsAny(
                        Player.EVENT_PLAY_WHEN_READY_CHANGED,
                        Player.EVENT_PLAYBACK_STATE_CHANGED,
                        Player.EVENT_PLAYBACK_SUPPRESSION_REASON_CHANGED
                    )
                ) {
                    _shouldShowPlayButton.value = Util.shouldShowPlayButton(player)
                    if (player.currentMediaItem != null && player.duration != C.TIME_UNSET) {
                        _duration.value = player.duration
                    }
                }
            }


            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                viewModelScope.launch {
                    mediaItem?.let { mediaItem ->
                        val songId = mediaItem.mediaId.toLong()
                        savedSongId = songId
                        val song = playlist.value.find { it.id == songId }
                        val songIndex = playlist.value.indexOf(song)
                        currentSongIndex.value = songIndex
                        playCurrentSong()
                    }
                }
            }

        })
    }

    fun previousMediaItem() {
        if (currentSongIndex.value > 0) {
            mediaController?.seekTo(currentSongIndex.value - 1, 0)
        }

    }

    fun nextMediaItem() {
        if (currentSongIndex.value < playlist.value.size - 1) {
            mediaController?.seekTo(currentSongIndex.value + 1, 0)
        }
    }

    private fun setMediaItems() {
        for (song in playlist.value) {
            val mediaMetadata =
                MediaMetadata.Builder().setTitle(song.songName).setArtist(song.artist)
                    .setArtworkUri(song.uri.toUri()).build()
            val mediaItem =
                MediaItem.Builder().setMediaMetadata(mediaMetadata).setMediaId(song.id.toString())
                    .setUri(song.uri).build()
            mediaController?.addMediaItem(mediaItem)

        }
        mediaController?.seekTo(currentSongIndex.value, 0)
    }

    private fun playCurrentSong() {
        _currentSong.value = Response.Success(playlist.value[currentSongIndex.value])
        viewModelScope.launch {
            savedSongId?.let {
                Log.d("MYDEBUG", "songid$it")
                dataStoreRepository.saveCurrentSong(it)
            }
            savedPlaylistId?.let {
                Log.d("MYDEBUG", "pla $it")
                dataStoreRepository.saveCurrentPlaylist(it)
            }
            savedFilterData?.let {
                dataStoreRepository.saveCurrentFilterData(it)
            }
        }
    }

    private fun checkPlaybackPosition(delayMs: Long): Boolean = handler.postDelayed({
        val currentPosition = mediaController?.currentPosition ?: 0
        _currentPosition.value = currentPosition
        checkPlaybackPosition(delayMs)
    }, delayMs)

    fun handlePlayPauseButton() {
        Util.handlePlayPauseButtonAction(mediaController)
        _shouldShowPlayButton.value = Util.shouldShowPlayButton(mediaController)
    }

}

