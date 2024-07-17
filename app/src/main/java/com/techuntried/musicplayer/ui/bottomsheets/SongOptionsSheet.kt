package com.techuntried.musicplayer.ui.bottomsheets

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.techuntried.musicplayer.R
import com.techuntried.musicplayer.data.models.OptionUi
import com.techuntried.musicplayer.databinding.SongOptionSheetBinding
import com.techuntried.musicplayer.utils.Constants
import com.techuntried.musicplayer.utils.SongOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SongOptionsSheet : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_SONG_NAME = "song_name"
        private const val ARG_PLAYLIST_ID = "playlist_id"

        fun newInstance(
            songName: String,
            playlistId: Long
        ): SongOptionsSheet {
            val args = Bundle().apply {
                putString(ARG_SONG_NAME, songName)
                putLong(ARG_PLAYLIST_ID, playlistId)
            }
            return SongOptionsSheet().apply {
                arguments = args
            }
        }
    }

    private var _binding: SongOptionSheetBinding? = null
    private val binding get() = _binding!!
    private var selectedOption: SongOptions? = null
    private var songSheetCallback: BottomSheetCallback? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = SongOptionSheetBinding.inflate(inflater, container, false)

//        song = arguments?.getParcelable(ARG_SONG)
//        binding.displayName.text = song?.songName


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUi()
        setOnClickListeners()
    }

    private fun setUi() {
        val songName = arguments?.getString(ARG_SONG_NAME)
        val playlistId = arguments?.getLong(ARG_PLAYLIST_ID)
        binding.displayName.text = songName
        if (playlistId == Constants.PLAYLIST_ID_ALL) {
            binding.shareSong.songOption = OptionUi("Share song", R.drawable.share_icon)
            binding.deleteSong.songOption = OptionUi("Delete song", R.drawable.delete_icon)
        }
    }

    private fun setOnClickListeners() {
        binding.shareSong.root.setOnClickListener {
            selectedOption = SongOptions.Share
            dismiss()
        }
        binding.deleteSong.root.setOnClickListener {
            selectedOption = SongOptions.Delete
            dismiss()
        }
    }

    fun setBottomSheetCallback(callback: BottomSheetCallback) {
        songSheetCallback = callback
    }

    interface BottomSheetCallback {
        fun onSongOptionSheetDismissed(selectedOption: SongOptions?)
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        songSheetCallback?.onSongOptionSheetDismissed(selectedOption)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}