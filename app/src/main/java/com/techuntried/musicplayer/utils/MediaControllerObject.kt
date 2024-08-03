package com.techuntried.musicplayer.utils

import android.util.Log
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executors
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


object MediaControllerObject {
    private var mediaController: MediaController? = null
    private var mediaControllerFuture: ListenableFuture<MediaController>? = null

    fun initialize(mediaControllerFuture: ListenableFuture<MediaController>) {
        this.mediaControllerFuture = mediaControllerFuture
        mediaControllerFuture.addListener({
            try {
                mediaController = mediaControllerFuture.get()
                Log.d("MYDEBUG", "initialized media controller")
            } catch (e: Exception) {
                Log.d("MYDEBUG", "initialize error media controller")
            }
        }, Executors.newSingleThreadExecutor())
    }

    suspend fun getMediaController(): MediaController? {
        return if (mediaController != null) {
            mediaController
        } else {
            mediaControllerFuture?.await()
        }
    }
}

// Extension function to convert ListenableFuture to a Kotlin Deferred
suspend fun <T> ListenableFuture<T>.await(): T = suspendCancellableCoroutine { cont ->
    addListener(Runnable {
        try {
            cont.resume(get())
        } catch (e: Exception) {
            cont.resumeWithException(e)
        }
    }, MoreExecutors.directExecutor())
}
