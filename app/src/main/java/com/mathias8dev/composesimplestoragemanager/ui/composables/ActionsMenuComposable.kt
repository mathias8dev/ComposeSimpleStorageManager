package com.mathias8dev.composesimplestoragemanager.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.mathias8dev.composesimplestoragemanager.utils.dpToPx
import com.mathias8dev.composesimplestoragemanager.utils.pxToDp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.abs


@Composable
fun <T> ActionsMenuComposable(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onExpandedChange: (updatedExpanded: Boolean) -> Unit,
    onActionClicked: (index: Int, clickedAction: T) -> Unit,
    actions: List<T>,
    actionToString: (item: T) -> String = { it.toString() },
    actionHolder: @Composable (onClick: () -> Unit) -> Unit,
    onDrawActionMenuItem: @Composable (item: T) -> Unit = {
        Text(
            modifier = Modifier
                .wrapContentWidth()
                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 72.dp),
            text = actionToString(it),
        )
    }
) {

    var textFieldSize by remember {
        mutableStateOf(IntSize.Zero)
    }

    var popupHeight by remember {
        mutableIntStateOf(0)
    }

    val screenHeightDp = LocalConfiguration.current.screenHeightDp
    val topPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding().value
    val availableHeight = screenHeightDp - topPadding


    val screenHeightPx = availableHeight.dp.dpToPx()

    var textFieldPositionInWindow: Offset by remember {
        mutableStateOf(Offset(0F, 0F))
    }

    val spacingBetweenPopupAndTextField = 6.dp.dpToPx().toInt()

    val updatedExpanded by rememberUpdatedState(expanded)


    var dismissAlreadyHandled by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()


    val popupMaxHeightPx = 300.dp.dpToPx()

    val popupPositionIsBottom by remember(
        textFieldPositionInWindow,
        screenHeightPx,
        popupMaxHeightPx,
        spacingBetweenPopupAndTextField
    ) {
        derivedStateOf {
            screenHeightPx - textFieldPositionInWindow.y > popupMaxHeightPx + spacingBetweenPopupAndTextField
        }
    }




    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .onGloballyPositioned {
                    textFieldSize = it.size
                    textFieldPositionInWindow = it.positionInWindow()
                }
        ) {

            actionHolder {
                if (!dismissAlreadyHandled) {
                    if (!updatedExpanded) {
                        onExpandedChange(true)
                    }
                }

            }

        }

        if (updatedExpanded) {
            Popup(
                onDismissRequest = {
                    onDismissRequest()
                    dismissAlreadyHandled = true
                    coroutineScope.launch {
                        delay(200)
                        dismissAlreadyHandled = false
                    }
                },
                offset = IntOffset(
                    0,
                    y = if (popupPositionIsBottom) {
                        textFieldSize.height + spacingBetweenPopupAndTextField
                    } else {
                        -popupHeight
                    }
                ),
            ) {
                Timber.d("This value is ${(abs(textFieldPositionInWindow.y - spacingBetweenPopupAndTextField)).pxToDp()}")
                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .heightIn(
                            max = if (popupPositionIsBottom) (screenHeightPx - textFieldPositionInWindow.y - textFieldSize.height - spacingBetweenPopupAndTextField).pxToDp()
                            else abs(textFieldPositionInWindow.y - spacingBetweenPopupAndTextField)
                                .pxToDp()

                        )
                        .onGloballyPositioned {
                            popupHeight = it.size.height
                        }
                ) {

                    LazyColumn(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(vertical = 8.dp)
                    ) {

                        itemsIndexed(items = actions, key = { index, _ -> index }) { index, item ->
                            ActionMenuItem(
                                modifier = Modifier,
                                onClick = { onActionClicked(index, item) }
                            ) {
                                onDrawActionMenuItem(item)
                            }
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun ActionMenuItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {

    Box(
        modifier = Modifier
            .clickable { onClick() }
            .then(modifier)
    ) {
        content()
    }
}