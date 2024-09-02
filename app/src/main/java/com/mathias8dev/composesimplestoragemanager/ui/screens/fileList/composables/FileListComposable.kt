package com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.composables

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.mathias8dev.composesimplestoragemanager.models.MediaInfo
import com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.domain.FilterQuery
import com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.domain.SortMode
import com.mathias8dev.composesimplestoragemanager.utils.otherwise
import kotlinx.coroutines.flow.MutableSharedFlow
import my.nanihadesuka.compose.LazyColumnScrollbar
import timber.log.Timber
import java.io.File

class FileListComposableBehaviourController {

    private val _eventEmitter = MutableSharedFlow<Event>()


    fun selectAll() {

    }

    fun unselectAll() {

    }

    sealed class Event {
        data object SelectAll : Event()
        data object UnselectAll : Event()
    }
}

@Composable
fun FileListComposable(
    modifier: Modifier = Modifier,
    recompositionKey: Any,
    selectAll: Boolean = false,
    rootPath: String?,
    filterQueries: List<FilterQuery> = emptyList(),
    sortMode: SortMode = SortMode.NAME_AZ,
    onFileClick: (File) -> Unit,
    onFileSelectedEvent: ((List<File>) -> Unit)? = null
) {

    Timber.d("FileListComposable recompositionKey: $recompositionKey")
    val files by remember(rootPath, recompositionKey) {
        derivedStateOf {
            rootPath?.let {
                val file = File(it)
                if (file.exists()) {
                    file.listFiles()?.toList()
                } else {
                    emptyList()
                }
            } ?: emptyList()
        }
    }

    FileListComposable(
        modifier = modifier,
        files = files,
        selectAll = selectAll,
        recompositionKey = recompositionKey,
        filterQueries = filterQueries,
        sortMode = sortMode,
        onFileClick = onFileClick,
        onFileSelectedEvent = onFileSelectedEvent
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileListComposable(
    modifier: Modifier = Modifier,
    recompositionKey: Any? = null,
    selectAll: Boolean = false,
    files: List<File>,
    filterQueries: List<FilterQuery> = emptyList(),
    sortMode: SortMode = SortMode.NAME_AZ,
    onFileClick: (File) -> Unit,
    onFileSelectedEvent: ((List<File>) -> Unit)? = null,
) {


    key(recompositionKey) {

        val updatedFiles by remember(filterQueries, sortMode, files) {
            derivedStateOf {
                files.filter {
                    if (filterQueries.isEmpty()) true else filterQueries.all { query -> query.filter(it) }
                }.sortedWith(sortMode)
            }
        }

        val query by remember(filterQueries) {
            derivedStateOf {
                if (filterQueries.isEmpty()) "" else (filterQueries.first { it is FilterQuery.SearchTerm } as? FilterQuery.SearchTerm)?.term.orEmpty()
            }
        }

        val selectedFiles = remember {
            mutableStateListOf<File>()
        }

        LaunchedEffect(selectAll) {
            when {
                selectAll -> {
                    if (selectedFiles.isEmpty()) selectedFiles.addAll(updatedFiles)
                    else selectedFiles.addAll(
                        updatedFiles.filter { !selectedFiles.contains(it) }
                    )
                }

                selectedFiles.isNotEmpty() && selectedFiles.size < updatedFiles.size -> {
                    // Complete the list with not contains files
                    selectedFiles.addAll(
                        updatedFiles.filter { !selectedFiles.contains(it) }
                    )
                }

                else -> {
                    selectedFiles.clear()
                }
            }
            onFileSelectedEvent?.invoke(selectedFiles)
        }


        val listState = rememberLazyListState()
        LazyColumnScrollbar(state = listState) {
            LazyColumn(
                modifier = modifier.padding(top = 16.dp),
                state = listState
            ) {
                itemsIndexed(items = updatedFiles, key = { _, file -> file.absolutePath }) { _, file ->
                    FileRowComposable(
                        modifier = Modifier
                            .padding(vertical = 0.25.dp)
                            .animateItemPlacement(),
                        innerPaddingValues = PaddingValues(vertical = 10.dp, horizontal = 16.dp),
                        selected = selectedFiles.contains(file),
                        file = file,
                        query = query,
                        onClick = {
                            if (selectedFiles.isEmpty()) {
                                onFileClick(it)
                            } else {
                                if (selectedFiles.contains(it)) {
                                    selectedFiles.remove(it)
                                } else {
                                    selectedFiles.add(it)
                                }
                                onFileSelectedEvent?.invoke(selectedFiles)
                            }
                        },
                        onLongClick = {
                            if (selectedFiles.isEmpty()) {
                                selectedFiles.add(it)
                                onFileSelectedEvent?.invoke(selectedFiles)
                            }
                        }
                    )
                }

                if (updatedFiles.isEmpty()) {
                    item {
                        Timber.d("Is empty")
                        NoItemComposable(
                            modifier = Modifier.fillParentMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NoItemComposable(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val theme = MaterialTheme.colorScheme
        Canvas(modifier = Modifier.size(72.dp)) {
            drawRoundRect(
                color = theme.surfaceVariant,
                cornerRadius = CornerRadius(8.dp.toPx()),
                style = Stroke(8.dp.toPx())
            )
        }

        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "No item",
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileListComposable(
    modifier: Modifier = Modifier,
    recompositionKey: Any? = null,
    media: List<MediaInfo>,
    filterQueries: List<FilterQuery> = emptyList(),
    sortMode: SortMode = SortMode.NAME_AZ,
    groupByBucketNameIfOnlyFiles: Boolean = true,
    onFileClick: (File) -> Unit
) {
    val updatedFiles by remember(media) {
        derivedStateOf { media.map { File(it.externalContentUri.toString()) } }
    }
    val groupByBucketName: Boolean by remember(groupByBucketNameIfOnlyFiles, updatedFiles) {
        derivedStateOf {
            val onlyFiles = updatedFiles.all { it.isFile }
            val namesNotNull = media.all { it.bucketName != null }
            val namesCount = media.size
            val namesNull = media.count { it.bucketName == null }
            Timber.d("onlyFiles: $onlyFiles, namesNotNull: $namesNotNull, groupByBucketNameIfOnlyFiles: $groupByBucketNameIfOnlyFiles; namesCount: $namesCount, namesNull: $namesNull")
            groupByBucketNameIfOnlyFiles && onlyFiles
        }
    }

    val groupedMedia: Map<String, List<MediaInfo>>? by remember(updatedFiles, groupByBucketName) {
        derivedStateOf {
            if (groupByBucketName) {
                media.groupBy { it.bucketName.orEmpty() }
            } else {
                null
            }
        }
    }

    var innerNavigate by rememberSaveable {
        mutableStateOf(false)
    }

    var selectedBucketName: String? by rememberSaveable {
        mutableStateOf(null)
    }

    BackHandler(
        innerNavigate
    ) {
        innerNavigate = false
    }


    Column {
        AnimatedVisibility(innerNavigate || !groupByBucketName || groupedMedia == null) {
            FileListComposable(
                modifier = modifier.padding(top = 16.dp),
                recompositionKey = recompositionKey,
                files = if (!groupByBucketName) updatedFiles else groupedMedia?.get(selectedBucketName).otherwise(media).map { File(it.externalContentUri.toString()) },
                filterQueries = filterQueries,
                sortMode = sortMode,
                onFileClick = onFileClick
            )
        }

        AnimatedVisibility(!innerNavigate && groupByBucketName && groupedMedia != null) {
            groupedMedia?.let {
                key(recompositionKey) {
                    LazyColumn(
                        modifier = modifier.padding(top = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(items = it.keys.toList()) { _, bucketName ->
                            BucketNameComposable(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .animateItemPlacement(),
                                bucketName = bucketName,
                                contentSize = it[bucketName]?.size?.toLong() ?: 0,
                                onClick = {
                                    innerNavigate = true
                                    selectedBucketName = bucketName
                                },
                            )
                        }

                        if (it.isEmpty()) {
                            item {
                                NoItemComposable(
                                    modifier = Modifier.fillParentMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}