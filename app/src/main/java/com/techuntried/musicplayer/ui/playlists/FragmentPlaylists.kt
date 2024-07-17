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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.techuntried.musicplayer.R
import com.techuntried.musicplayer.data.models.PlaylistEntity
import com.techuntried.musicplayer.databinding.FragmentPlaylistsBinding
import com.techuntried.musicplayer.ui.bottomsheets.AddPlaylistSheet
import com.techuntried.musicplayer.ui.bottomsheets.PlaylistOptionSheet
import com.techuntried.musicplayer.utils.CustomAlertDialog
import com.techuntried.musicplayer.utils.PlaylistOptions
import com.techuntried.musicplayer.utils.PlaylistType
import com.techuntried.musicplayer.utils.Response
import com.techuntried.musicplayer.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentPlaylists : Fragment(), AddPlaylistSheet.BottomSheetCallback,
    PlaylistOptionSheet.BottomSheetCallback {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PlaylistsAdapter
    private val viewModel: PlayListsViewModel by viewModels()
    private lateinit var selectedPlaylist: PlaylistEntity
    private lateinit var addPlaylistSheetCallback: AddPlaylistSheet.BottomSheetCallback
    private lateinit var playlistOptionsSheetCallback: PlaylistOptionSheet.BottomSheetCallback


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPlaylistSheetCallback = this
        playlistOptionsSheetCallback = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
                    val playlistSheet = AddPlaylistSheet.newInstance(null, PlaylistType.Add)
                    playlistSheet.setBottomSheetCallback(addPlaylistSheetCallback)
                    playlistSheet.show(parentFragmentManager, "AddPlaylistSheet")
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
                                binding.emptyLayout.visibility = View.GONE
                                binding.playListRecyclerView.visibility = View.VISIBLE
                                adapter.submitList(data)
                            } else {
                                binding.playListRecyclerView.visibility = View.GONE
                                binding.emptyLayout.visibility = View.VISIBLE
                            }
                        }


                        is Response.Error -> {
                            binding.playListRecyclerView.visibility = View.GONE
                            binding.progressBar.visibility = View.GONE
                            binding.emptyLayout.visibility = View.GONE
                            showSnackBar(binding.root, playlists.errorMessage.toString())
                        }

                        is Response.Loading -> {
                            binding.playListRecyclerView.visibility = View.GONE
                            binding.emptyLayout.visibility = View.GONE
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlistAction.collect { playlistAction ->
                    playlistAction?.let {
                        when (playlistAction) {
                            is Response.Success -> {
                                binding.progressBar.visibility = View.GONE
                                showSnackBar(binding.root, playlistAction.errorMessage.toString())
                            }

                            is Response.Error -> {
                                binding.progressBar.visibility = View.GONE
                                showSnackBar(binding.root, playlistAction.errorMessage.toString())
                            }

                            is Response.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                        }
                        viewModel.clearPlaylistAction()
                    }
                }
            }
        }
    }


    private fun setPlaylistAdapter() {
        adapter = PlaylistsAdapter(object : PlaylistClickListener {
            override fun onClick(playlist: PlaylistEntity) {
                val action =
                    FragmentPlaylistsDirections.actionFragmentPlaylistsToFragmentPlaylistSongs(
                        playlist.id, playlist.playListName
                    )
                findNavController().navigate(action)
            }

            override fun onMoreClick(playlist: PlaylistEntity) {
                selectedPlaylist = playlist
                val playlistOptionsSheet = PlaylistOptionSheet.newInstance(playlist.playListName)
                playlistOptionsSheet.setBottomSheetCallback(playlistOptionsSheetCallback)
                playlistOptionsSheet.show(parentFragmentManager, "PlaylistOptionSheet")
            }
        })
        binding.playListRecyclerView.adapter = adapter
        binding.playListRecyclerView.layoutManager = LinearLayoutManager(context)

    }

    private fun setClickListeners() {

    }

    private fun showDeleteDialog(playlist: PlaylistEntity) {
        val title = getString(R.string.delete_title)
        val description = getString(R.string.delete_confirm)
        val btnText = getString(R.string.delete_title)
        val dialog = CustomAlertDialog(requireContext())
        dialog.setTitle(title)
        dialog.setDescription(description)
        dialog.setButton(btnText) {
            viewModel.deletePlayList(playlist)
            dialog.close()
        }
        dialog.show()
    }


    override fun onAddPlaylistSheetDismissed(playlistName: String, playlistType: PlaylistType) {
        when (playlistType) {
            PlaylistType.Add -> {
                viewModel.addPlayList(PlaylistEntity(0, playlistName))
            }

            PlaylistType.Update -> {
                viewModel.updatePlayList(selectedPlaylist.copy(playListName = playlistName))
            }
        }

    }

    override fun onPlaylistOptionSheetDismissed(selectedOption: PlaylistOptions?) {
        selectedOption?.let {
            when (it) {
                PlaylistOptions.Share -> {}
                PlaylistOptions.Delete -> {
                    showDeleteDialog(selectedPlaylist)
                }

                PlaylistOptions.Edit -> {
                    val playlistSheet = AddPlaylistSheet.newInstance(
                        selectedPlaylist.playListName, PlaylistType.Update
                    )
                    playlistSheet.setBottomSheetCallback(addPlaylistSheetCallback)
                    playlistSheet.show(parentFragmentManager, "AddPlaylistSheet")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}