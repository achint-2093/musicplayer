package com.techuntried.musicplayer.ui.player

import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.Looper
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
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.techuntried.musicplayer.data.models.SongEntity
import com.techuntried.musicplayer.data.repository.RoomRepository
import com.techuntried.musicplayer.utils.Constants
import com.techuntried.musicplayer.utils.PlayerService
import com.techuntried.musicplayer.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewmodel @Inject constructor(
    private val roomRepository: RoomRepository, private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val songId = savedStateHandle.get<Long>("songId")
    private val playlistId = savedStateHandle.get<Long>("playlistId")

    private val _currentSong = MutableStateFlow<Response<SongEntity>>(Response.Loading())
    val currentSong: StateFlow<Response<SongEntity>>
        get() = _currentSong

    private val _currentSongIndex = MutableStateFlow<Int>(0)
    val currentSongIndex: StateFlow<Int>
        get() = _currentSongIndex

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

    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private lateinit var controller: MediaController
    private val handler = Handler(Looper.getMainLooper())


    private fun fetchSongs(songId: Long?, playlistId: Long?) {
        viewModelScope.launch {
            if (songId != null && playlistId != null) {
                if (playlistId == Constants.PLAYLIST_ID_ALL) {
                    val songs = roomRepository.getAllSongs()
                    _playlist.value = songs
                    val song = songs.find { it.id == songId }
                    val songIndex = songs.indexOf(song)
                    _currentSongIndex.value = songIndex
                    //_currentSong.value = Response.Success(song)
                } else {
                    val songs = roomRepository.getPlaylistSongs(playlistId)
                    _playlist.value = songs
                    val song = songs.find { it.id == songId }
                    val songIndex = songs.indexOf(song)
                    _currentSongIndex.value = songIndex
                }
            }
            setMediaItems()
        }
    }

    fun initializeController(context: Context) {
        val sessionToken = SessionToken(context, ComponentName(context, PlayerService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener({
            controller = controllerFuture.get()
            controller.playWhenReady = false
            fetchSongs(songId, playlistId)
            setUi()
            listeners()
        }, MoreExecutors.directExecutor())
    }

    private fun setUi() {
        checkPlaybackPosition(100)
    }

    private fun listeners() {
        controller.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                if (events.containsAny(
                        Player.EVENT_PLAY_WHEN_READY_CHANGED,
                        Player.EVENT_PLAYBACK_STATE_CHANGED,
                        Player.EVENT_PLAYBACK_SUPPRESSION_REASON_CHANGED
                    )
                ) {
                    _shouldShowPlayButton.value = Util.shouldShowPlayButton(player)
//                    if (player.currentMediaItem != null)
//                        if (player.duration != C.TIME_UNSET)
//                            _duration.value = player.duration
                    if (player.currentMediaItem != null && player.duration != C.TIME_UNSET) {
                        _duration.value = player.duration
                    }
                }
            }


            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            }

        })
    }

    fun previousMediaItem() {
        if (currentSongIndex.value > 0) {
            _currentSongIndex.value=currentSongIndex.value-1
            controller.seekTo(currentSongIndex.value, 0)
            _currentSong.value = Response.Success(playlist.value.get(currentSongIndex.value))
        }

    }

    fun nextMediaItem() {
        if (currentSongIndex.value < playlist.value.size - 1) {
            _currentSongIndex.value=currentSongIndex.value+1
            controller.seekTo(currentSongIndex.value, 0)
            _currentSong.value = Response.Success(playlist.value.get(currentSongIndex.value))
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
            controller.addMediaItem(mediaItem)

        }
        controller.seekTo(currentSongIndex.value, 0)
        _currentSong.value = Response.Success(playlist.value.get(currentSongIndex.value))
    }

    private fun checkPlaybackPosition(delayMs: Long): Boolean = handler.postDelayed({
        val currentPosition = controller.currentPosition ?: 0
        _currentPosition.value = currentPosition
        checkPlaybackPosition(delayMs)
    }, delayMs)

    fun handlePlayPauseButton() {
        Util.handlePlayPauseButtonAction(controller)
        _shouldShowPlayButton.value = Util.shouldShowPlayButton(controller)
    }

    fun releaseController() {
        MediaController.releaseFuture(controllerFuture)
    }

    override fun onCleared() {
        super.onCleared()
        releaseController()
    }
}

