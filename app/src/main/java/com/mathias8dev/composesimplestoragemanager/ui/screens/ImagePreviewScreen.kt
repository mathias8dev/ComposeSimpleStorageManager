package com.mathias8dev.composesimplestoragemanager.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mathias8dev.composesimplestoragemanager.ui.composables.ImageViewerComposable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination
@RootNavGraph
fun ImagePreviewScreen(
    filePath: String,
    navigator: DestinationsNavigator
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Preview")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigator.popBackStack()
                        },
                        content = {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    )
                }
            )
        }
    ) { padding ->
        ImageViewerComposable(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            model = filePath,
        )
    }
}

