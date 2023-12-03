package com.immanlv.trem.presentation.screens.timetableBuilder.components

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import com.immanlv.trem.presentation.screens.login.util.noRippleClickable


internal val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }

@Composable
fun DragDropItem(
    modifier: Modifier = Modifier,
    enableDrag: Boolean = true,
    dataToDrop: Any = 1,
    onClick: () -> Unit = {},
    onLongClick: (offset: DpOffset) -> Unit = {},
    dragPreview: @Composable (() -> Unit)? = null,
    content: @Composable (isHovered: Boolean, data: Any?) -> Unit
) {

    val currentState = LocalDragTargetInfo.current

    var currentBound by remember { mutableStateOf<Rect?>(null) }

    var currentPosition by remember { mutableStateOf(Offset.Zero) }

    var isCurrentDropTarget by remember { mutableStateOf(false) }


    Box(modifier = modifier
        .onGloballyPositioned {
            currentPosition = it.localToWindow(Offset.Zero)
            it
                .boundsInWindow()
                .let { rect ->
                    currentBound = rect
                }
        }
        .pointerInput(Unit) {
            if (enableDrag)
                detectDragGesturesAfterLongPress(onDragStart = { dragAmount ->
                    // Start Dragging
                    currentState.dataToDrop = dataToDrop
                    currentState.isDragging = true
                    currentState.dragPosition = currentPosition + dragAmount
                    currentState.dragComposable = { dragPreview ?: content(false, null) }
                }, onDrag = { change, dragAmount ->
                    change.consume()
                    currentState.dragOffset += Offset(dragAmount.x, dragAmount.y)

                }, onDragEnd = {
                    // Stop Dragging
                    currentState.dragOffset = Offset.Zero
                    currentState.isDragging = false
                }, onDragCancel = {
                    // Stop Dragging
                    currentState.dragOffset = Offset.Zero
                    currentState.isDragging = false
                })

        }
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = {
                    if (!currentState.isDragging)
                        onLongClick(
                            DpOffset(
                                currentPosition.x.toDp(),
                                currentPosition.y.toDp()
                            )
                        )
                },
                onTap = { onClick() }
            )
        }
    ) {
        val data =
            if (isCurrentDropTarget && !currentState.isDragging) currentState.dataToDrop else null

        content(isCurrentDropTarget, data)

        isCurrentDropTarget =
            if (currentState.isDragging) (currentBound?.contains(currentState.dragOffset + currentState.dragPosition) == true)
            else false
    }
}

@Composable
fun DragDropSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val state = remember {
        DragTargetInfo()
    }
    CompositionLocalProvider(LocalDragTargetInfo provides state) {
        Box(modifier = modifier) {
            content()
            if (state.isDragging) {
                var targetSize by remember {
                    mutableStateOf(IntSize.Zero)
                }
                Box(modifier = Modifier
                    .graphicsLayer {
                        val offset = (state.dragOffset + state.dragPosition)
                        scaleX = 1.3f
                        scaleY = 1.3f
                        alpha = if (targetSize == IntSize.Zero) 0f else .9f
                        translationX = offset.x.minus(targetSize.width / 2)
                        translationY = offset.y.minus(targetSize.height / 2)
                    }
                    .onGloballyPositioned {
                        targetSize = it.size
                    }) {
                    state.dragComposable?.invoke()
                }
            }
        }
    }
}

internal class DragTargetInfo {
    var isDragging: Boolean by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var dragComposable by mutableStateOf<(@Composable () -> Unit)?>(null)
    var dataToDrop by mutableStateOf<Any?>(null)
}