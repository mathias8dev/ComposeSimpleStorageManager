package com.mathias8dev.composesimplestoragemanager.ui.screens.fileList.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mathias8dev.composesimplestoragemanager.R


@Composable
fun BucketNameComposable(
    modifier: Modifier = Modifier,
    bucketName: String,
    contentSize: Long,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .fillMaxWidth()
            .then(modifier),
    ) {
        Card {
            Image(
                painter = painterResource(R.drawable.ic_folder),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .width(0.dp)
                    .weight(1F),
                text = bucketName,
            )



            Text(
                modifier = Modifier,
                text = "($contentSize)",
                fontSize = 14.sp
            )
        }
    }
}