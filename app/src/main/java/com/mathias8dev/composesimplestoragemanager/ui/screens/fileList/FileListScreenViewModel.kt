package com.mathias8dev.composesimplestoragemanager.ui.screens.fileList

import android.os.Environment
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathias8dev.composesimplestoragemanager.ui.composables.AutoGrowTabController
import com.mathias8dev.composesimplestoragemanager.ui.composables.MediaGroup
import com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.composables.WantedMedia
import com.mathias8dev.composesimplestoragemanager.utils.otherwise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import timber.log.Timber
import java.io.File
import java.util.Locale


@KoinViewModel
class FileListScreenViewModel : ViewModel() {
    private val backStackHolder = BackStackHolder()
    private val tabsController = AutoGrowTabController()

    private val _backStackEntry = MutableStateFlow<BackStackEntry?>(null)
    val backStackEntry = _backStackEntry.asStateFlow()
    private val _reallyWantedMedia = MutableStateFlow<WantedMedia?>(null)
    val reallyWantedMedia = _reallyWantedMedia.asStateFlow()
    private val _reallyRootPath = MutableStateFlow<String?>(null)
    val reallyRootPath = _reallyRootPath.asStateFlow()

    var isCurrentStackEmpty = mutableStateOf(false)
        private set

    val tabs = tabsController.tabs
    val tabIndex = tabsController.tabIndex
    val dragState = tabsController.dragState

    init {
        Timber.d("FileListScreenViewModel init")
        updateStates()
    }

    fun onBackStackEntryChanged(position: Int = tabIndex.value, entry: BackStackEntry) {
        viewModelScope.launch {
            Timber.d("onBackStackEntryChanged: ${tabIndex.value} - $entry")
            backStackHolder.setCurrentPosition(position)
            backStackHolder.addOrUpdateAt(entry = entry)
            updateStates()
            Timber.d("backStackEntry: ${tabIndex.value} - $entry")
        }

    }

    fun onTabIndexChanged(index: Int) {
        viewModelScope.launch {
            Timber.d("onTabIndexChanged: $index")
            tabsController.updateTabIndex(index)
            backStackHolder.setCurrentPosition(index)
            updateStates()
            Timber.d("backStackEntry: ${backStackEntry.value}")
        }
    }

    fun onTabRemovedAt(index: Int) {
        viewModelScope.launch {
            Timber.d("onTabRemovedAt: $index")
            tabsController.removeTabAt(index)
            backStackHolder.removeAt(index)
            backStackHolder.setCurrentPosition(tabIndex.value)
            updateStates()
            Timber.d("backStackEntry: ${backStackEntry.value}")
        }
    }

    fun onTabIndexChangedBasedOnSwipe() {
        viewModelScope.launch {
            Timber.d("onTabIndexChangedBasedOnSwipe")
            tabsController.updateTabIndexBasedOnSwipe()
            backStackHolder.setCurrentPosition(tabIndex.value)
            updateStates()
            Timber.d("backStackEntry: ${backStackEntry.value}")
        }
    }

    fun onPopCurrentBackStack() {
        viewModelScope.launch {
            Timber.d("onPopCurrentBackStack")
            backStackHolder.removeAt()
            updateStates()
            Timber.d("backStackEntry: ${backStackEntry.value}")
        }
    }


    private fun updateStates() {
        Timber.d("updateStates")
        viewModelScope.launch {
            isCurrentStackEmpty.value = backStackHolder.isStackEmptyAt()
            _backStackEntry.value = backStackHolder.getAt().otherwise(BackStackHolder.default)

            backStackEntry.value.let {
                _reallyWantedMedia.value = when (it?.path) {
                    MediaGroup.Audio.path -> WantedMedia.AUDIO
                    MediaGroup.Video.path -> WantedMedia.VIDEO
                    MediaGroup.Image.path -> WantedMedia.IMAGE
                    MediaGroup.Apk.path -> WantedMedia.APK
                    MediaGroup.Archive.path -> WantedMedia.ARCHIVE
                    MediaGroup.Document.path -> WantedMedia.DOCUMENT
                    MediaGroup.App.path -> WantedMedia.APP
                    MediaGroup.RecentFiles.path -> WantedMedia.RECENT_FILES
                    MediaGroup.AllFiles.path -> WantedMedia.ALL_FILES
                    MediaGroup.RecycleBin.path -> WantedMedia.RECENT_FILES
                    else -> null
                }
                _reallyRootPath.value = when (it?.path) {
                    MediaGroup.InternalStorage.path -> Environment.getExternalStorageDirectory().absolutePath
                    else -> it?.path
                }

            }

            reallyWantedMedia.value?.let {
                tabsController.updateTabNameAt(
                    title = it.name
                        .replace("_", " ")
                        .lowercase()
                        .titleCase()
                )
            }.otherwise {
                when (backStackEntry.value?.path) {
                    MediaGroup.InternalStorage.path -> tabsController.updateTabNameAt(title = "Internal storage")
                    MediaGroup.Home.path -> tabsController.updateTabNameAt(title = "Home")
                    else -> reallyRootPath.value?.let { it -> File(it).takeIf { it1 -> it1.exists() }?.name.let { tabsController.updateTabNameAt(title = it.orEmpty()) } }
                }
            }

        }
    }

}

fun String.titleCase(): String = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }