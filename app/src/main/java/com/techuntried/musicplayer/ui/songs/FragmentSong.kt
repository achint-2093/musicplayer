package com.techuntried.musicplayer.ui.songs

import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.techuntried.musicplayer.R
import com.techuntried.musicplayer.data.models.SongEntity
import com.techuntried.musicplayer.databinding.FragmentSongsBinding
import com.techuntried.musicplayer.ui.bottomsheets.SongOptionsSheet
import com.techuntried.musicplayer.ui.player.PlayerViewmodel
import com.techuntried.musicplayer.utils.Constants
import com.techuntried.musicplayer.utils.PermissionManager
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
    private val playerViewModel: PlayerViewmodel by activityViewModels()

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
        setSongsAdapter()
        setObservers()
        setOnClickListener()
    }


    private fun setOnClickListener() {
        binding.moreButton.setOnClickListener {
            showMenu(it)
        }
        binding.permissionRequire.setOnClickListener {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    PermissionManager.audioPermission
                )
            ) {
                playerViewModel.updateDialogShown(true, false)
            } else {
                playerViewModel.updateDialogShown(true, true)
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshSongs()
        }
    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.songs.collect { songs ->
                    songs?.let {
                        binding.swipeRefreshLayout.isRefreshing = false
                        when (songs) {
                            is Response.Success -> {
                                binding.progressBar.visibility = View.GONE
                                val data = songs.data
                                data?.let {
                                    binding.songsRecyclerView.visibility = View.VISIBLE
                                    binding.songsText.text = "Songs ${data.size}"
                                    adapter.submitList(data)

                                } ?: kotlin.run {
                                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                playerViewModel.isPermissionGranted.collect {
                    if (it) {
                        binding.permissionRequire.visibility = View.GONE
                        viewModel.fetchMusicFiles()
                    } else {
                        binding.permissionRequire.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        requireActivity().menuInflater.inflate(R.menu.songs_menu, menu)
    }

    private fun setSongsAdapter() {
        adapter = SongsAdapter(object : SongsClickListener {

            override fun onClick(songEntity: SongEntity) {
                playerViewModel.fetchSongs(
                    songId = songEntity.id,
                    playlistId = Constants.PLAYLIST_ID_ALL,
                    filterData = ""
                )
            }

            override fun onMoreClick(songEntity: SongEntity) {
                selectedSong = songEntity
                val songsBottomSheet =
                    SongOptionsSheet.newInstance(songEntity, Constants.PLAYLIST_ID_ALL)
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

    private fun showMenu(v: View) {
        val popup = PopupMenu(context, v, 0,0, R.style.CustomPopupMenu)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.songs_menu, popup.menu)
        popup.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.refresh_songs_action -> {
                    viewModel.refreshSongs()
                    true
                }

                else -> {
                    true
                }
            }

        }
        popup.show()
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