package com.techuntried.musicplayer.ui.player

import android.content.ComponentName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.fragment.navArgs
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.techuntried.musicplayer.R
import com.techuntried.musicplayer.data.models.SongEntity
import com.techuntried.musicplayer.databinding.FragmentPlayerBinding
import com.techuntried.musicplayer.utils.PlayerService
import com.techuntried.musicplayer.utils.Response
import com.techuntried.musicplayer.utils.formatDuration
import com.techuntried.musicplayer.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentPlayer : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayerViewmodel by viewModels()
    private val args by navArgs<FragmentPlayerArgs>()
    private var songs: List<SongEntity>? = null
    private var currentSongIndex: Int? = null
    private var playlistName: String? = null
    private var playlistId: Long? = null
    private var shortPlayback: Boolean? = null

    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private val controller: MediaController? get() = if (controllerFuture.isDone) controllerFuture.get() else null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPlayerBinding.inflate(inflater, container, false)

//        shortPlayback = arguments?.getBoolean("short")
//        if (shortPlayback == true) {
//            currentSongIndex =
//                arguments?.getInt("currentSongIndex")
//            playlistName = arguments?.getString("playlistName")
//            playlistId = arguments?.getLong("playlistId")
//        } else {
//            songs = args.parcelSong?.songs
//            currentSongIndex =
//                args.parcelSong?.currentSongIndex
//            playlistName = args.parcelSong?.playListName
//            playlistId = args.parcelSong?.playlistId
//        }


//        if (playlistId == viewModel.currentMusic.value?.playlistId) {
//            Toast.makeText(context, "same playlist", Toast.LENGTH_SHORT).show()
//            if (viewModel.liveSongs.value?.get(currentSongIndex!!)?.songName != viewModel.currentMusic.value?.songName) {
//                viewModel.goToIndex(currentSongIndex!!)
//            }
//        } else {
//            Toast.makeText(context, "diff", Toast.LENGTH_SHORT).show()
//            viewModel.updateSongs(songs!!)
//            viewModel.updatePlaylistName(playlistName!!, playlistId!!)
//            viewModel.initializeController(
//                requireContext(),
//                currentSongIndex!!
//            )
//        }

        var controllerIsPlaying = false
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    controller?.seekTo(progress.toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                controllerIsPlaying = controller?.isPlaying!!
                controller?.pause()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (controllerIsPlaying)
                    controller?.play()
                else
                    controller?.pause()
            }

        })

        observers()
        clickListeners()

        return binding.root
    }

    fun initializeController(songEntity: SongEntity) {
        val context = requireContext()
        controllerFuture = MediaController.Builder(
            context,
            SessionToken(
                context, ComponentName(context, PlayerService::class.java)
            )
        ).buildAsync()
        controllerFuture.addListener({
            setController(songEntity)
        }, MoreExecutors.directExecutor())
    }

    private fun setController(songEntity: SongEntity) {
        val controller = this.controller ?: return
        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(songEntity.songName)
            .setArtist(songEntity.artist)
            .setArtworkUri(songEntity.uri.toUri())
            .build()
        val mediaItem = MediaItem.Builder()
            .setMediaMetadata(mediaMetadata)
            .setMediaId(songEntity.id.toString())
            .setUri(songEntity.uri)
            .build()
        controller.addMediaItem(mediaItem)

        controller.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                if (events.containsAny(
                        Player.EVENT_PLAY_WHEN_READY_CHANGED,
                        Player.EVENT_PLAYBACK_STATE_CHANGED,
                        Player.EVENT_PLAYBACK_SUPPRESSION_REASON_CHANGED
                    )
                ) {
//                    val duration = player.duration
//                    binding.duration.text = duration.formatDuration()
                }
            }


            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
//                updateCurrentMetadata(
//                    context,
//                    mediaItem?.mediaId?.toLong()!!,
//                    mediaItem.mediaMetadata.title.toString(),
//                    mediaItem.mediaMetadata.artist.toString(),
//                    mediaItem.mediaMetadata.artworkUri!!
//                )
//                checkPlaybackPosition(100)
            }

        })
    }

    private fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.song.collect { song ->
                    when (song) {
                        is Response.Success -> {
                            val data = song.data
                            data?.let {
                                initializeController(data)
                                binding.playBackControls.visibility = View.VISIBLE
                                binding.MusicName.text = data.songName
                                binding.ArtistName.text = data.artist
                            }
                        }

                        is Response.Error -> {
                            showSnackBar(binding.root, song.errorMessage.toString())
                        }

                        is Response.Loading -> {
                            binding.playBackControls.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

//    private fun updateRepeatUi(repeatOption: RepeatOption) {
////        when (repeatOption) {
////            RepeatOption.OFF -> {
////                binding.repeatButton.setImageResource(R.drawable.repeat_off_icon)
////            }
////
////            RepeatOption.ON -> {
////                binding.repeatButton.setImageResource(R.drawable.repeat_icon)
////            }
////
////            RepeatOption.ONE -> {
////                binding.repeatButton.setImageResource(R.drawable.repeat_one_icon)
////            }
////
////        }
//
//    }
//
//    private fun updateShuffleUi(shuffleMode: ShuffleMode) {
////        when (shuffleMode) {
////            ShuffleMode.ON -> {
////                binding.shuffleButton.setImageResource(R.drawable.shuffle)
////            }
////
////            ShuffleMode.OFF -> {
////                binding.shuffleButton.setImageResource(R.drawable.repeat_off_icon)
////            }
////        }
//    }

    private fun clickListeners() {
        binding.playButton.setOnClickListener {
            Util.handlePlayPauseButtonAction(controller)
            val shouldShowPlayButton = Util.shouldShowPlayButton(controller)
            if (shouldShowPlayButton) {
                binding.playButton.setImageResource(R.drawable.play_icon)
            } else {
                binding.playButton.setImageResource(R.drawable.pause_icon)
            }
        }
//        binding.skipNext.setOnClickListener {
//            viewModel.nextMediaItem()
//        }
//        binding.skipPrevious.setOnClickListener {
//            viewModel.previousMediaItem()
//        }
//        binding.shuffleButton.setOnClickListener {
//            viewModel.toggleShuffle()
//            Toast.makeText(context, "shuffle", Toast.LENGTH_SHORT).show()
//        }
//        binding.repeatButton.setOnClickListener {
//            viewModel.toggleRepeat()
//        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }
}