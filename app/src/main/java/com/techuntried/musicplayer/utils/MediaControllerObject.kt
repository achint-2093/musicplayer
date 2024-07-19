package com.techuntried.musicplayer.utils

import android.util.Log
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executors
import javax.inject.Singleton


object MediaControllerObject {
    private var mediaController: MediaController? = null

    fun initialize(mediaControllerFuture: ListenableFuture<MediaController>) {
        mediaControllerFuture.addListener({
            try {
                mediaController = mediaControllerFuture.get()
            } catch (e: Exception) {
                Log.d("MYDEBUG", "initialize error media controller")
            }
        }, Executors.newSingleThreadExecutor())
    }

    fun getMediaController(): MediaController? {
        return mediaController
    }
}