package com.mathias8dev.composesimplestoragemanager.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mathias8dev.composesimplestoragemanager.models.MediaInfo
import com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.composables.FileRowComposable
import java.io.File


@Composable
fun MediaInfoComposable(
    modifier: Modifier,
    mediaInfo: MediaInfo,
    query: String = "",
    onClick: (MediaInfo) -> Unit
) {
    val derivedFile by remember(mediaInfo) {
        derivedStateOf {
            File(mediaInfo.externalContentUri.toString())
        }
    }

    FileRowComposable(
        modifier = modifier,
        file = derivedFile,
        query = query,
        onClick = {
            onClick(mediaInfo)
        }
    )
}