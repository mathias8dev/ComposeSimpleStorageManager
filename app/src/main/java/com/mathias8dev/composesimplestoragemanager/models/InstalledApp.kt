package com.mathias8dev.composesimplestoragemanager.models

import android.graphics.drawable.Drawable

data class InstalledApp(
    val name: String,
    val packageName: String,
    val sourceDir: String,
    val appIcon: Drawable? = null,
    val isSystemApp: Boolean = false,
)