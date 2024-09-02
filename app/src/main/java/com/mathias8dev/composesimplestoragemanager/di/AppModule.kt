package com.mathias8dev.composesimplestoragemanager.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single


@Module
class AppModule {


    @Single
    fun provideExoPlayer(context: Context): ExoPlayer {
        return ExoPlayer.Builder(context)
            .build()
    }
}