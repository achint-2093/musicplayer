package com.techuntried.musicplayer.ui.artist

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
import com.techuntried.musicplayer.databinding.FragmentArtistBinding
import com.techuntried.musicplayer.utils.Response
import com.techuntried.musicplayer.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentArtist : Fragment() {

    private var _binding: FragmentArtistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ArtistViewmodel by viewModels()
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
            override fun onClick() {
            }

        })
        binding.artistRecyclerView.adapter = adapter
        binding.artistRecyclerView.layoutManager = LinearLayoutManager(context)

    }

    private fun observers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.artists.collect { artists ->
                    when (artists) {
                        is Response.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val data = artists.data ?: emptyList()
                            if (data.isNotEmpty()) {
                                binding.artistRecyclerView.visibility = View.VISIBLE
                                adapter.submitList(data)

                            } else {
                                binding.progressBar.visibility = View.GONE
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

    private fun setOnClickListeners() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}