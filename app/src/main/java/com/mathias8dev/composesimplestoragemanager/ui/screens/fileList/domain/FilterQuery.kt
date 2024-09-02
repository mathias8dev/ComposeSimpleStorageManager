package com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File


interface FilterQuery : Parcelable {

    fun filter(files: List<File>): List<File>
    fun filter(file: File): Boolean

    @Parcelize
    data class SearchTerm(val term: String) : FilterQuery {
        override fun filter(files: List<File>): List<File> {
            return files.filter { it.name.contains(term, ignoreCase = true) }
        }

        override fun filter(file: File): Boolean {
            return file.name.contains(term, ignoreCase = true)
        }
    }

    @Parcelize
    data class MimeTypes(val mimeTypes: List<String>) : FilterQuery {
        override fun filter(files: List<File>): List<File> {
            return files.filter { file ->
                mimeTypes.any { mimeType ->
                    file.isDirectory || file.mimeData?.mimeType?.contains(mimeType, ignoreCase = true) == true
                }
            }
        }

        override fun filter(file: File): Boolean {
            return mimeTypes.any { mimeType ->
                file.isDirectory || file.mimeData?.mimeType?.contains(mimeType, ignoreCase = true) == true
            }
        }
    }

    @Parcelize
    data object Nothing : FilterQuery {
        override fun filter(files: List<File>): List<File> {
            return files
        }

        override fun filter(file: File): Boolean {
            return true
        }
    }
}


@Parcelize
data class FilterQueries(
    val queries: List<FilterQuery>
) : Parcelable

