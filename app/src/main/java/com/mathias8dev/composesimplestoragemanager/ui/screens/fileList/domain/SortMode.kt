package com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.domain

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.mathias8dev.composesimplestoragemanager.R
import kotlinx.parcelize.Parcelize
import java.io.File
import java.util.Random


@Parcelize
enum class SortMode(
    @DrawableRes val iconRes: Int? = null,
    val nameRes: Int,
) : Parcelable, Comparator<File> {
    NAME_AZ(nameRes = R.string.name_az) {
        override fun compare(first: File, second: File): Int {
            return first.name.compareTo(second.name)
        }
    },
    NAME_ZA(nameRes = R.string.name_za) {
        override fun compare(first: File, second: File): Int {
            return second.name.compareTo(first.name)
        }
    },
    SIZE_SMALLER(nameRes = R.string.size_smaller) {
        override fun compare(first: File, second: File): Int {
            return first.length().compareTo(second.length())
        }
    },
    SIZE_BIGGER(nameRes = R.string.size_bigger) {
        override fun compare(first: File, second: File): Int {
            return second.length().compareTo(first.length())
        }
    },
    DATE_OLDER(nameRes = R.string.date_older) {
        override fun compare(first: File, second: File): Int {
            return first.lastModified().compareTo(second.lastModified())
        }
    },
    DATE_NEWER(nameRes = R.string.date_newer) {
        override fun compare(first: File, second: File): Int {
            return second.lastModified().compareTo(first.lastModified())
        }
    },
    TYPE_ASCENDING(nameRes = R.string.type_ascending) {
        override fun compare(first: File, second: File): Int {
            // Directories should appear before files
            if (first.isDirectory && !second.isDirectory) return -1
            if (!first.isDirectory && second.isDirectory) return 1

            // If both are directories or both are files, compare by extension
            val extComparison = first.extension.compareTo(second.extension)
            return if (extComparison != 0) extComparison else first.name.compareTo(second.name)
        }
    },
    TYPE_DESCENDING(nameRes = R.string.type_descending) {
        override fun compare(first: File, second: File): Int {
            // Directories should appear after files
            if (first.isDirectory && !second.isDirectory) return 1
            if (!first.isDirectory && second.isDirectory) return -1

            // If both are directories or both are files, compare by extension
            val extComparison = second.extension.compareTo(first.extension)
            return if (extComparison != 0) extComparison else second.name.compareTo(first.name)
        }
    },
    SHUFFLED(nameRes = R.string.shuffled) {
        override fun compare(first: File, second: File): Int {
            val random = Random()
            return random.nextInt(2)
        }
    },
}