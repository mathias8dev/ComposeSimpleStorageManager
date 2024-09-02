package com.mathias8dev.composesimplestoragemanager.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class MediaInfo(
    open val id: Long,
    open val contentUri: Uri? = null,
    open val externalContentUri: Uri? = null,
    open val name: String,
    open val size: Long,
    open val bucketName: String? = null,
) : Parcelable
