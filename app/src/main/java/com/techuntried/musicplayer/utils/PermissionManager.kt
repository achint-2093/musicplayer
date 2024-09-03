package com.techuntried.musicplayer.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat


object PermissionManager {
    val audioPermission = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    } else {
        android.Manifest.permission.READ_MEDIA_AUDIO
    }

    fun hasAudioPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            audioPermission
        ) == PackageManager.PERMISSION_GRANTED
    }
}

