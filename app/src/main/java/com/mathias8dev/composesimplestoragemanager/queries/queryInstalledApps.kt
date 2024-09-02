package com.mathias8dev.composesimplestoragemanager.queries

import android.content.Context
import android.content.pm.ApplicationInfo
import com.mathias8dev.composesimplestoragemanager.models.InstalledApp

fun queryInstalledApps(context: Context): List<InstalledApp> {
    val installedAppList = mutableListOf<InstalledApp>()
    // Get the PackageManager instance
    val packageManager = context.packageManager

    // Get a list of installed packages (apps)
    val installedPackages = packageManager.getInstalledPackages(0)

    for (packageInfo in installedPackages) {
        // Retrieve the app name and package name
        val appName = packageInfo.applicationInfo.loadLabel(packageManager).toString()
        val packageName = packageInfo.packageName
        val sourceDir = packageInfo.applicationInfo.sourceDir // APK file path

        // Determine if it's a system app or user-installed app
        val isSystemApp = (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        val appIcon = packageManager.getApplicationIcon(packageName)

        // Log or process the app details
        installedAppList += InstalledApp(
            name = appName,
            packageName = packageName,
            sourceDir = sourceDir,
            isSystemApp = isSystemApp,
            appIcon = appIcon
        )
    }

    return installedAppList
}