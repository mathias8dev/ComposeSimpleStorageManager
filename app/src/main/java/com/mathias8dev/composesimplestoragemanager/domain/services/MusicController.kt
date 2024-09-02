package com.mathias8dev.composesimplestoragemanager.domain.services

import com.mathias8dev.composesimplestoragemanager.models.Song
import com.mathias8dev.composesimplestoragemanager.others.PlayerState


interface MusicController {
    var mediaControllerCallback: (
        (
        playerState: PlayerState,
        currentMusic: Song?,
        currentPosition: Long,
        totalDuration: Long,
        isShuffleEnabled: Boolean,
        isRepeatOneEnabled: Boolean
    ) -> Unit
    )?

    fun addMediaItems(songs: List<Song>)

    fun play(mediaItemIndex: Int)

    fun resume()

    fun pause()

    fun getCurrentPosition(): Long

    fun destroy()

    fun skipToNextSong()

    fun skipToPreviousSong()

    fun getCurrentSong(): Song?

    fun seekTo(position: Long)
}