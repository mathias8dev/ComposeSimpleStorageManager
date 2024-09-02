package com.mathias8dev.composesimplestoragemanager.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable


@Composable
fun stdNotConsumedPaddingValues() = PaddingValues(
    top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding(),
    bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
)