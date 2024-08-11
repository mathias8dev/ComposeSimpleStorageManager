package com.mathias8dev.composesimplestoragemanager.ui

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import com.mathias8dev.composesimplestoragemanager.BuildConfig
import java.io.File


@Stable
fun Modifier.on(
    condition: Boolean,
    use: (currentModifier: Modifier) -> Modifier
): Modifier {
    return if (condition) use(this) else this
}


fun Dp.toPx(): Float = (this.value * Resources.getSystem().displayMetrics.density)
fun Float.toPx(): Float = (this * Resources.getSystem().displayMetrics.density)

fun <T> Boolean.select(first: T, second: T): T = if (this) first else second


fun File.getUri(context: Context): Uri {
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.android.fileprovider",
        this
    )
}

fun Uri.toContentFile(context: Context): File {
    if (ContentResolver.SCHEME_CONTENT == this.scheme) {
        val cr: ContentResolver = context.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        val extensionFile = mimeTypeMap.getExtensionFromMimeType(cr.getType(this))
        val file = File.createTempFile(
            this.contentSchemeName(context),
            ".$extensionFile",
            context.cacheDir
        )
        val input = cr.openInputStream(this)
        file.outputStream().use { stream ->
            input?.copyTo(stream)
        }
        input?.close()
        return file
    }

    return this.toFile()
}

fun Uri.contentSchemeName(context: Context): String {
    return runCatching {
        context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
            if (!cursor.moveToFirst()) return@use null

            val name = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.getString(name)
        } ?: System.currentTimeMillis().toString()
    }.getOrElse { System.currentTimeMillis().toString() }
}


fun Context.findActivity(): ComponentActivity {
    var context = this
    while (context is ContextWrapper) {
        if (context is ComponentActivity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Picture in picture should be called in the context of an Activity")
}

fun PaddingValues.asStdNotConsumedValues() = PaddingValues(
    top = calculateTopPadding(),
    bottom = calculateBottomPadding()
)

fun <T> T?.otherwise(value: T): T = this ?: value
inline fun <T> T?.otherwise(block: () -> T): T = this ?: block()


@Composable
fun stdNotConsumedPaddingValues() = PaddingValues(
    top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding(),
    bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
)