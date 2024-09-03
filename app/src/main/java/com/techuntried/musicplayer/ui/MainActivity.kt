package com.techuntried.musicplayer.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.techuntried.musicplayer.R
import com.techuntried.musicplayer.databinding.ActivityMainBinding
import com.techuntried.musicplayer.ui.bottomsheets.PermissionBottomSheet
import com.techuntried.musicplayer.ui.player.PlayerViewmodel
import com.techuntried.musicplayer.utils.PermissionManager
import com.techuntried.musicplayer.utils.Response
import com.techuntried.musicplayer.utils.setSongCover
import com.techuntried.musicplayer.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PermissionBottomSheet.BottomSheetCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: PlayerViewmodel by viewModels()
    private lateinit var permissionSheetCallback: PermissionBottomSheet.BottomSheetCallback

    private val audioPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.updatePermissionStatus(true)
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    PermissionManager.audioPermission
                )
            ) {
                viewModel.updateDialogShown(isDialogShown = true, isSettingStyle = false)
            } else {
                viewModel.updateDialogShown(isDialogShown = true, isSettingStyle = true)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionSheetCallback = this
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
        if (PermissionManager.hasAudioPermission(this)) {
            viewModel.updatePermissionStatus(true)
        }else{
            audioPermissionLauncher.launch(PermissionManager.audioPermission)
        }
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
                    if (viewModel.currentSong.value.data != null)
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
                                    setSongCover(binding.musicImageView, it.albumId)
                                } else {
                                    binding.miniPlayback.visibility = View.GONE
                                }
                                binding.musicName.text = data.songName
                                binding.artistName.text = data.artist
                            } ?: run {
                                binding.miniPlayback.visibility = View.GONE
                            }

                        }

                        is Response.Error -> {
                            binding.miniPlayback.visibility = View.GONE
                            showSnackBar(binding.root, song.errorMessage.toString())
                        }

                        is Response.Loading -> {
                            binding.miniPlayback.visibility = View.GONE
                            //binding.playerLayout.visibility = View.GONE
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isDialogShown.collect {
                    if (it.first) {
                        showPermissionSheet(it.second)
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

    private fun showPermissionSheet(isSettingStyle: Boolean = false) {
        val permissionSheet = PermissionBottomSheet.newInstance(isSettingStyle)
        permissionSheet.setBottomSheetCallback(permissionSheetCallback)
        permissionSheet.show(supportFragmentManager, "permissionSheet")
    }

    private fun setOnClickListeners() {
        binding.playPauseButton.setOnClickListener {
            viewModel.handlePlayPauseButton()
        }
        binding.miniPlayback.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_bottom)
                .setExitAnim(R.anim.slide_out_top)
                .setPopEnterAnim(R.anim.slide_in_top)
                .setPopExitAnim(R.anim.slide_out_bottom)
                .build()

            navController.navigate(R.id.fragmentPlayer, null, navOptions)
        }
    }

    override fun onPermissionSheetDismissed(isPermissionGranted: Boolean, isSettingStyle: Boolean) {
        if (!isSettingStyle && isPermissionGranted) {
            audioPermissionLauncher.launch(PermissionManager.audioPermission)
        } else if (isSettingStyle && isPermissionGranted) {
            val settingIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            settingIntent.data = uri
            startActivity(settingIntent)
        }
        viewModel.updateDialogShown(false,false)
    }
}