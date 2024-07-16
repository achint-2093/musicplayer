package com.techuntried.musicplayer.ui.player

import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _song = MutableStateFlow<Response<SongEntity>>(Response.Loading())
    val song: StateFlow<Response<SongEntity>>
        get() = _song

    private val _currentPosition = MutableStateFlow<Long>(0L)
    val currentPosition: StateFlow<Long>
        get() = _currentPosition

    private val _duration = MutableStateFlow<Long>(0L)
    val duration: StateFlow<Long>
        get() = _duration

    private val _shouldShowPlayButton = MutableStateFlow<Boolean>(true)
    val shouldShowPlayButton: StateFlow<Boolean>
        get() = _shouldShowPlayButton

    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private lateinit var controller: MediaController
    private val handler = Handler(Looper.getMainLooper())

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

    fun initializeController(context: Context) {
        val sessionToken = SessionToken(context, ComponentName(context, PlayerService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener({
            controller = controllerFuture.get()
            fetchSong(songId)
            setMediaItem(song.value.data!!)
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
                    _duration.value = player.duration
                }
            }
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            }

        })
    }

    private fun setMediaItem(songEntity: SongEntity) {
        val mediaMetadata =
            MediaMetadata.Builder().setTitle(songEntity.songName).setArtist(songEntity.artist)
                .setArtworkUri(songEntity.uri.toUri()).build()
        val mediaItem =
            MediaItem.Builder().setMediaMetadata(mediaMetadata).setMediaId(songEntity.id.toString())
                .setUri(songEntity.uri).build()

        controller.setMediaItem(mediaItem)
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

