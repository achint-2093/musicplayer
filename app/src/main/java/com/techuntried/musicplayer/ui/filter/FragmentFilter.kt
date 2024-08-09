package com.techuntried.musicplayer.ui.filter

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
import com.techuntried.musicplayer.data.models.SongEntity
import com.techuntried.musicplayer.databinding.FragmentFilterBinding
import com.techuntried.musicplayer.ui.player.PlayerViewmodel
import com.techuntried.musicplayer.ui.songs.SongsAdapter
import com.techuntried.musicplayer.ui.songs.SongsClickListener
import com.techuntried.musicplayer.utils.Constants
import com.techuntried.musicplayer.utils.FilterType
import com.techuntried.musicplayer.utils.Response
import com.techuntried.musicplayer.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentFilter : Fragment() {

    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FilterViewModel by viewModels()
    private lateinit var adapter: SongsAdapter
    private val playerViewModel: PlayerViewmodel by activityViewModels()
    private val args by navArgs<FragmentFilterArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUi()
        setFilterAdapter()
        setObservers()
        setOnClickListeners()
    }

    private fun setUi() {
        binding.toolbar.title = args.filterData
    }

    private fun setFilterAdapter() {
        adapter = SongsAdapter(object : SongsClickListener {
            override fun onClick(songEntity: SongEntity) {

                val filter = args.filter
                val playlistId =
                    if (filter == FilterType.Album) Constants.PLAYLIST_ID_ALBUM else Constants.PLAYLIST_ID_ARTIST
                playerViewModel.fetchSongs(
                    songId = songEntity.id,
                    playlistId = playlistId,
                    filterData = args.filterData
                )
//                val action = FragmentFilterDirections.actionFragmentFilterToFragmentPlayer(
//                    songId = songEntity.id,
//                    playlistId = playlistId,
//                    filterData = args.filterData
//                )
//                findNavController().navigate(action)
            }

            override fun onMoreClick(songEntity: SongEntity) {
            }

        })
        binding.filterRecyclerView.adapter = adapter
        binding.filterRecyclerView.layoutManager = LinearLayoutManager(context)

    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredSongs.collect { filteredSongs ->
                    when (filteredSongs) {
                        is Response.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val data = filteredSongs.data

                            data?.let {
                                binding.filterRecyclerView.visibility = View.VISIBLE
                                adapter.submitList(data)

                            } ?: run {

                            }
                        }

                        is Response.Error -> {
                            binding.filterRecyclerView.visibility = View.GONE
                            binding.progressBar.visibility = View.GONE
                            showSnackBar(binding.root, filteredSongs.errorMessage.toString())
                        }

                        is Response.Loading -> {
                            binding.filterRecyclerView.visibility = View.GONE
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}