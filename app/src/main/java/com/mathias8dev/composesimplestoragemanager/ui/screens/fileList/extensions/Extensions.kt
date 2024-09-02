package com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.extensions

import de.datlag.mimemagic.MimeData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


val File.mimeData: MimeData?
    get() {
        return kotlin.runCatching { MimeData.fromFile(this) }.getOrNull()
    }


suspend fun File.asContentSize(): Long {
    val file = this
    return withContext(Dispatchers.IO) {
        if (file.isFile) {
            file.length()
        } else {
            var totalSize = 0L
            file.walk().forEach { file ->
                if (file.isFile) {
                    totalSize += file.length()
                }
            }
            totalSize
        }
    }
}