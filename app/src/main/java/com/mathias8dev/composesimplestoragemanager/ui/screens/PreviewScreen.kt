package com.mathias8dev.composesimplestoragemanager.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mathias8dev.composesimplestoragemanager.ui.ImageLoaderComposable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph


@Composable
@Destination
@RootNavGraph
fun PreviewScreen(
    filePath: String
) {
    ImageLoaderComposable(
        modifier = Modifier.fillMaxSize(),
        model = filePath,
    )
}