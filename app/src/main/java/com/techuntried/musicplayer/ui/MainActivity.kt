package com.techuntried.musicplayer.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.techuntried.musicplayer.R
import com.techuntried.musicplayer.databinding.ActivityMainBinding
import com.techuntried.musicplayer.ui.player.PlayerViewmodel
import com.techuntried.musicplayer.utils.Response
import com.techuntried.musicplayer.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: PlayerViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_main
        )
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setUi()
        observers()
        setOnClickListeners()

    }

    private fun setUi() {
        binding.musicName.isSelected = true
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.fragmentPlayer -> {
                    binding.miniPlayback.visibility = View.GONE
                }

                else -> {
                    binding.miniPlayback.visibility = View.VISIBLE
                }
            }

        }
    }

    private fun observers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentSong.collect { song ->
                    when (song) {
                        is Response.Success -> {
                            val data = song.data
                            data?.let {
                                val currentDestination = navController.currentDestination
                                if (currentDestination?.id != R.id.fragmentPlayer) {
                                    binding.miniPlayback.visibility = View.VISIBLE
                                } else {
                                    binding.miniPlayback.visibility = View.GONE
                                }
                                binding.musicName.text = data.songName
                                binding.artistName.text = data.artist
                            } ?: run {
                                showSnackBar(binding.root, "Error loading content")
                            }

                        }

                        is Response.Error -> {
                            showSnackBar(binding.root, song.errorMessage.toString())
                        }

                        is Response.Loading -> {
                            //binding.playerLayout.visibility = View.GONE
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.shouldShowPlayButton.collect {
                    if (it) {
                        binding.playPauseButton.setImageResource(R.drawable.play_icon)
                    } else {
                        binding.playPauseButton.setImageResource(R.drawable.pause_icon)
                    }
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.playPauseButton.setOnClickListener {
            viewModel.handlePlayPauseButton()
        }
    }
}