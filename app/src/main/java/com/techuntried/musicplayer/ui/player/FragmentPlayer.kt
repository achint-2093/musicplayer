package com.techuntried.musicplayer.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.techuntried.musicplayer.R
import com.techuntried.musicplayer.data.models.SongEntity
import com.techuntried.musicplayer.databinding.FragmentPlayerBinding
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

    //    private val handler = Handler(Looper.getMainLooper())
    private val args by navArgs<FragmentPlayerArgs>()
    private var songs: List<SongEntity>? = null
    private var currentSongIndex: Int? = null
    private var playlistName: String? = null
    private var playlistId: Long? = null
    private var shortPlayback: Boolean? = null

//    private lateinit var controllerFuture: ListenableFuture<MediaController>
//    private lateinit var controller: MediaController


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

//        var controllerIsPlaying = false
//        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                if (fromUser)
//                    controller?.seekTo(progress.toLong())
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {
//                controllerIsPlaying = controller?.isPlaying ?: false
//                controller?.pause()
//            }
//
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                if (controllerIsPlaying)
//                    controller?.play()
//                else
//                    controller?.pause()
//            }
//
//        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //initializeController()
        viewModel.initializeController(requireContext())
        observers()
        clickListeners()
    }

//    fun initializeController() {
//        val context = requireContext()
//        val sessionToken = SessionToken(context, ComponentName(context, PlayerService::class.java))
//        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
//        controllerFuture.addListener({
//            controller = controllerFuture.get()
//            checkPlaybackPosition(100)
//            listeners()
//        }, MoreExecutors.directExecutor())
//
//    }

//    private fun listeners() {
//        controller.addListener(object : Player.Listener {
//
//            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
//            }
//
//        })
//    }

//    private fun setMediaItem(songEntity: SongEntity) {
//        val mediaMetadata = MediaMetadata.Builder()
//            .setTitle(songEntity.songName)
//            .setArtist(songEntity.artist)
//            .setArtworkUri(songEntity.uri.toUri())
//            .build()
//        val mediaItem = MediaItem.Builder()
//            .setMediaMetadata(mediaMetadata)
//            .setMediaId(songEntity.id.toString())
//            .setUri(songEntity.uri)
//            .build()
//
//        controller.setMediaItem(mediaItem)
//    }

    private fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.song.collect { song ->
                    when (song) {
                        is Response.Success -> {
                            val data = song.data
                            data?.let {
//                                setMediaItem(data)
                                binding.playerLayout.visibility = View.VISIBLE
                                binding.MusicName.text = data.songName
                                binding.ArtistName.text = data.artist
                            }
                        }

                        is Response.Error -> {
                            showSnackBar(binding.root, song.errorMessage.toString())
                        }

                        is Response.Loading -> {
                            binding.playerLayout.visibility = View.GONE
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.shouldShowPlayButton.collect {
                    if (it) {
                        binding.playButton.setImageResource(R.drawable.play_icon)
                    } else {
                        binding.playButton.setImageResource(R.drawable.pause_icon)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.duration.collect {
                    binding.seekbar.max=it.toInt()
                    binding.duration.text = it.formatDuration()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentPosition.collect {
                    binding.seekbar.progress=it.toInt()
                    binding.currentPosition.text = it.formatDuration()
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
//    private fun checkPlaybackPosition(delayMs: Long): Boolean = handler.postDelayed({
//        val currentPosition = controller.currentPosition ?: 0
//        binding.currentPosition.text = currentPosition.formatDuration()
//        checkPlaybackPosition(delayMs)
//    }, delayMs)

    private fun clickListeners() {
        binding.playButton.setOnClickListener {
            viewModel.handlePlayPauseButton()
//            Util.handlePlayPauseButtonAction(controller)
//            val shouldShowPlayButton = Util.shouldShowPlayButton(controller)
//            if (shouldShowPlayButton) {
//                binding.playButton.setImageResource(R.drawable.play_icon)
//            } else {
//                binding.playButton.setImageResource(R.drawable.pause_icon)
//            }
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
        //releaseController()
        _binding = null
    }
}