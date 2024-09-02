package com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.composables

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.mathias8dev.composesimplestoragemanager.LocalSnackbarHostState
import com.mathias8dev.composesimplestoragemanager.ui.composables.MediaGroup
import com.mathias8dev.composesimplestoragemanager.ui.composables.MediaGroupComposable


@Composable
fun CssHomeComposable(
    modifier: Modifier = Modifier,
    onNavigateToMediaGroup: (MediaGroup) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val localSnackbarHostState = LocalSnackbarHostState.current
    LazyColumn(
        modifier = modifier
    ) {
        item {
            MediaGroupComposable(
                title = "Internal Storage",
                onClick = {
                    onNavigateToMediaGroup(MediaGroup.InternalStorage)
                }
            )
            MediaGroupComposable(
                title = "App",
                onClick = {
                    onNavigateToMediaGroup(MediaGroup.App)
                }
            )

            MediaGroupComposable(
                title = "Audio",
                onClick = {
                    onNavigateToMediaGroup(MediaGroup.Audio)
                }
            )

            MediaGroupComposable(
                title = "Document",
                onClick = {
                    onNavigateToMediaGroup(MediaGroup.Document)
                }
            )

            MediaGroupComposable(
                title = "Image",
                onClick = {
                    onNavigateToMediaGroup(MediaGroup.Image)
                }
            )

            MediaGroupComposable(
                title = "Video",
                onClick = {
                    onNavigateToMediaGroup(MediaGroup.Video)
                }
            )
        }
    }

}





