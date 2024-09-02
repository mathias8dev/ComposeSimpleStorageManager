package com.mathias8dev.composesimplestoragemanager.utils

import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import com.mathias8dev.composesimplestoragemanager.BuildConfig
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


fun LocalDateTime.toFileFormat(): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm a")
    return this.format(formatter)
}


fun File.isImageMimeType(): Boolean {
    return this.extension == "jpg" ||
            this.extension == "png" ||
            this.extension == "jpeg" ||
            this.extension == "bmp" ||
            this.extension == "webp" ||
            (this.extension == "gif" && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) ||
            (this.extension == "heif" && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
}

fun File.isTextMimeType(): Boolean {
    return this.extension == "txt"
}

fun File.toReadableSize(): String {
    return this.length().asFileReadableSize()
}

fun Number.asFileReadableSize(): String {
    val length = this.toLong()
    val kbLimit = 1024
    val moLimit = 1024 * 1024
    val goLimit = 1024 * 1024 * 1024
    if (length > goLimit) return "${length / goLimit}Go"
    if (length > moLimit) return "${length / moLimit}Mo"
    if (length > kbLimit) return "${length / kbLimit}Kb"
    return "${length}Octets"
}


suspend fun File.asContentSchemeUri(context: Context): Uri? {
    return suspendCoroutine { continuation ->
        val mediaScannerClient = object : MediaScannerConnection.MediaScannerConnectionClient {
            var connection: MediaScannerConnection? = null

            init {
                connection = MediaScannerConnection(context, this)
                connection?.connect()
            }

            override fun onMediaScannerConnected() {
                connection?.scanFile(absolutePath, null)
            }

            override fun onScanCompleted(path: String, uri: Uri?) {
                connection?.disconnect()

                continuation.resume(uri)
            }
        }
    }
}


@Stable
fun Modifier.on(
    condition: Boolean,
    use: (currentModifier: Modifier) -> Modifier
): Modifier {
    return if (condition) use(this) else this
}


fun Dp.toPx(): Float = (this.value * Resources.getSystem().displayMetrics.density)
fun Number.toPx(): Float = (this.toFloat() * Resources.getSystem().displayMetrics.density)

@Composable
fun Number.pxToDp() = with(LocalDensity.current) { this@pxToDp.toInt().toDp() }

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }


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