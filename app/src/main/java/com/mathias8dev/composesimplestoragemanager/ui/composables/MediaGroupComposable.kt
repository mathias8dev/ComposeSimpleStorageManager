package com.mathias8dev.composesimplestoragemanager.ui.composables

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.parcelize.Parcelize


@Parcelize
enum class MediaGroup(
    val title: String,
    @DrawableRes val iconRes: Int? = null,
    val path: String,
) : Parcelable {

    Home(
        title = "Home",
        path = "/data/user/0/cssm/home"
    ),

    InternalStorage(
        title = "Internal storage",
        path = "/storage/emulated/0"
    ),

    Root(
        title = "Root",
        path = "/"
    ),

    Audio(
        title = "Audio",
        path = "content://cssm/audio"
    ),

    Video(
        title = "Video",
        path = "content://cssm/video"
    ),

    Image(
        title = "Image",
        path = "content://cssm/image"
    ),

    Apk(
        title = "Apk",
        path = "content://cssm/apk"
    ),

    Archive(
        title = "Archive",
        path = "content://cssm/archive"
    ),

    Document(
        title = "Document",
        path = "content://cssm/document"
    ),

    App(
        title = "App",
        path = "content://cssm/app"
    ),

    RecentFiles(
        title = "Recent files",
        path = "content://cssm/recent"
    ),


    AllFiles(
        title = "All Files",
        path = "content://cssm/all"
    ),

    RecycleBin(
        title = "Recycle bin",
        path = "content://cssm/trash"
    ),

}


@Composable
fun MediaGroupComposable(
    icon: @Composable (ColumnScope.() -> Unit)? = null,
    title: String,
    subTitle: @Composable (ColumnScope.() -> Unit)? = null,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Card {
                it()
            }
        }

        Column {
            Text(
                text = title,
                lineHeight = 18.sp
            )
            subTitle?.invoke(this)
        }
    }
}


@Composable
fun MediaGroupComposable(
    @DrawableRes iconRes: Int? = null,
    title: String,
    subTitle: String,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card {
            iconRes?.let {
                Icon(
                    modifier = Modifier
                        .padding(5.dp)
                        .size(32.dp),
                    painter = painterResource(it),
                    contentDescription = null
                )
            }
        }

        Column {
            Text(
                text = title,
                lineHeight = 18.sp
            )
            Text(
                text = subTitle,
                fontSize = 12.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )
        }
    }
}
