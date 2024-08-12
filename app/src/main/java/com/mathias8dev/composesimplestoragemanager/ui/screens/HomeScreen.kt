package com.mathias8dev.composesimplestoragemanager.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mathias8dev.composesimplestoragemanager.LocalSnackbarHostState
import com.mathias8dev.composesimplestoragemanager.R
import com.mathias8dev.composesimplestoragemanager.ui.ImageLoaderComposable
import com.mathias8dev.composesimplestoragemanager.ui.StoragePermissionRequestUi
import com.mathias8dev.composesimplestoragemanager.ui.screens.destinations.FileListScreenDestination
import com.mathias8dev.composesimplestoragemanager.ui.screens.destinations.PreviewScreenDestination
import com.mathias8dev.permissionhelper.permission.OneShotPermissionsHelper
import com.mathias8dev.permissionhelper.permission.Permission
import com.mathias8dev.permissionhelper.permission.PermissionState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import de.datlag.mimemagic.MimeData
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@OptIn(ExperimentalFoundationApi::class)
@Composable
@Destination
@RootNavGraph
fun FileListScreen(
    rootPath: String?,
    navigator: DestinationsNavigator
) {
    val localSnackbarHostState = LocalSnackbarHostState.current
    val coroutineScope = rememberCoroutineScope()
    val localContext = LocalContext.current

    val files by remember(rootPath) {
        derivedStateOf {
            rootPath?.let { File(it).listFiles()?.toList() } ?: emptyList()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(items = files) { _, file ->
            FileComposable(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .animateItemPlacement(),
                file = file,
                onClick = {
                    if (file.isFile && file.isImageMimeType()) {
                        navigator.navigate(PreviewScreenDestination(file.path))
                    } else if (file.isDirectory) {
                        navigator.navigate(
                            FileListScreenDestination(
                                rootPath = file.absolutePath
                            )
                        )
                    } else {

                        coroutineScope.launch {
                            val uri = file.asContentSchemeUri(localContext)
                            val mimeData = MimeData.fromFile(file)
                            val intent = Intent()
                            intent.action = Intent.ACTION_VIEW
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            intent.setDataAndType(uri, mimeData.mimeType)
                            val chooserIntent = Intent.createChooser(intent, "Veuillez sélectionner une application")
                            localContext.startActivity(chooserIntent)
                        }

                    }
                }
            )
        }
    }
}

suspend fun File.asContentSchemeUri(context: Context): Uri? {
    return suspendCoroutine { continuation ->
        val mediaScannerClient = object : MediaScannerConnection.MediaScannerConnectionClient {
            var connection: MediaScannerConnection? = null

            init {
                connection = MediaScannerConnection(context.applicationContext, this)
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

@Composable
@Destination
@RootNavGraph(start = true)
fun HomeScreen(
    navigator: DestinationsNavigator
) {

    var showHomeScreenContent by remember {
        mutableStateOf(false)
    }



    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val permissions = remember {
            mutableListOf<Permission>().apply {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    add(Permission(Manifest.permission.READ_MEDIA_AUDIO))
                    add(Permission(Manifest.permission.READ_MEDIA_VIDEO))
                    add(Permission(Manifest.permission.READ_MEDIA_IMAGES))
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    add(Permission(Manifest.permission.MANAGE_EXTERNAL_STORAGE))
                }

                if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.Q) {
                    add(Permission(Manifest.permission.READ_EXTERNAL_STORAGE))
                    add(Permission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }
            }
        }

        OneShotPermissionsHelper(
            permissions = permissions,
            permissionRequestUi = StoragePermissionRequestUi
        ) {
            val permissionScope = this

            LaunchedEffect(permissionScope) {
                permissionScope.launchPermissions { result ->
                    if (result.all { it.second == PermissionState.Granted }) {
                        showHomeScreenContent = true
                    } else {
                        navigator.popBackStack()
                    }
                }
            }
        }

        AnimatedVisibility(visible = showHomeScreenContent) {
            FileListScreen(
                rootPath = Environment.getExternalStorageDirectory().absolutePath,
                navigator = navigator
            )
        }
    }
}


@Composable
fun FileComposable(
    modifier: Modifier = Modifier,
    file: File,
    onClick: (File) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable {
                onClick(file)
            }
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card {


            if (file.isFile) {
                val mimeData = remember {
                    MimeData.fromFile(file)
                }
                if (mimeData.isImage || mimeData.isVideo) {
                    ImageLoaderComposable(
                        model = file,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(48.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(
                            if (file.isDirectory) R.drawable.ic_folder
                            else if (mimeData.isText) R.drawable.ic_short_text
                            else R.drawable.ic_question_mark
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                }
            } else {
                Image(
                    painter = painterResource(R.drawable.ic_folder),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
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
                    .padding(8.dp)
                    .width(0.dp)
                    .weight(1F),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                Text(
                    text = file.name,
                    maxLines = 1
                )
                val lastModifiedDate = remember {
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.of("Europe/Paris"))
                }
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

fun LocalDateTime.toFileFormat(): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm a")
    return this.format(formatter)
}

fun File.isImageMimeType(): Boolean {
    return this.extension == "jpg" || this.extension == "png" || this.extension == "jpeg"
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