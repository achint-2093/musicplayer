package com.techuntried.musicplayer.ui.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.techuntried.musicplayer.data.models.ArtistModel
import com.techuntried.musicplayer.databinding.FragmentArtistBinding
import com.techuntried.musicplayer.ui.home.FragmentHomeDirections
import com.techuntried.musicplayer.ui.player.PlayerViewmodel
import com.techuntried.musicplayer.utils.FilterType
import com.techuntried.musicplayer.utils.PermissionManager
import com.techuntried.musicplayer.utils.Response
import com.techuntried.musicplayer.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentArtist : Fragment() {

    private var _binding: FragmentArtistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArtistViewmodel by viewModels()
    private val playerViewModel: PlayerViewmodel by activityViewModels()
    private lateinit var adapter: ArtistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArtistBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        setArtistAdapter()
        observers()
    }

    private fun setArtistAdapter() {
        adapter = ArtistAdapter(object : ArtistClickListener {
            override fun onClick(artist: ArtistModel) {
                val action =
                    FragmentHomeDirections.actionFragmentHomeToFragmentFilter(
                        FilterType.Artist, artist.artistName
                    )
                findNavController().navigate(action)
            }

        })
        binding.artistRecyclerView.adapter = adapter
        binding.artistRecyclerView.layoutManager = LinearLayoutManager(context)

    }

    private fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.artists.collect { artists ->
                    artists?.let {


                        when (artists) {
                            is Response.Success -> {
                                binding.progressBar.visibility = View.GONE
                                val data = artists.data
                                data?.let {
                                    binding.artistRecyclerView.visibility = View.VISIBLE
                                    binding.artistText.text = "${it.size} Artists"
                                    adapter.submitList(data)

                                } ?: run {
                                }

                            }

                            is Response.Error -> {
                                binding.artistRecyclerView.visibility = View.GONE
                                binding.progressBar.visibility = View.GONE
                                showSnackBar(binding.root, artists.errorMessage.toString())
                            }

                            is Response.Loading -> {
                                binding.artistRecyclerView.visibility = View.GONE
                                binding.progressBar.visibility = View.VISIBLE
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
                        viewModel.fetchArtists()
                    } else {
                        binding.permissionRequire.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setOnClickListeners() {
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}