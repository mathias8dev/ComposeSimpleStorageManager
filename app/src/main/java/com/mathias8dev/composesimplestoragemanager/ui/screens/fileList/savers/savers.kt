package com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.savers

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import java.io.File


val fileListSaver = object : Saver<MutableState<List<File>>, List<String>> {
    override fun SaverScope.save(value: MutableState<List<File>>): List<String> {
        return value.value.map { it.path }
    }

    override fun restore(value: List<String>): MutableState<List<File>> {
        return mutableStateOf(value.map { File(it) })
    }
}

