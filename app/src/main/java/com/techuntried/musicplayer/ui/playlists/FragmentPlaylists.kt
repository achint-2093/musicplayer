package com.techuntried.musicplayer.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.techuntried.musicplayer.R
import com.techuntried.musicplayer.data.models.PlaylistEntity
import com.techuntried.musicplayer.databinding.FragmentPlaylistsBinding
import com.techuntried.musicplayer.ui.bottomsheets.AddPlaylistSheet
import com.techuntried.musicplayer.utils.CustomAlertDialog
import com.techuntried.musicplayer.utils.PlaylistType
import com.techuntried.musicplayer.utils.Response
import com.techuntried.musicplayer.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentPlaylists : Fragment(), AddPlaylistSheet.BottomSheetCallback {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PlaylistsAdapter
    private val viewModel: PlayListsViewModel by viewModels()

    private lateinit var playlistSheetCallback: AddPlaylistSheet.BottomSheetCallback
    private var createdPlaylistName: String? = null
    // private var deletedItem: PlaylistsEntity? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlistSheetCallback = this

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPlaylistAdapter()
        setClickListeners()
        observers()
        setMenuProvider()
    }

    private fun setMenuProvider() {
        binding.toolbar.inflateMenu(R.menu.playlist_menu)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search_playlists_action -> {

                    true
                }
                R.id.add_playlists_action -> {
                    val playlistSheet = AddPlaylistSheet.newInstance("",PlaylistType.Add)
                    playlistSheet.setBottomSheetCallback(playlistSheetCallback)
                    playlistSheet.show(parentFragmentManager, "AddPlaylistSheet")
                    //  findNavController().navigate(R.id.action_fragmentHome_to_fragmentFavorites)
                    true
                }

                else -> false
            }
        }
    }


    private fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlists.collect { playlists ->
                    when (playlists) {
                        is Response.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val data = playlists.data ?: emptyList()
                            if (data.isNotEmpty()) {
                                binding.playListRecyclerView.visibility = View.VISIBLE
                                adapter.submitList(data)
                            } else {

                            }
                        }


                        is Response.Error -> {
                            binding.playListRecyclerView.visibility = View.GONE
                            binding.progressBar.visibility = View.GONE
                            showSnackBar(binding.root, playlists.errorMessage.toString())
                        }

                        is Response.Loading -> {
                            binding.playListRecyclerView.visibility = View.GONE
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

    }


    private fun setPlaylistAdapter() {
        adapter = PlaylistsAdapter(object : PlaylistClickListener {
            override fun onClick(playlist: PlaylistEntity) {
                TODO("Not yet implemented")
            }

            override fun onMoreClick(playlist: PlaylistEntity) {
                TODO("Not yet implemented")
            }
        })
        binding.playListRecyclerView.adapter = adapter
        binding.playListRecyclerView.layoutManager = LinearLayoutManager(context)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun createPlaylist(playListName: String) {
        viewModel.addPlayList(PlaylistEntity(0, playListName))
    }

//    override fun onDataDismissed(selectedOption: Int, playlist: PlaylistsEntity) {
//        if (selectedOption == 0) {
//            showDeleteDialog(playlist)
//        }
//    }

    private fun setClickListeners() {

    }

    private fun showDeleteDialog(playlist: PlaylistEntity) {
        val title = "Delete"
        val description = "Are you sure"
        val btnText = "Confirm"
        val dialog = CustomAlertDialog(requireContext())
        dialog.setTitle(title)
        dialog.setDescription(description)
        dialog.setButton(btnText) {
            viewModel.deletePlayList(playlist)
            dialog.close()
        }
        dialog.show()
    }


    override fun onPlaylistSheetDismissed(playlistName: String) {
        createPlaylist(playlistName)
    }

}