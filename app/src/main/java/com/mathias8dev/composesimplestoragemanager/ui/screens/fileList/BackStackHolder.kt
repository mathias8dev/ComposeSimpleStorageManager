package com.mathias8dev.composesimplestoragemanager.ui.screens.fileList

import android.os.Parcelable
import androidx.collection.mutableScatterMapOf
import androidx.compose.runtime.mutableIntStateOf
import com.mathias8dev.composesimplestoragemanager.ui.composables.MediaGroup
import com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.domain.FilterQuery
import com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.domain.SortMode
import kotlinx.parcelize.Parcelize
import timber.log.Timber


class BackStackHolder {

    private val currentPosition = mutableIntStateOf(0)

    // The key is the tab index
    private val stack = mutableScatterMapOf<Int, List<BackStackEntry>>()

    fun setCurrentPosition(position: Int) {
        currentPosition.intValue = position
        Timber.d("setCurrentPosition: $position")
    }

    fun addAt(position: Int = currentPosition.intValue, entry: BackStackEntry) {
        stack[position] = stack.getOrElse(position) { emptyList() } + entry
        Timber.d("addAt: $position - $entry")
    }

    fun isStackEmptyAt(position: Int = currentPosition.intValue): Boolean {
        return stack[position].isNullOrEmpty()
    }

    fun addOrUpdateAt(position: Int = currentPosition.intValue, entry: BackStackEntry) {
        addAt(position, entry)
    }

    fun getAt(position: Int = currentPosition.intValue): BackStackEntry? {
        val entry = stack.getOrElse(position) { emptyList() }.lastOrNull()
        Timber.d("getAt: $position - $entry")
        return entry
    }

    fun removeAt(position: Int = currentPosition.intValue) {
        stack[position] = stack.getOrElse(position) { emptyList() }.dropLast(1)
    }

    fun removeAllAt(position: Int = currentPosition.intValue) {
        stack[position] = emptyList()
    }


    fun clear() {
        stack.clear()
    }

    fun getOrAddAt(position: Int = currentPosition.intValue, backStackEntry: BackStackEntry): BackStackEntry {
        val entry = if (isStackEmptyAt(position)) {
            addAt(position, backStackEntry)
            backStackEntry
        } else {
            getAt(position)!!
        }
        Timber.d("getOrAddAt: $position - $entry")
        return entry
    }

    companion object {
        val default = BackStackEntry(
            path = MediaGroup.InternalStorage.path,
            filterQueries = emptyList(),
            sortMode = SortMode.NAME_AZ
        )
    }
}


@Parcelize
data class BackStackEntry(
    val path: String? = MediaGroup.InternalStorage.path,
    val filterQueries: List<FilterQuery> = emptyList(),
    val sortMode: SortMode = SortMode.NAME_AZ,
) : Parcelable
