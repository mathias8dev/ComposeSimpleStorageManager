package com.mathias8dev.composesimplestoragemanager.ui.screens

import android.Manifest
import android.os.Build
import android.os.Environment
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.mathias8dev.composesimplestoragemanager.ui.permissionRequestUis.StoragePermissionRequestUi
import com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.FileListScreen
import com.mathias8dev.permissionhelper.permission.OneShotPermissionsHelper
import com.mathias8dev.permissionhelper.permission.Permission
import com.mathias8dev.permissionhelper.permission.PermissionState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Composable
@Destination
@RootNavGraph
fun HomeScreen(
    navigator: DestinationsNavigator
) {

    var showHomeScreenContent by remember {
        mutableStateOf(false)
    }


    val permissions = remember {
        mutableListOf<Permission>().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Permission(Manifest.permission.READ_MEDIA_AUDIO))
                add(Permission(Manifest.permission.READ_MEDIA_VIDEO))
                add(Permission(Manifest.permission.READ_MEDIA_IMAGES))
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                add(Permission(Manifest.permission.MANAGE_EXTERNAL_STORAGE))
                add(Permission(Manifest.permission.QUERY_ALL_PACKAGES))
            }

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
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

    AnimatedVisibility(
        visible = showHomeScreenContent,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        FileListScreen(
            rootPath = Environment.getExternalStorageDirectory().absolutePath,
            navigator = navigator
        )
    }
}




