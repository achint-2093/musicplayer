//package com.techuntried.musicplayer.ui.bottomsheets
//
//
//import android.content.DialogInterface
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.AdapterView
//import android.widget.Toast
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProvider
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment
//import com.slisafe.musicplayer.R
//import com.slisafe.musicplayer.adapters.BottomSheetAdapter
//import com.slisafe.musicplayer.databinding.FragmentSongOptionsSheetBinding
//import com.slisafe.musicplayer.models.BottomSheetModel
//import com.slisafe.musicplayer.models.PlaylistsEntity
//import com.slisafe.musicplayer.models.SongDataClass
//import com.slisafe.musicplayer.models.SongEntity
//import com.slisafe.musicplayer.repository.Response
//import com.slisafe.musicplayer.utils.CustomAlertDialog
//import com.slisafe.musicplayer.viewModels.PlaylistSongViewModel
//import com.techuntried.musicplayer.R
//import com.techuntried.musicplayer.data.models.SongModel
//import com.techuntried.musicplayer.databinding.FragmentSongOptionSheetBinding
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class SongOptionsSheet : BottomSheetDialogFragment() {
//
//    companion object {
//        private const val ARG_SONG = "song_name"
//        private const val ARG_PLAYLIST = "playlist"
//
//        fun newInstance(
//            song: SongModel
//        ): SongOptionsSheet {
//            val args = Bundle().apply {
//                putParcelable(ARG_SONG, song)
//            }
//            return SongOptionsSheet().apply {
//                arguments = args
//            }
//        }
//    }
//
//    private var _binding: FragmentSongOptionSheetBinding? = null
//    private val binding get() = _binding!!
//    private var song: SongEntity? = null
//    private var playlist: PlaylistsEntity? = null
//    private var options: List<BottomSheetModel> = emptyList()
//    private var selectedOption: Int? = null
//
//    private var songSheetCallback: BottomSheetCallback? = null
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        _binding = FragmentSongOptionSheetBinding.inflate(inflater, container, false)
//
//        song = arguments?.getParcelable(ARG_SONG)
//        playlist = arguments?.getParcelable(ARG_PLAYLIST)
//        binding.bottomSheetTitle.text = song?.songName
//
//
//
//
//
//        return binding.root
//    }
//
//
//    fun setBottomSheetCallback(callback: BottomSheetCallback) {
//        songSheetCallback = callback
//    }
//
//    interface BottomSheetCallback {
//        fun onDataDismissed(selectedOption: Int,song: SongEntity)
//    }
//
//
//    override fun onDismiss(dialog: DialogInterface) {
//        super.onDismiss(dialog)
//        if (selectedOption != null) {
//            songSheetCallback?.onDataDismissed(selectedOption!!,song!!)
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//}