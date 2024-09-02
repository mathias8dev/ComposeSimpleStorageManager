package com.mathias8dev.composesimplestoragemanager.data.event

import com.mathias8dev.composesimplestoragemanager.models.MediaInfo
import com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.domain.FilterQuery
import com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.domain.LayoutMode
import com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.domain.SortMode
import java.io.File


sealed class BroadcastEvent : Event() {

    data class FilterEvent(
        val queries: List<FilterQuery>
    ) : BroadcastEvent()

    data class SortEvent(
        val mode: SortMode
    ) : BroadcastEvent()

    data class LayoutEvent(
        val mode: LayoutMode
    ) : BroadcastEvent()


    data class FileSelectedEvent(
        val selectedFiles: List<File>
    ) : BroadcastEvent()

    data class MediaSelectedEvent(
        val selectedMedias: List<MediaInfo>
    ) : BroadcastEvent()

    data object UnselectAllFilesEvent : BroadcastEvent()


    data object SelectAllEvent : BroadcastEvent()

    data object ReloadEvent : BroadcastEvent()
}