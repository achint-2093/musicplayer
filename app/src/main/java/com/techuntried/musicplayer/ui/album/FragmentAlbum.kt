package com.techuntried.musicplayer.ui.album

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
import androidx.recyclerview.widget.GridLayoutManager
import com.techuntried.musicplayer.data.models.AlbumModel
import com.techuntried.musicplayer.databinding.FragmentAlbumBinding
import com.techuntried.musicplayer.ui.artist.AlbumViewmodel
import com.techuntried.musicplayer.ui.home.FragmentHomeDirections
import com.techuntried.musicplayer.utils.FilterType
import com.techuntried.musicplayer.utils.Response
import com.techuntried.musicplayer.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentAlbum : Fragment() {

    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AlbumViewmodel by viewModels()
    private lateinit var adapter: AlbumAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlbumBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        setArtistAdapter()
        observers()
    }

    private fun setArtistAdapter() {
        adapter = AlbumAdapter(object : AlbumClickListener {
            override fun onClick(album: AlbumModel) {
                val action =
                    FragmentHomeDirections.actionFragmentHomeToFragmentFilter(
                        FilterType.Album, album.album
                    )
                findNavController().navigate(action)
            }

        })
        binding.albumsRecyclerView.adapter = adapter
        binding.albumsRecyclerView.layoutManager = GridLayoutManager(context, 2)

    }

    private fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.albums.collect { artists ->
                    when (artists) {
                        is Response.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val data = artists.data ?: emptyList()
                            if (data.isNotEmpty()) {
                                binding.albumsRecyclerView.visibility = View.VISIBLE
                                adapter.submitList(data)

                            } else {
                                binding.progressBar.visibility = View.GONE
                            }

                        }

                        is Response.Error -> {
                            binding.albumsRecyclerView.visibility = View.GONE
                            binding.progressBar.visibility = View.GONE
                            showSnackBar(binding.root, artists.errorMessage.toString())
                        }

                        is Response.Loading -> {
                            binding.albumsRecyclerView.visibility = View.GONE
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun setOnClickListeners() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}