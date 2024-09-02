package com.mathias8dev.composesimplestoragemanager.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Song(
    val mediaId: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val songUri: Uri? = null,
    val imageUrl: String? = null
) : Parcelable
