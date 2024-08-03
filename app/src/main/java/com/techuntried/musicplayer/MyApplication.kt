package com.techuntried.musicplayer


import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.media3.session.MediaController
import com.google.common.util.concurrent.ListenableFuture
import com.techuntried.musicplayer.utils.MediaControllerObject
import com.techuntried.musicplayer.utils.PlayerService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var mediaControllerFuture: ListenableFuture<MediaController>

    override fun onCreate() {
        super.onCreate()
       // MediaControllerObject.initialize(mediaControllerFuture)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                PlayerService.CHANNEL_ID,
                "media playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "channel for media playback"

            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)

        }
    }
}