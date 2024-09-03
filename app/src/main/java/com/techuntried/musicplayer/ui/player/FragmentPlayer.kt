package com.techuntried.musicplayer.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.techuntried.musicplayer.R
import com.techuntried.musicplayer.databinding.FragmentPlayerBinding
import com.techuntried.musicplayer.utils.Response
import com.techuntried.musicplayer.utils.formatDuration
import com.techuntried.musicplayer.utils.setSongCover
import com.techuntried.musicplayer.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentPlayer : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayerViewmodel by activityViewModels()
    //private val args by navArgs<FragmentPlayerArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPlayerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // viewModel.fetchSongs(args.songId,args.playlistId,args.filterData)
        observers()
        clickListeners()
    }

    private fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentSong.collect { song ->
                    when (song) {
                        is Response.Success -> {
                            val data = song.data
                            data?.let {
//                                setMediaItem(data)
                                binding.playerLayout.visibility = View.VISIBLE
                                binding.MusicName.text = it.songName
                                binding.ArtistName.text = it.artist
                                setSongCover(binding.musicImage, it.albumId)
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
                    binding.seekbar.max = it.toInt()
                    binding.duration.text = it.formatDuration()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentPosition.collect {
                    binding.seekbar.progress = it.toInt()
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
        }
        binding.skipNext.setOnClickListener {
            viewModel.nextMediaItem()
        }
        binding.skipPrevious.setOnClickListener {
            viewModel.previousMediaItem()
        }
//        binding.shuffleButton.setOnClickListener {
//            viewModel.toggleShuffle()
//            Toast.makeText(context, "shuffle", Toast.LENGTH_SHORT).show()
//        }
//        binding.repeatButton.setOnClickListener {
//            viewModel.toggleRepeat()
//        }

        binding.favouriteIcon.setOnClickListener {

        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}