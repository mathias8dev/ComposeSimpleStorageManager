package com.mathias8dev.composesimplestoragemanager.queries

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import com.mathias8dev.composesimplestoragemanager.models.MediaInfo


fun queryAllMedias(context: Context): List<MediaInfo> {

    val mediaList = mutableListOf<MediaInfo>()

    val collection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Files.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Files.getContentUri("external")
        }

    val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.SIZE,
        MediaStore.Files.FileColumns.DATA,
        MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
        MediaStore.Files.FileColumns.MIME_TYPE
    )
    val selection = MediaStore.Files.FileColumns.MIME_TYPE + " IS NOT NULL"

    val sortOrder = "${MediaStore.Files.FileColumns.DISPLAY_NAME} ASC"

    val query = context.contentResolver.query(
        collection,
        projection,
        selection,
        null,
        sortOrder
    )

    query?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
        val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
        val bucketNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val name = cursor.getString(nameColumn)
            val size = cursor.getLong(sizeColumn)
            val filePath = cursor.getString(dataColumn)
            val bucketName = cursor.getStringOrNull(bucketNameColumn)

            val contentUri: Uri = ContentUris.withAppendedId(
                collection,
                id
            )

            mediaList += MediaInfo(
                id = id,
                name = name,
                size = size,
                contentUri = contentUri,
                externalContentUri = Uri.parse(filePath),
                bucketName = bucketName,
            )
        }
    }

    return mediaList
}