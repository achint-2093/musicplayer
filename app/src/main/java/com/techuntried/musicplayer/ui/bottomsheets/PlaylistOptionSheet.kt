package com.techuntried.musicplayer.ui.bottomsheets

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.techuntried.musicplayer.R
import com.techuntried.musicplayer.data.models.OptionUi
import com.techuntried.musicplayer.databinding.PlaylistOptionSheetBinding
import com.techuntried.musicplayer.utils.PlaylistOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistOptionSheet : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_PLAYLIST_NAME = "playlist_name"
        private const val ARG_PLAYLIST_SONGS = "playlist_songs"

        fun newInstance(
            playlistName: String,
            songsCount: Int
        ): PlaylistOptionSheet {
            val args = Bundle().apply {
                putString(ARG_PLAYLIST_NAME, playlistName)
                putInt(ARG_PLAYLIST_SONGS, songsCount)
            }
            return PlaylistOptionSheet().apply {
                arguments = args
            }
        }
    }

    private var _binding: PlaylistOptionSheetBinding? = null
    private val binding get() = _binding!!
    private var selectedOption: PlaylistOptions? = null
    private var playlistOptionSheetCallback: BottomSheetCallback? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = PlaylistOptionSheetBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUi()
        setOnClickListeners()
    }

    private fun setUi() {
        val playlistName = arguments?.getString(ARG_PLAYLIST_NAME)
        val songsCount = arguments?.getInt(ARG_PLAYLIST_SONGS)
        binding.playlistName.text = playlistName
        binding.songsCount.text = "songs $songsCount"
        binding.shareSong.songOption = OptionUi("Share song", R.drawable.share_icon)
        binding.deletePlaylist.songOption = OptionUi("Delete Playlist", R.drawable.delete_icon)
        binding.editPlaylist.songOption = OptionUi("Edit Playlist", R.drawable.edit_icon)

    }

    private fun setOnClickListeners() {
        binding.shareSong.root.setOnClickListener {
            selectedOption = PlaylistOptions.Share
            dismiss()
        }
        binding.deletePlaylist.root.setOnClickListener {
            selectedOption = PlaylistOptions.Delete
            dismiss()
        }
        binding.editPlaylist.root.setOnClickListener {
            selectedOption = PlaylistOptions.Edit
            dismiss()
        }
    }

    fun setBottomSheetCallback(callback: BottomSheetCallback) {
        playlistOptionSheetCallback = callback
    }

    interface BottomSheetCallback {
        fun onPlaylistOptionSheetDismissed(selectedOption: PlaylistOptions?)
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        playlistOptionSheetCallback?.onPlaylistOptionSheetDismissed(selectedOption)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}