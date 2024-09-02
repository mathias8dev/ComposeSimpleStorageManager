package com.mathias8dev.composesimplestoragemanager.models

import android.net.Uri
import kotlinx.parcelize.Parcelize


// Container for information about each video.
@Parcelize
data class Video(
    override val id: Long,
    override val contentUri: Uri,
    override val externalContentUri: Uri? = null,
    override val name: String,
    override val size: Long,
    val duration: Long,
    override val bucketName: String? = null
) : MediaInfo(id, contentUri, externalContentUri, name, size, bucketName)
