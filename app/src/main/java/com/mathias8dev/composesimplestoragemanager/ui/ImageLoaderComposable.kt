package com.mathias8dev.composesimplestoragemanager.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageScope
import coil.request.ImageRequest

@Composable
fun ImageLoaderComposable(
    model: Any?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    onError: @Composable (SubcomposeAsyncImageScope.(AsyncImagePainter.State.Error) -> Unit)? = null
) {

    Box(modifier = modifier) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(model)
                .crossfade(true)
                .build(),
            loading = {
                ShimmerAnimation(modifier = modifier)
            },
            error = {
                onError?.invoke(this, it).otherwise {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Text(text = "Une erreur est survenue.")
                    }
                }
            },
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            alignment = Alignment.Center,
            contentScale = contentScale,
        )
    }

}