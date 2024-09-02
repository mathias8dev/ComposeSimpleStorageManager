package com.mathias8dev.composesimplestoragemanager.data.mapper

import androidx.media3.common.MediaItem
import com.mathias8dev.composesimplestoragemanager.models.Song


fun MediaItem.toSong() =
    Song(
        mediaId = mediaId,
        title = mediaMetadata.title.toString(),
        subtitle = mediaMetadata.subtitle.toString(),
        songUri = this.localConfiguration?.uri,
        imageUrl = mediaMetadata.artworkUri.toString()
    )


