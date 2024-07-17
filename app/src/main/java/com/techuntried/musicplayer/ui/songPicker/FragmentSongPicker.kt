package com.techuntried.musicplayer.ui.songPicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.techuntried.musicplayer.data.models.SongPickerModel
import com.techuntried.musicplayer.databinding.FragmentSongPickerBinding
import com.techuntried.musicplayer.utils.Response
import com.techuntried.musicplayer.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentSongPicker : Fragment() {

    private var _binding: FragmentSongPickerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SongPickerViewModel by viewModels()
    private lateinit var adapter: SongPickerAdapter
    private var mPosition: Int? = null
    private var addButtonImage: ImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSongPickerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSongsAdapter()
        setClickListeners()
        observers()
    }

    private fun observers() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.songList.collect { songList ->
                    when (songList) {
                        is Response.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val data = songList.data ?: emptyList()
                            if (data.isNotEmpty()) {
                                binding.songPickerRecyclerview.visibility = View.VISIBLE
                                adapter.submitList(data)

                            } else {
                                binding.progressBar.visibility = View.GONE
                            }

                        }

                        is Response.Error -> {
                            binding.songPickerRecyclerview.visibility = View.GONE
                            binding.progressBar.visibility = View.GONE
                            showSnackBar(binding.root, songList.errorMessage.toString())
                        }

                        is Response.Loading -> {
                            binding.songPickerRecyclerview.visibility = View.GONE
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun setClickListeners() {

    }

    private fun setSongsAdapter() {
        adapter = SongPickerAdapter(object : SongPickerClickListener {
            override fun onClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onAddClick(song: SongPickerModel, position: Int, addButton: ImageView) {
                Toast.makeText(context, "${song.songId}", Toast.LENGTH_SHORT).show()
                mPosition = position
                if (song.isInPlaylist) {
                    viewModel.removeSongFromPlaylist(songId = song.songId)
                } else {
                    viewModel.addSongToPlaylist(songId = song.songId)
                }
                adapter.toggleSongSelection(position)
//                mPosition=position
//                val find=songs?.find { it.songId==song.songId }
//                if (find!=null) {
//                    if (find.isInPlaylist) {
//                        viewModel.removeSongFromPlaylist(
//                            SongEntity(
//                                song.songId,
//                                playlist?.playlistId!!,
//                                song.name,
//                                song.artist,
//                                song.uri
//                            )
//                        )
//                    } else {
//                        viewModel.addSongToPlaylist(
//                            SongEntity(
//                                song.songId,
//                                playlist?.playlistId!!,
//                                song.name,
//                                song.artist,
//                                song.uri
//                            )
//                        )
//                    }
//                }
//                mSong = song
//                addButtonImage = addButton
//                val find = updatedSongs?.find { it.songName == song.name }
//                if (find != null) {
//                    viewModel.removeSongFromPlaylist(find)
//                } else {
//                    viewModel.addSongToPlaylist(
//                        SongEntity(
//                            0,
//                            playlist?.playlistId!!,
//                            song.name,
//                            song.artist,
//                            song.uri
//                        )
//                    )
//                }
            }
        })

        binding.songPickerRecyclerview.adapter = adapter
        binding.songPickerRecyclerview.layoutManager = LinearLayoutManager(context)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}