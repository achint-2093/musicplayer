package com.techuntried.musicplayer.ui.bottomsheets

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.techuntried.musicplayer.databinding.FragmentAddPlaylistSheetBinding
import com.techuntried.musicplayer.utils.PlaylistType
import com.techuntried.musicplayer.utils.title


class AddPlaylistSheet : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_PLAYLIST_NAME = "playlist_name"
        private const val ARG_PLAYLIST_TYPE = "playlist_type"

        fun newInstance(
            playlistName: String?,
            playlistType: PlaylistType,
        ): AddPlaylistSheet {
            val args = Bundle().apply {
                putString(ARG_PLAYLIST_NAME, playlistName)
                putSerializable(ARG_PLAYLIST_TYPE, playlistType)

            }
            return AddPlaylistSheet().apply {
                arguments = args
            }
        }
    }

    private var _binding: FragmentAddPlaylistSheetBinding? = null
    private val binding get() = _binding!!
    private var bottomSheetCallback: BottomSheetCallback? = null
    private var playlistName: String? = null
    private lateinit var playlistType: PlaylistType
    private lateinit var updatedName: String
    private var isContinueClicked = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddPlaylistSheetBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistName = arguments?.getString(ARG_PLAYLIST_NAME)
        playlistType = arguments?.getSerializable(ARG_PLAYLIST_TYPE) as PlaylistType

        binding.playlistText.text = playlistType.title()
        if (playlistName!=null){
            binding.playlistNameInput.setText(playlistName)
        }
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.ContinueButton.setOnClickListener {
            updatedName = binding.playlistNameInput.text.toString()
            isContinueClicked = true
            dismiss()
        }
    }

    interface BottomSheetCallback {
        fun onAddPlaylistSheetDismissed(playlistName: String, playlistType: PlaylistType)
    }


    fun setBottomSheetCallback(callback: BottomSheetCallback) {
        bottomSheetCallback = callback
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (isContinueClicked) {
            bottomSheetCallback?.onAddPlaylistSheetDismissed(updatedName,playlistType)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}