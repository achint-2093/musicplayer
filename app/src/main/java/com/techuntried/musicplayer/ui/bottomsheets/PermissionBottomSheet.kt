package com.techuntried.musicplayer.ui.bottomsheets

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.techuntried.musicplayer.databinding.PermissionBottomSheetBinding

class PermissionBottomSheet : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_SETTING_STYLE = "setting_style"
        fun newInstance(
            settingStyle: Boolean
        ): PermissionBottomSheet {
            val args = Bundle().apply {
                putBoolean(ARG_SETTING_STYLE, settingStyle)
            }
            return PermissionBottomSheet().apply {
                arguments = args
            }
        }
    }

    private var _binding: PermissionBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomSheetCallback: BottomSheetCallback
    private var settingStyle: Boolean = false
    private var isPermissionGranted = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = PermissionBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
        setClickListeners()
    }

    private fun setUI() {
        settingStyle = arguments?.getBoolean(ARG_SETTING_STYLE) ?: false

        if (settingStyle) {
            binding.grantPermissionButton.text = "Settings"
        } else {
            binding.grantPermissionButton.text = "Grant"
        }
    }

    private fun setClickListeners() {
        binding.grantPermissionButton.setOnClickListener {
            isPermissionGranted = true
            dismiss()
        }
        binding.laterButton.setOnClickListener {
            isPermissionGranted = false
            dismiss()
        }

    }

    interface BottomSheetCallback {
        fun onPermissionSheetDismissed(isPermissionGranted: Boolean, isSettingStyle: Boolean)
    }

    fun setBottomSheetCallback(callback: BottomSheetCallback) {
        bottomSheetCallback = callback
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        bottomSheetCallback.onPermissionSheetDismissed(isPermissionGranted, settingStyle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}