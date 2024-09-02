package com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.composables

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mathias8dev.composesimplestoragemanager.LocalSnackbarHostState
import com.mathias8dev.composesimplestoragemanager.queries.MediaQueryViewModel
import com.mathias8dev.composesimplestoragemanager.ui.composables.InstalledAppComposable
import com.mathias8dev.composesimplestoragemanager.ui.composables.InstalledAppFilter
import com.mathias8dev.composesimplestoragemanager.ui.composables.InstalledAppFilterComposable
import com.mathias8dev.composesimplestoragemanager.ui.composables.StandardLoading
import com.mathias8dev.composesimplestoragemanager.ui.destinations.FileListScreenDestination
import com.mathias8dev.composesimplestoragemanager.ui.imageViewer.ImageViewerActivity
import com.mathias8dev.composesimplestoragemanager.ui.mediaPlayer.MediaPlayerActivity
import com.mathias8dev.composesimplestoragemanager.ui.pdfViewer.PdfViewerActivity
import com.mathias8dev.composesimplestoragemanager.utils.asContentSchemeUri
import com.mathias8dev.composesimplestoragemanager.utils.onLoading
import com.mathias8dev.composesimplestoragemanager.utils.onSuccess
import com.mathias8dev.composesimplestoragemanager.utils.otherwise
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import de.datlag.mimemagic.MimeData
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber
import java.io.File


@Parcelize
enum class WantedMedia : Parcelable {
    AUDIO,
    VIDEO,
    IMAGE,
    APK,
    ARCHIVE,
    DOCUMENT,
    APP,
    ALL_FILES,
    RECENT_FILES,
}

suspend fun <T> launchViewerIntent(
    context: Context,
    file: File,
    mimeType: String?,
    viewerActivity: Class<T>
) = coroutineScope {
    val type = mimeType.otherwise(kotlin.runCatching { MimeData.fromFile(file) }.getOrNull()?.mimeType)
    val intent = Intent()
    intent.setClass(context, viewerActivity)
    val uri = file.asContentSchemeUri(context)
    intent.setDataAndType(uri, type)
    context.startActivity(intent)
}

suspend fun stdOnFileClick(
    context: Context,
    file: File,
    dataMime: MimeData?,
): Unit = coroutineScope {
    if (!file.isFile) return@coroutineScope
    val mimeData = dataMime ?: kotlin.runCatching { MimeData.fromFile(file) }.getOrNull()
    when {
        mimeData?.isImage == true -> {
            launchViewerIntent(
                context,
                file,
                mimeData.mimeType,
                ImageViewerActivity::class.java
            )
        }

        mimeData?.isAudio == true -> {
            launchViewerIntent(
                context,
                file,
                mimeData.mimeType,
                MediaPlayerActivity::class.java
            )

        }

        mimeData?.isVideo == true -> {
            launchViewerIntent(
                context,
                file,
                mimeData.mimeType,
                MediaPlayerActivity::class.java
            )
        }

        mimeData?.isDocument == true && file.extension.isPdfDocument() -> {
            launchViewerIntent(
                context,
                file,
                mimeData.mimeType,
                PdfViewerActivity::class.java
            )
        }

        else -> {
            val uri = file.asContentSchemeUri(context)

            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(uri, mimeData?.mimeType)
            val chooserIntent = Intent.createChooser(intent, "Veuillez sélectionner une application")
            context.startActivity(chooserIntent)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaStoreFileListComposable(
    modifier: Modifier = Modifier,
    recompositionKey: Any,
    wantedMedia: WantedMedia,
    navigator: DestinationsNavigator
) {
    val localContext = LocalContext.current
    val localSnackbarHostState = LocalSnackbarHostState.current
    val coroutineScope = rememberCoroutineScope()
    val onFileClick: (file: File) -> Unit = { file ->
        coroutineScope.launch {
            val mimeData = kotlin.runCatching { MimeData.fromFile(file) }.getOrNull()
            when {

                file.isFile -> {
                    stdOnFileClick(localContext, file, mimeData)
                }

                file.isDirectory -> {
                    navigator.navigate(
                        FileListScreenDestination(
                            rootPath = file.absolutePath
                        )
                    )
                }

            }
        }
    }

    val mediaQueryViewModel: MediaQueryViewModel = koinViewModel()

    key(recompositionKey) {
        when (wantedMedia) {
            WantedMedia.VIDEO -> {
                LaunchedEffect(Unit) {
                    mediaQueryViewModel.queryAllVideos()
                }
                val videosResource by mediaQueryViewModel.allVideosResource.collectAsStateWithLifecycle()
                val videosFiles by mediaQueryViewModel.allVideosFiles

                videosResource.onLoading {
                    StandardLoading(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                videosResource.onSuccess {
                    FileListComposable(
                        modifier = modifier,
                        recompositionKey = recompositionKey,
                        files = videosFiles,
                        onFileClick = onFileClick
                    )
                }
            }

            WantedMedia.AUDIO -> {
                LaunchedEffect(Unit) {
                    mediaQueryViewModel.queryAllAudios()
                }
                val audiosResource by mediaQueryViewModel.allAudiosResource.collectAsStateWithLifecycle()
                val audioFiles by mediaQueryViewModel.allAudiosFiles
                audiosResource.onLoading {
                    StandardLoading(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                audiosResource.onSuccess {
                    FileListComposable(
                        modifier = modifier,
                        recompositionKey = recompositionKey,
                        files = audioFiles,
                        onFileClick = onFileClick
                    )
                }
            }

            WantedMedia.IMAGE -> {
                LaunchedEffect(Unit) {
                    mediaQueryViewModel.queryAllImages()
                }
                val imagesResource by mediaQueryViewModel.allImagesResource.collectAsStateWithLifecycle()

                imagesResource.onLoading {
                    StandardLoading(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                imagesResource.onSuccess { images ->
                    FileListComposable(
                        modifier = modifier,
                        recompositionKey = recompositionKey,
                        media = images,
                        onFileClick = onFileClick
                    )
                }
            }

            WantedMedia.ARCHIVE -> {
                LaunchedEffect(Unit) {
                    mediaQueryViewModel.queryAllArchives()
                }
                val archivesResource by mediaQueryViewModel.allArchivesResource.collectAsStateWithLifecycle()
                val archiveFiles by mediaQueryViewModel.allArchivesFiles
                archivesResource.onLoading {
                    StandardLoading(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                archivesResource.onSuccess {
                    FileListComposable(
                        modifier = modifier,
                        recompositionKey = recompositionKey,
                        files = archiveFiles,
                        onFileClick = onFileClick
                    )
                }
            }

            WantedMedia.APK -> {
                LaunchedEffect(Unit) {
                    mediaQueryViewModel.queryAllApks()
                }
                val apksResource by mediaQueryViewModel.allApksResource.collectAsStateWithLifecycle()
                val apkFiles by mediaQueryViewModel.allApksFiles
                apksResource.onLoading {
                    StandardLoading(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                apksResource.onSuccess {
                    FileListComposable(
                        modifier = modifier,
                        recompositionKey = recompositionKey,
                        files = apkFiles,
                        onFileClick = onFileClick
                    )
                }
            }

            WantedMedia.ALL_FILES -> {
                LaunchedEffect(Unit) {
                    mediaQueryViewModel.queryAllFiles()
                }
                val mediaResource by mediaQueryViewModel.allMediaResource.collectAsStateWithLifecycle()
                val mediaFiles by mediaQueryViewModel.allMediaFiles
                mediaResource.onLoading {
                    StandardLoading(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                mediaResource.onSuccess {
                    FileListComposable(
                        modifier = modifier,
                        recompositionKey = recompositionKey,
                        files = mediaFiles,
                        onFileClick = onFileClick
                    )
                }
            }

            WantedMedia.RECENT_FILES -> {
                LaunchedEffect(Unit) {
                    mediaQueryViewModel.queryAllRecentFiles()
                }
                val recentFilesResource by mediaQueryViewModel.allRecentFilesResource.collectAsStateWithLifecycle()
                val recentFilesFiles by mediaQueryViewModel.allRecentFilesFiles

                recentFilesResource.onLoading {
                    StandardLoading(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                recentFilesResource.onSuccess {
                    FileListComposable(
                        modifier = modifier,
                        recompositionKey = recompositionKey,
                        files = recentFilesFiles,
                        onFileClick = onFileClick
                    )
                }
            }

            WantedMedia.DOCUMENT -> {
                LaunchedEffect(Unit) {
                    mediaQueryViewModel.queryAllDocuments()
                }
                val documentsResource by mediaQueryViewModel.allDocumentsResource.collectAsStateWithLifecycle()

                documentsResource.onLoading {
                    StandardLoading(
                        modifier = Modifier.fillMaxSize()
                    )
                }
                documentsResource.onSuccess { documents ->
                    FileListComposable(
                        modifier = modifier,
                        recompositionKey = recompositionKey,
                        media = documents,
                        onFileClick = onFileClick
                    )
                }
            }


            WantedMedia.APP -> {
                LaunchedEffect(Unit) {
                    mediaQueryViewModel.queryInstalledApps()
                }
                val installedAppsResource by mediaQueryViewModel.allInstalledAppsResource.collectAsStateWithLifecycle()

                installedAppsResource.onLoading {
                    Timber.d("Loading")
                    StandardLoading(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                installedAppsResource.onSuccess { installedApps ->
                    Timber.d("Success")
                    var installedAppFilter by rememberSaveable {
                        mutableStateOf(InstalledAppFilter.ALL)
                    }
                    val filtered by remember(installedApps, installedAppFilter) {
                        derivedStateOf {
                            when (installedAppFilter) {
                                InstalledAppFilter.ALL -> installedApps
                                InstalledAppFilter.SYSTEM -> installedApps.filter { it.isSystemApp }
                                InstalledAppFilter.USER -> installedApps.filter { !it.isSystemApp }
                            }
                        }
                    }

                    val listState = rememberLazyListState()

                    LazyColumnScrollbar(
                        modifier = modifier,
                        state = listState,
                        settings = ScrollbarSettings.Default
                    ) {
                        LazyColumn(
                            modifier = Modifier,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            state = listState
                        ) {
                            stickyHeader {
                                Row(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surface)
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp)
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    InstalledAppFilterComposable(
                                        filter = InstalledAppFilter.ALL,
                                        selected = installedAppFilter == InstalledAppFilter.ALL,
                                        onClick = {
                                            installedAppFilter = InstalledAppFilter.ALL
                                        }
                                    )

                                    InstalledAppFilterComposable(
                                        filter = InstalledAppFilter.SYSTEM,
                                        selected = installedAppFilter == InstalledAppFilter.SYSTEM,
                                        onClick = {
                                            installedAppFilter = InstalledAppFilter.SYSTEM
                                        }
                                    )

                                    InstalledAppFilterComposable(
                                        filter = InstalledAppFilter.USER,
                                        selected = installedAppFilter == InstalledAppFilter.USER,
                                        onClick = {
                                            installedAppFilter = InstalledAppFilter.USER
                                        }
                                    )
                                }

                            }
                            items(filtered) {
                                InstalledAppComposable(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                        .animateItemPlacement(),
                                    app = it,
                                    onClick = { app ->
                                        val intent = localContext.packageManager.getLaunchIntentForPackage(app.packageName)
                                        if (intent != null) {
                                            localContext.startActivity(intent)
                                        } else {
                                            coroutineScope.launch {
                                                localSnackbarHostState.showSnackbar("Unable to launch app")
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

            }

        }
    }
}


