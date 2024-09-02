package com.mathias8dev.composesimplestoragemanager.queries

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import com.mathias8dev.composesimplestoragemanager.models.Archive


fun queryAllArchives(context: Context): List<Archive> {

    // URI for querying any type of file
    val archiveList = mutableListOf<Archive>()

    val collection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Files.getContentUri("external")
        }


    // MIME types for different archive formats
    val mimeTypes = arrayOf(
        "application/zip",
        "application/x-rar-compressed",
        "application/x-tar",
        "application/x-7z-compressed",
        "application/gzip"
    )


    // Selection criteria to match any of the MIME types
    var selection = MediaStore.Files.FileColumns.MIME_TYPE + "=?"
    for (i in 1 until mimeTypes.size) {
        selection += " OR " + MediaStore.Files.FileColumns.MIME_TYPE + "=?"
    }


    // Convert MIME types array to selectionArgs
    val selectionArgs = mimeTypes
    val sortOrder = "${MediaStore.Files.FileColumns.DISPLAY_NAME} ASC"

    // Projection to specify the columns we are interested in
    val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.SIZE,
        MediaStore.Files.FileColumns.DATA,
        MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME
    )


    // Perform the query
    val cursor: Cursor? = context.contentResolver.query(
        collection,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )



    if (cursor != null) {
        while (cursor.moveToNext()) {
            // Get details of each file
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME))
            val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE))
            val filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
            val bucketName = cursor.getStringOrNull(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME))

            val contentUri: Uri = ContentUris.withAppendedId(
                collection,
                id
            )

            // Process the archive file details
            archiveList += Archive(
                id = id,
                name = name,
                size = size,
                contentUri = contentUri,
                externalContentUri = Uri.parse(filePath),
                bucketName = bucketName,
            )
        }
        cursor.close()
    }

    return archiveList
}