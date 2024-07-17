package com.techuntried.musicplayer.ui.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.techuntried.musicplayer.R
import com.techuntried.musicplayer.data.models.SongEntity
import com.techuntried.musicplayer.databinding.FragmentSongsBinding
import com.techuntried.musicplayer.ui.bottomsheets.SongOptionsSheet
import com.techuntried.musicplayer.utils.Constants
import com.techuntried.musicplayer.utils.Response
import com.techuntried.musicplayer.utils.SongOptions
import com.techuntried.musicplayer.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentSong : Fragment(), SongOptionsSheet.BottomSheetCallback {

    private var _binding: FragmentSongsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SongViewModel by viewModels()
    private lateinit var adapter: SongsAdapter
    private lateinit var selectedSong: SongEntity
    private lateinit var songSheetCallback: SongOptionsSheet.BottomSheetCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        songSheetCallback = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSongsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUi()
        setSongsAdapter()
        setObservers()
        setOnClickListener()
    }

    private fun setUi() {
        binding.toolbar.inflateMenu(R.menu.songs_menu)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.refresh_songs_action -> {
                    viewModel.refreshSongs()
                    true
                }

                else -> false
            }
        }
    }

    private fun setOnClickListener() {

    }

    private fun setObservers() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.songs.collect { songs ->
                    when (songs) {
                        is Response.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val data = songs.data ?: emptyList()
                            if (data.isNotEmpty()) {
                                binding.songsRecyclerView.visibility = View.VISIBLE
                                adapter.submitList(data)

                            } else {
                                binding.songsRecyclerView.visibility = View.GONE
                            }

                        }

                        is Response.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.songsRecyclerView.visibility = View.GONE
                            showSnackBar(binding.root, songs.errorMessage.toString())
                        }

                        is Response.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.songsRecyclerView.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun setSongsAdapter() {
        adapter = SongsAdapter(object : SongsClickListener {

            override fun onClick(songEntity: SongEntity) {
                val action =
                    FragmentSongDirections.actionFragmentSongToFragmentPlayer(
                        songId = songEntity.id, playlistId = Constants.PLAYLIST_ID_ALL
                    )
                findNavController().navigate(action)
            }

            override fun onMoreClick(songEntity: SongEntity) {
                selectedSong = songEntity
                val songsBottomSheet =
                    SongOptionsSheet.newInstance(songEntity.songName, Constants.PLAYLIST_ID_ALL)
                songsBottomSheet.setBottomSheetCallback(songSheetCallback)
                songsBottomSheet.show(parentFragmentManager, "songsBottomSheet")

            }

        })
        binding.songsRecyclerView.adapter = adapter
        binding.songsRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onSongOptionSheetDismissed(selectedOption: SongOptions?) {
        selectedOption?.let {
            when (it) {
                SongOptions.Share -> {

                }

                SongOptions.Delete -> {

                }
            }
        }
    }

}