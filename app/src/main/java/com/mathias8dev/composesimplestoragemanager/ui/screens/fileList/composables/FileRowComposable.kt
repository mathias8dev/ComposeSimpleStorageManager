package com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.composables

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mathias8dev.composesimplestoragemanager.R
import com.mathias8dev.composesimplestoragemanager.ui.composables.HighlightText
import com.mathias8dev.composesimplestoragemanager.ui.composables.ImageLoaderComposable
import com.mathias8dev.composesimplestoragemanager.utils.isImageMimeType
import com.mathias8dev.composesimplestoragemanager.utils.toFileFormat
import com.mathias8dev.composesimplestoragemanager.utils.toReadableSize
import de.datlag.mimemagic.MimeData
import de.datlag.mimemagic.MimeSuffix
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileRowComposable(
    modifier: Modifier = Modifier,
    file: File,
    selected: Boolean = false,
    innerPaddingValues: PaddingValues = PaddingValues(0.dp),
    query: String = "",
    onClick: (File) -> Unit,
    onLongClick: ((File) -> Unit)? = null,
) {

    val localContext = LocalContext.current

    val mimeData = remember(file) {
        runCatching {
            MimeData.fromFile(file)
        }.getOrNull()
    }

    val iconResource = remember(file, mimeData) {
        when {
            file.isDirectory -> R.drawable.ic_folder
            file.extension.isTorrent() -> R.drawable.ic_utorrent_logo
            file.extension.isCodeFile() -> R.drawable.ic_code_icon
            mimeData?.isDocument == true && file.extension.isPdfDocument() -> R.drawable.ic_pdf_icon
            mimeData?.isDocument == true && mimeData.suffix?.isWordDocument() == true -> R.drawable.ic_word_icon
            mimeData?.isDocument == true && mimeData.suffix?.isExcelDocument() == true -> R.drawable.ic_excel_icon
            mimeData?.isText == true || mimeData?.isDocument == true -> R.drawable.ic_text_icon
            mimeData?.isAudio == true -> R.drawable.ic_music_icon
            file.extension.isAndroidApk() -> file.apkFileIcon(localContext) ?: R.drawable.ic_archives_icon
            mimeData?.isArchive == true -> R.drawable.ic_archives_icon
            else -> R.drawable.ic_question_mark
        }
    }

    val lastModifiedDate = remember(file) {
        LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.of("Europe/Paris"))
    }

    Row(
        modifier = modifier
            .combinedClickable(
                onClick = {
                    onClick(file)
                },
                onLongClick = {
                    onLongClick?.invoke(file)
                }
            )
            .fillMaxWidth()
            .background(
                color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2F)
                else Color.Transparent
            )
            .padding(innerPaddingValues)
    ) {
        Card(modifier = Modifier.size(48.dp)) {
            Box {
                when {
                    mimeData?.isImage == true || mimeData?.isVideo == true || file.isImageMimeType() -> {
                        ImageLoaderComposable(
                            model = file,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    else -> {
                        ImageLoaderComposable(
                            model = iconResource,
                            modifier = Modifier
                                .padding(if (file.isDirectory) 0.dp else 4.dp)
                                .size(48.dp)
                        )
                    }
                }

                if (file.isMusicDirectory()) {
                    Image(
                        painter = painterResource(R.drawable.ic_music_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 3.dp, y = 2.dp)
                            .size(32.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .width(0.dp)
                    .weight(1F),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                HighlightText(
                    text = file.name,
                    highlightWith = query,
                )

                Text(
                    text = lastModifiedDate.toFileFormat(),
                    fontSize = 12.sp
                )
            }

            Text(
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(bottom = 18.dp),
                text = if (file.isDirectory) "(${(file.listFiles()?.size ?: 0)})" else file.toReadableSize(),
                fontSize = 14.sp
            )
        }
    }
}

fun String.isCodeFile(): Boolean {
    return this.equals("json", true) ||
            this.equals(".html", true) ||
            this.equals(".htm", true) ||
            this.equals("c", true) ||
            this.equals("kt", true) ||
            this.equals("java", true) ||
            this.equals("py", true) ||
            this.equals("js", true) ||
            this.equals("css", true) ||
            this.equals("scss", true) ||
            this.equals("sass", true) ||
            this.equals("less", true) ||
            this.equals("php", true) ||
            this.equals("sql", true) ||
            this.equals("xml", true) ||
            this.equals("yaml", true) ||
            this.equals("yml", true) ||
            this.equals("md", true)
}

fun String.isPdfDocument(): Boolean {
    return this.equals("pdf", true)
}

fun String.isTorrent(): Boolean {
    return this.equals("torrent", true)
}

fun File.isMusicDirectory(): Boolean {
    return this.isDirectory &&
            (this.name.contains("audio", true) ||
                    this.name.contains("music", true) ||
                    this.name.contains("musique", true) ||
                    this.name.contains("sound", true) ||
                    this.name.contains("son", true) ||
                    this.name.contains("mp3", true) ||
                    this.name.contains("alarms", true))
}

fun String.isWordDocument() = this == MimeSuffix.DOC || this == MimeSuffix.DOCX || this == MimeSuffix.DOCM

fun String.isExcelDocument() = this == MimeSuffix.XLS || this == MimeSuffix.XLSX || this == MimeSuffix.XLTX || this == MimeSuffix.XLSM

fun String.isAndroidApk() = this.equals("apk", true)

fun File.apkFileIcon(context: Context): Drawable? {
    val path = this.absolutePath
    return runCatching {
        val packageInfo = context.packageManager.getPackageArchiveInfo(path, 0)

        // the secret are these two lines....
        packageInfo?.let { pi ->
            pi.applicationInfo.sourceDir = path
            pi.applicationInfo.publicSourceDir = path
            pi.applicationInfo.loadIcon(context.packageManager)
        }
    }.getOrNull()
}