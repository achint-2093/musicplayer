package com.techuntried.musicplayer.ui.playlistsongs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.techuntried.musicplayer.R
import com.techuntried.musicplayer.data.models.SongEntity
import com.techuntried.musicplayer.databinding.FragmentPlaylistSongsBinding
import com.techuntried.musicplayer.ui.bottomsheets.SongOptionsSheet
import com.techuntried.musicplayer.ui.player.PlayerViewmodel
import com.techuntried.musicplayer.ui.songs.SongsAdapter
import com.techuntried.musicplayer.ui.songs.SongsClickListener
import com.techuntried.musicplayer.utils.Response
import com.techuntried.musicplayer.utils.SongOptions
import com.techuntried.musicplayer.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentPlaylistSongs : Fragment(), SongOptionsSheet.BottomSheetCallback {

    private var _binding: FragmentPlaylistSongsBinding? = null
    private val binding get() = _binding!!

    private val args: FragmentPlaylistSongsArgs by navArgs()
    private lateinit var adapter: SongsAdapter
    private val viewModel: PlaylistSongViewModel by viewModels()
    private val playerViewModel: PlayerViewmodel by activityViewModels()
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
        _binding = FragmentPlaylistSongsBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUi()
        setSongsAdapter()
        observers()
        setOnClickListeners()
    }

    private fun setUi() {
        setToolbar()
    }

    private fun setToolbar() {
        binding.toolbar.title = args.playlistName
        binding.toolbar.inflateMenu(R.menu.playlist_songs_menu)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add_songs_action -> {
                    val action =
                        FragmentPlaylistSongsDirections.actionFragmentPlaylistSongsToFragmentSongPicker(
                            args.playlistId
                        )
                    findNavController().navigate(action)
                    true
                }

                else -> false
            }
        }
    }


    private fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlistSongs.collect { playlistSongs ->
                    when (playlistSongs) {
                        is Response.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val data = playlistSongs.data ?: emptyList()
                            if (data.isNotEmpty()) {
                                binding.emptyLayout.visibility = View.GONE
                                binding.playlistSongRecyclerView.visibility = View.VISIBLE
                                adapter.submitList(data)

                            } else {
                                binding.playlistSongRecyclerView.visibility = View.GONE
                                binding.emptyLayout.visibility = View.VISIBLE
                            }
                        }

                        is Response.Error -> {
                            binding.playlistSongRecyclerView.visibility = View.GONE
                            binding.progressBar.visibility = View.GONE
                            binding.emptyLayout.visibility = View.GONE
                            showSnackBar(binding.root, playlistSongs.errorMessage.toString())
                        }

                        is Response.Loading -> {
                            binding.playlistSongRecyclerView.visibility = View.GONE
                            binding.emptyLayout.visibility = View.GONE
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun setSongsAdapter() {
        adapter = SongsAdapter(object : SongsClickListener {
            override fun onClick(songEntity: SongEntity) {
                playerViewModel.fetchSongs(songEntity.id, args.playlistId, "")
//                val action =
//                    FragmentPlaylistSongsDirections.actionFragmentPlaylistSongsToFragmentPlayer(
//                        songEntity.id, args.playlistId,""
//                    )
//                findNavController().navigate(action)
            }

            override fun onMoreClick(songEntity: SongEntity) {
                selectedSong = songEntity
                val songsBottomSheet =
                    SongOptionsSheet.newInstance(songEntity, playlistId = args.playlistId)
                songsBottomSheet.setBottomSheetCallback(songSheetCallback)
                songsBottomSheet.show(parentFragmentManager, "songsBottomSheet")
            }

        })
        binding.playlistSongRecyclerView.adapter = adapter
        binding.playlistSongRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setOnClickListeners() {

    }

//    private fun showDialog(
//        title: String,
//        description: String,
//        buttonTxt: String,
//        playlist: PlaylistsEntity,
//        song: SongEntity
//    ) {
//        val dialog = CustomAlertDialog(requireContext())
//        dialog.setTitle(title)
//        dialog.setDescription(description)
//        dialog.setButton(buttonTxt) {
//            viewModel.deleteSongFromPlaylist(song)
//            dialog.close()
//        }
//        dialog.show()
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onSongOptionSheetDismissed(selectedOption: SongOptions?) {
        selectedOption?.let {

        }
    }

}