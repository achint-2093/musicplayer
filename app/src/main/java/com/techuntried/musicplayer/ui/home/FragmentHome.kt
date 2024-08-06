package com.techuntried.musicplayer.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.techuntried.musicplayer.databinding.FragmentHomeBinding


class FragmentHome : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var tabAdapter: TabAdapter

    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
//            if (isGranted) {
//                viewModel.fetchImages()
//            } else if (requireActivity().requestRationale(readImagePermissionAbove10)) {
//                showPermissionSheet(false)
//            } else {
//                showPermissionSheet(true)
//            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_AUDIO)

        setTabAdapter()
    }

    private fun setTabAdapter() {
        tabAdapter = TabAdapter(childFragmentManager,lifecycle)
        binding.tabViewpager.adapter = tabAdapter

        TabLayoutMediator(binding.tabLayout, binding.tabViewpager) { tab, position ->
            tab.text = tabAdapter.getPageTitle(position)
        }.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}