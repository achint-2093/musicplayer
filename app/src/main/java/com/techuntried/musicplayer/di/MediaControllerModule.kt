package com.techuntried.musicplayer.di

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.techuntried.musicplayer.utils.PlayerService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class MediaControllerModule {


    @Singleton
    @Provides
    fun providesMediaControllerFuture(@ApplicationContext context: Context): ListenableFuture<MediaController> {
        val sessionToken = SessionToken(context, ComponentName(context, PlayerService::class.java))
        return MediaController.Builder(context, sessionToken).buildAsync()
    }


}