package com.mathias8dev.composesimplestoragemanager.data.service

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.mathias8dev.composesimplestoragemanager.data.mapper.toSong
import com.mathias8dev.composesimplestoragemanager.domain.services.MusicController
import com.mathias8dev.composesimplestoragemanager.models.Song
import com.mathias8dev.composesimplestoragemanager.others.PlayerState
import org.koin.core.annotation.Single
import timber.log.Timber


@Single
class MusicControllerImpl(context: Context) : MusicController {

    private var mediaControllerFuture: ListenableFuture<MediaController>
    private val mediaController: MediaController?
        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get() else null

    override var mediaControllerCallback: (
        (
        playerState: PlayerState,
        currentMusic: Song?,
        currentPosition: Long,
        totalDuration: Long,
        isShuffleEnabled: Boolean,
        isRepeatOneEnabled: Boolean
    ) -> Unit
    )? = null


    init {
        val sessionToken =
            SessionToken(context, ComponentName(context, MusicService::class.java))
        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture.addListener({ controllerListener() }, MoreExecutors.directExecutor())
        Timber.d("MusicControllerImpl init")
    }


    private fun controllerListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)

                Timber.d("OnEvents $events")

                with(player) {
                    mediaControllerCallback?.invoke(
                        playbackState.toPlayerState(isPlaying),
                        currentMediaItem?.toSong(),
                        currentPosition.coerceAtLeast(0L),
                        duration.coerceAtLeast(0L),
                        shuffleModeEnabled,
                        repeatMode == Player.REPEAT_MODE_ONE
                    )
                }
            }
        })
    }

    private fun Int.toPlayerState(isPlaying: Boolean) =
        when (this) {
            Player.STATE_IDLE -> PlayerState.STOPPED
            Player.STATE_ENDED -> PlayerState.STOPPED
            else -> if (isPlaying) PlayerState.PLAYING else PlayerState.PAUSED
        }

    override fun addMediaItems(songs: List<Song>) {
        mediaControllerFuture.addListener({
            mediaController?.let { controller ->
                val mediaItems = songs.filter { it.songUri != null }.map {
                    MediaItem.Builder()
                        .setMediaId(it.mediaId ?: it.songUri.toString())
                        .setUri(it.songUri!!.toString())
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(it.title)
                                .setSubtitle(it.subtitle)
                                .setArtist(it.subtitle)
                                .setArtworkUri(Uri.parse(it.imageUrl))
                                .build()
                        )
                        .build()
                }

                controller.setMediaItems(mediaItems)
            }
        }, MoreExecutors.directExecutor())
    }

    override fun play(mediaItemIndex: Int) {
        Timber.d("OnPlay $mediaItemIndex")
        mediaController?.apply {
            Timber.d("OnConfigure player")
            seekToDefaultPosition(mediaItemIndex)
            playWhenReady = true
            prepare()
        }
    }

    override fun resume() {
        mediaController?.play()
    }

    override fun pause() {
        mediaController?.pause()
    }

    override fun getCurrentPosition(): Long = mediaController?.currentPosition ?: 0L

    override fun getCurrentSong(): Song? = mediaController?.currentMediaItem?.toSong()

    override fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    override fun destroy() {
        MediaController.releaseFuture(mediaControllerFuture)
        mediaControllerCallback = null
    }

    override fun skipToNextSong() {
        mediaController?.seekToNext()
    }

    override fun skipToPreviousSong() {
        mediaController?.seekToPrevious()
    }

}