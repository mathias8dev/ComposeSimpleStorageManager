package com.mathias8dev.composesimplestoragemanager.ui.screens.fileList

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mathias8dev.composesimplestoragemanager.LocalSnackbarHostState
import com.mathias8dev.composesimplestoragemanager.data.event.BroadcastEvent
import com.mathias8dev.composesimplestoragemanager.data.event.EventBus
import com.mathias8dev.composesimplestoragemanager.models.MediaInfo
import com.mathias8dev.composesimplestoragemanager.ui.composables.AutoGrowTabs
import com.mathias8dev.composesimplestoragemanager.ui.composables.MediaGroup
import com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.composables.CssHomeComposable
import com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.composables.FileListComposable
import com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.composables.FileListScreenLayout
import com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.composables.MediaStoreFileListComposable
import com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.composables.stdOnFileClick
import com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.domain.FilterQuery
import com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.domain.SortMode
import com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.extensions.asContentSize
import com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.savers.fileListSaver
import com.mathias8dev.composesimplestoragemanager.utils.asFileReadableSize
import com.mathias8dev.composesimplestoragemanager.utils.otherwise
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import de.datlag.mimemagic.MimeData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination
@RootNavGraph(start = true)
fun FileListScreen(
    rootPath: String? = MediaGroup.InternalStorage.path,
    filterQueries: List<FilterQuery> = emptyList(),
    sortMode: SortMode = SortMode.NAME_AZ,
    navigator: DestinationsNavigator
) {

    val coroutineScope = rememberCoroutineScope()
    val localContext = LocalContext.current
    val viewModel: FileListScreenViewModel = koinViewModel()


    val tabIndex = viewModel.tabIndex.collectAsStateWithLifecycle()
    val dragState by viewModel.dragState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val selectedColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(tabIndex.value) {
        listState.animateScrollToItem(tabIndex.value)
    }


    val reallyWantedMedia by viewModel.reallyWantedMedia.collectAsStateWithLifecycle()

    val reallyRootPath by viewModel.reallyRootPath.collectAsStateWithLifecycle()

    val isCurrentStackEmpty by viewModel.isCurrentStackEmpty

    var wantedQueries by rememberSaveable(filterQueries) {
        mutableStateOf(filterQueries)
    }

    var wantedSortMode by rememberSaveable(sortMode) {
        mutableStateOf(sortMode)
    }

    var recompose by rememberSaveable {
        mutableLongStateOf(System.currentTimeMillis())
    }

    val pullRefreshState = rememberPullToRefreshState()

    var selectAll by rememberSaveable {
        mutableStateOf(false)
    }

    // Create a custom file saver


    var selectedFiles by rememberSaveable(saver = fileListSaver) {
        mutableStateOf(emptyList())
    }

    var selectedMedias: List<MediaInfo> by rememberSaveable {
        mutableStateOf(emptyList())
    }

    val localSnackbarHostState = LocalSnackbarHostState.current


    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            delay(500)
            EventBus.publish(BroadcastEvent.ReloadEvent)
            pullRefreshState.endRefresh()
        }
    }

    LaunchedEffect(Unit) {
        EventBus.subscribe<BroadcastEvent> {
            Timber.d("On Event: $it")
            when (it) {
                is BroadcastEvent.FilterEvent -> {
                    wantedQueries = it.queries
                }

                is BroadcastEvent.SortEvent -> {
                    wantedSortMode = it.mode
                }

                is BroadcastEvent.LayoutEvent -> {

                }

                BroadcastEvent.ReloadEvent -> {
                    recompose = System.currentTimeMillis()
                }

                BroadcastEvent.SelectAllEvent -> {
                    selectAll = !selectAll
                }

                is BroadcastEvent.FileSelectedEvent -> {
                    selectedFiles = it.selectedFiles
                }

                is BroadcastEvent.MediaSelectedEvent -> {
                    selectedMedias = it.selectedMedias
                }

                else -> Unit
            }
        }
    }

    BackHandler(!isCurrentStackEmpty) {
        viewModel.onPopCurrentBackStack()
    }

    FileListScreenLayout(
        onNavigateToMediaGroup = {
            if (it.path != reallyRootPath) {
                viewModel.onBackStackEntryChanged(
                    entry = BackStackEntry(
                        path = it.path,
                        filterQueries = emptyList(),
                        sortMode = SortMode.NAME_AZ
                    )
                )
                selectedFiles = emptyList()
                selectedMedias = emptyList()
            }
        },
        title = {
            Column {
                AnimatedVisibility(visible = selectedFiles.isEmpty() && selectedMedias.isEmpty()) {
                    Text(text = "Accueil")
                }

                AnimatedVisibility(visible = selectedFiles.isNotEmpty() || selectedMedias.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    EventBus.publish(BroadcastEvent.SelectAllEvent)
                                    EventBus.publish(BroadcastEvent.SelectAllEvent)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                            )
                        }

                        Column(
                            modifier = Modifier
                                .wrapContentSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            var selectedFilesSize by remember {
                                mutableLongStateOf(0L)
                            }

                            LaunchedEffect(selectedFiles.size) {
                                selectedFilesSize = selectedFiles.sumOf { it.asContentSize() }
                            }
                            Text(
                                "${selectedFiles.size}",
                                fontSize = 12.sp,
                                lineHeight = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = selectedFilesSize.asFileReadableSize(),
                                fontSize = 12.sp,
                                lineHeight = 12.sp
                            )
                        }

                        IconButton(
                            onClick = { }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                            )
                        }

                        IconButton(
                            onClick = {}
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null
                            )
                        }

                        IconButton(
                            onClick = {}
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCut,
                                contentDescription = null
                            )
                        }

                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Default.TextFormat,
                                contentDescription = null
                            )

                        }
                    }
                }
            }
        }
    ) { innerPadding, drawerState ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AutoGrowTabs(
                selectedIndex = tabIndex.value,
                tabs = viewModel.tabs,
                tabToString = { it.title },
                tabToKey = { _, tab -> tab.id },
                selectedColor = selectedColor,
                onRemoveTabAt = viewModel::onTabRemovedAt,
                onTabClicked = viewModel::onTabIndexChanged
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(pullRefreshState.nestedScrollConnection)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .draggable(
                            state = dragState,
                            orientation = Orientation.Horizontal,
                            onDragStopped = {
                                viewModel.onTabIndexChangedBasedOnSwipe()
                            }
                        )
                ) {
                    reallyWantedMedia?.let {
                        MediaStoreFileListComposable(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(),
                            wantedMedia = it,
                            navigator = navigator,
                            recompositionKey = recompose,
                        )
                    }.otherwise {
                        when (reallyRootPath) {
                            MediaGroup.Home.path -> {
                                CssHomeComposable(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 16.dp),
                                    onNavigateToMediaGroup = {
                                        viewModel.onBackStackEntryChanged(
                                            entry = BackStackEntry(
                                                path = it.path,
                                                filterQueries = emptyList(),
                                                sortMode = SortMode.NAME_AZ
                                            )
                                        )
                                    }
                                )
                            }

                            else -> {
                                FileListComposable(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(),
                                    recompositionKey = recompose,
                                    selectAll = selectAll,
                                    rootPath = reallyRootPath,
                                    filterQueries = wantedQueries,
                                    sortMode = wantedSortMode,
                                    onFileSelectedEvent = {
                                        coroutineScope.launch {
                                            Timber.d("OnEvent: $it")
                                            EventBus.publish(BroadcastEvent.FileSelectedEvent(it))
                                        }
                                    },
                                    onFileClick = { file ->
                                        val mimeData = kotlin.runCatching { MimeData.fromFile(file) }.getOrNull()
                                        when {
                                            file.isFile -> {
                                                coroutineScope.launch {
                                                    stdOnFileClick(
                                                        file = file,
                                                        dataMime = mimeData,
                                                        context = localContext,
                                                    )
                                                }
                                            }

                                            file.isDirectory -> {
                                                viewModel.onBackStackEntryChanged(
                                                    entry = BackStackEntry(
                                                        path = file.path,
                                                        filterQueries = emptyList(),
                                                        sortMode = SortMode.NAME_AZ
                                                    )
                                                )
                                            }

                                        }

                                    }
                                )
                            }
                        }
                    }
                }


                PullToRefreshContainer(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-32).dp),
                    state = pullRefreshState
                )
            }
        }
    }

}