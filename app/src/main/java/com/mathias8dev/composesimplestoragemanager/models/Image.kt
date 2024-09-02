package com.mathias8dev.composesimplestoragemanager.models

import android.net.Uri
import kotlinx.parcelize.Parcelize


@Parcelize
data class Image(
    override val id: Long,
    override val contentUri: Uri,
    override val externalContentUri: Uri,
    override val name: String,
    override val size: Long,
    override val bucketName: String? = null
) : MediaInfo(id, contentUri, externalContentUri, name, size, bucketName)
