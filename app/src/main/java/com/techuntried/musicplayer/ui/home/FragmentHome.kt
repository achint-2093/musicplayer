package com.techuntried.musicplayer.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.techuntried.musicplayer.R
import com.techuntried.musicplayer.databinding.FragmentHomeBinding


class FragmentHome : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.songs.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHome_to_fragmentSong)
        }

        binding.playlists.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHome_to_fragmentPlaylists)
        }


        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}