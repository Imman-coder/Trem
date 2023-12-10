package com.immanlv.trem.presentation.screens.timetableBuilder

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.NavigateBefore
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.rounded.CheckBox
import androidx.compose.material.icons.rounded.CheckBoxOutlineBlank
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ContentCut
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.immanlv.trem.domain.util.Constants
import com.immanlv.trem.presentation.ItemState
import com.immanlv.trem.presentation.LocalRailStatus
import com.immanlv.trem.presentation.screens.timetable.util.intToTime
import com.immanlv.trem.presentation.screens.timetableBuilder.components.AdderCard
import com.immanlv.trem.presentation.screens.timetableBuilder.components.CustomTimePicker
import com.immanlv.trem.presentation.screens.timetableBuilder.components.DragDropItem
import com.immanlv.trem.presentation.screens.timetableBuilder.components.DragDropSurface
import com.immanlv.trem.presentation.screens.timetableBuilder.components.EventEditor
import com.immanlv.trem.presentation.screens.timetableBuilder.components.LocalDragTargetInfo
import com.immanlv.trem.presentation.screens.timetableBuilder.components.SubjectCard
import com.immanlv.trem.presentation.screens.timetableBuilder.util.FilePicker
import com.immanlv.trem.presentation.screens.timetableBuilder.util.saveFileToDownloads
import com.touchlane.gridpad.GridPad
import com.touchlane.gridpad.GridPadCells

@Composable
fun TimetableBuilderScreen(
    navController: NavController,
    viewModel: TimetableBuilderViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val railState = LocalRailStatus.current
    val density = LocalDensity.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    val colorTable = viewModel.colorTable.value
    val timetable = viewModel.timetable.value

    val eventTable = timetable.eventTable
    val timeList = timetable.timeList
    val eventList = timetable.eventList

    var filePickerData by remember { mutableStateOf<FilePickerData?>(null) }
    var pickerData by remember { mutableStateOf<PickerData?>(null) }
    var selectedEvent by remember { mutableStateOf(Pair(-1, -1)) }
    var clipboardEvent by remember { mutableIntStateOf(-1) }

    var enableCopy = false

    LaunchedEffect(key1 = viewModel.undoRedoStack.value) {
        railState.undoMenu = ItemState(
            viewModel.undoRedoStack.value.first > 0
        ) {
            viewModel.undo()
        }
        railState.redoMenu = ItemState(
            viewModel.undoRedoStack.value.second > 0
        ) {
            viewModel.redo()
        }
    }

    fun getNewEventId(): Int {
        val k = mutableMapOf<Int, Boolean>()
        for (e in eventTable) {
            for (j in e) {
                k[j] = true
            }
        }
        for (i in 1..50) {
            if (k[i] == false) return i
        }
        return 50
    }

    //
    fun addTime(time: Int) {
        viewModel.onEvent(TimetableBuilderEvent.AddTime(time))
    }

    fun removeTime(index: Int) {
        viewModel.onEvent(TimetableBuilderEvent.RemoveTime(index))
    }

    fun setTime(index: Int, time: Int) {
        viewModel.onEvent(TimetableBuilderEvent.EditTime(index, time))
    }

    //
    fun addEvent(row: Int) {
        viewModel.onEvent(TimetableBuilderEvent.AddBlankEvent(row))
    }

    fun delete(from: Pair<Int, Int>) {
        if (from == selectedEvent) {
            selectedEvent = Pair(-1, -1)
        }
        viewModel.onEvent(TimetableBuilderEvent.DeleteEvent(from))
    }

    fun pasteEvent(to: Pair<Int, Int>) {
        viewModel.onEvent(TimetableBuilderEvent.PasteEventId(clipboardEvent, to))
    }

    fun copyTo(from: Pair<Int, Int>, to: Pair<Int, Int>) {
        viewModel.onEvent(TimetableBuilderEvent.CopyEvent(from, to))
    }

    fun moveTo(from: Pair<Int, Int>, to: Pair<Int, Int>) {
        if (enableCopy) {
            enableCopy = false
            copyTo(from, to)
            return
        }
        if (selectedEvent == from) {
            selectedEvent = to
        }
        viewModel.onEvent(TimetableBuilderEvent.MoveEvent(from, to))
    }


    Box(Modifier.fillMaxHeight()) {
        var openOffset by remember { mutableStateOf(DpOffset.Zero) }
        var showContextMenu by remember { mutableStateOf(false) }
        var contextMenuBy by remember { mutableIntStateOf(0) }
        var contextMenuFromId by remember { mutableStateOf<Pair<Int, Int>?>(null) }
        var itemHeight by remember { mutableStateOf(0.dp) }

        Column {
            DragDropSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.5f)
            ) {
                var s = 6
                for (e in eventTable) {
                    var m = 2
                    for (a in e) {
                        m += eventList[a - 1].timeSpan
                    }
                    if (s < m) s = m
                }
                if (s < timeList.size + 1) s = timeList.size + 1
                GridPad(
                    cells = GridPadCells(7, s + 1), modifier = Modifier
                ) {
                    eventTable.forEachIndexed { x, v ->

                        var ll = 1

                        v.forEachIndexed { y, eventId ->
                            item(
                                row = x + 1,
                                column = ll,
                                columnSpan = eventList[eventId - 1].timeSpan
                            ) {
                                SubjectCard(
                                    x = x,
                                    y = y,
                                    selected = (selectedEvent.first == x && selectedEvent.second == y),
                                    event = eventList[eventId - 1],
                                    onClick = { selectedEvent = Pair(x, y) },
                                    onLongClick = {
                                        showContextMenu = true
                                        contextMenuBy = 1
                                        contextMenuFromId = Pair(x, y)
                                        openOffset = it
                                    },
                                    onDrop = { from, to ->
                                        moveTo(from, to)
                                    },
                                    modifier = Modifier
                                        .graphicsLayer(alpha = if (showContextMenu && contextMenuFromId?.let { it.first == x && it.second == y } != true) .7f else 1f),
                                    backgroundColor = if (colorTable.size > eventId) colorTable[eventId] else Color.Unspecified
                                )
                            }
                            ll += eventList[eventId - 1].timeSpan
                        }
                        item(row = x + 1, column = ll) {
                            AdderCard(
                                row = x,
                                modifier = Modifier
                                    .graphicsLayer(alpha = if (showContextMenu) .7f else 1f),
                                addEvent = { addEvent(it) },
                                moveTo = { from, to -> moveTo(from, to) })
                        }
                    }
                    for (x in 0..6) {
                        item(
                            row = x + 1,
                            column = 0
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = Constants.WeekList[x])
                            }
                        }
                    }
                    var ll = 1
                    timeList.forEachIndexed { y, v ->
                        item(
                            row = 0,
                            column = y + 1
                        ) {
                            var currentPosition by remember { mutableStateOf(Offset.Zero) }
                            Column(
                                modifier = Modifier
                                    .graphicsLayer(alpha = if (showContextMenu && (contextMenuFromId?.let { it.first == -1 && it.second == y } != true)) .5f else 1f)
                                    .onGloballyPositioned {
                                        currentPosition = it.localToWindow(Offset.Zero)
                                    }
                                    .fillMaxSize()
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onLongPress = {
                                                pickerData = TimePickerData(
                                                    data = v,
                                                    onSet = {
                                                        setTime(y, it as Int)
                                                        pickerData = null
                                                    },
                                                    onDismiss = {
                                                        pickerData = null
                                                    }
                                                )

                                            },
                                            onDoubleTap = {
                                                showContextMenu = true
                                                contextMenuBy = 0
                                                openOffset = DpOffset(
                                                    currentPosition.x.toDp(),
                                                    currentPosition.y.toDp()
                                                )
                                                contextMenuFromId = Pair(-1, y)
                                            }
                                        )
                                    },
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = intToTime(v),
                                    textDecoration = if (selectedEvent == Pair(
                                            -1,
                                            y
                                        )
                                    ) TextDecoration.Underline else TextDecoration.None
                                )
                            }
                        }
                        ll += 1
                    }
                    item(
                        row = 0,
                        column = ll
                    ) {
                        Column(
                            modifier = Modifier
                                .graphicsLayer(alpha = if (showContextMenu && (contextMenuFromId?.let { it.first == -1 } != true)) .5f else 1f)
                                .fillMaxSize()
                                .clickable {
                                    pickerData = TimePickerData(
                                        data = 0,
                                        onDismiss = {
                                            pickerData = null
                                        },
                                        onSet = {
                                            pickerData = null
                                            addTime(it as Int)
                                        }
                                    )
                                },

                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "Add Time")
                        }
                    }
                }

                Column(
                    Modifier
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    val cState = LocalDragTargetInfo.current
                    railState.hideRail = cState.isDragging

                    DragDropItem(
                        modifier = Modifier
                            .width(80.dp)
                            .offset(x = (-80).dp),
                        enableDrag = false
                    ) { hovered, data ->
                        if (hovered && data != null) {
                            delete(from = data as Pair<Int, Int>)
                        }

                        NavigationRailItem(
                            selected = hovered,
                            onClick = { /*TODO*/ },
                            icon = { Icon(Icons.Rounded.Delete, "Delete") },
                            label = { Text("Delete") })

                    }

                    DragDropItem(
                        modifier = Modifier
                            .width(80.dp)
                            .offset(x = (-80).dp),
                        enableDrag = false
                    ) { hovered, data ->
                        if (hovered && data != null) {
                            data as Pair<Int, Int>
                            clipboardEvent = eventTable[data.first][data.second]

                            delete(data)
                        }

                        NavigationRailItem(
                            selected = hovered,
                            onClick = { /*TODO*/ },
                            icon = { Icon(Icons.Rounded.ContentCut, "Cut") },
                            label = { Text("Cut") })

                    }

                    DragDropItem(
                        modifier = Modifier
                            .width(80.dp)
                            .offset(x = (-80).dp),
                        enableDrag = false
                    ) { hovered, data ->
                        if (hovered && data != null) {
                            data as Pair<Int, Int>
                            clipboardEvent = eventTable[data.first][data.second]
                        }
                        NavigationRailItem(
                            selected = hovered,
                            onClick = { /*TODO*/ },
                            icon = { Icon(Icons.Rounded.ContentCopy, "Copy") },
                            label = { Text("Copy") })
                    }

                    DragDropItem(
                        modifier = Modifier
                            .width(80.dp)
                            .offset(x = (-80).dp),
                        enableDrag = false
                    ) { hovered, _ ->
                        if (hovered) {
                            LaunchedEffect(key1 = Unit, block = { enableCopy = !enableCopy })
                        }

                        NavigationRailItem(
                            selected = enableCopy,
                            onClick = { /*TODO*/ },
                            icon = { Icon(Icons.Rounded.CopyAll, "Duplicate") },
                            label = { Text("Duplicate") })
                    }
                }
            }
            var selectedEventId = -1
            try {
                selectedEventId = eventTable[selectedEvent.first][selectedEvent.second] - 1
            } catch (_: Exception) {
                selectedEvent = Pair(-1, -1)
            }
            if (selectedEventId > -1) {

                EventEditor(
                    event = eventList[selectedEventId]
                ) {
                    viewModel.onEvent(TimetableBuilderEvent.EditEvent(selectedEventId, it))
                }
            }

        }

        pickerData?.let {
            CustomTimePicker(
                it
            )
        }

        DropdownMenu(
            expanded = showContextMenu,
            onDismissRequest = {
                showContextMenu = false
            },
            modifier = Modifier
                .graphicsLayer(
                    alpha = if (showContextMenu) 1f else 0f
                )
                .onSizeChanged {
                    itemHeight = with(density) { it.height.toDp() }
                },
            offset = openOffset.copy(y = openOffset.y + itemHeight)
        ) {
            if (contextMenuBy == 1) {
                arrayOf("Copy", "Cut", "Paste Before", "Paste After", "Delete").forEach { v ->
                    DropdownMenuItem(
                        text = { Text(text = v) },
                        enabled = (!(v in listOf(
                            "Paste Before",
                            "Paste After"
                        ) && clipboardEvent == -1)),
                        onClick = {
                            showContextMenu = false
                            when (v) {
                                "Delete" -> {
                                    contextMenuFromId?.let { delete(it) }
                                }

                                "Cut" -> {
                                    contextMenuFromId?.let {
                                        clipboardEvent = eventTable[it.first][it.second]
                                        delete(it)
                                    }
                                }

                                "Copy" -> {
                                    contextMenuFromId?.let {
                                        clipboardEvent = eventTable[it.first][it.second]
                                    }
                                }

                                "Paste Before" -> {
                                    contextMenuFromId?.let {
                                        pasteEvent(it)
                                    }
                                }

                                "Paste After" -> {
                                    contextMenuFromId?.let {
                                        pasteEvent(it.copy(second = it.second + 1))
                                    }
                                }
                            }
                            contextMenuFromId = Pair(-1, -1)
                        }
                    )
                }
            } else {
                val a = listOf("Add Time", "Delete")
                a.forEachIndexed { id, text ->
                    DropdownMenuItem(text = { Text(text = text) }, onClick = {
                        when (id) {
                            0 -> {}
                            1 -> {
                                contextMenuFromId?.let { removeTime(it.second) }
                            }
                        }
                        contextMenuFromId = Pair(-1, -1)
                        showContextMenu = false
                    })
                }
            }
        }
    }
    var state by remember { mutableStateOf(DropdownMenuState.Main) }
    if (!railState.showMenu) state = DropdownMenuState.Main
    fun provideFileMenu(): Map<DropdownMenuState, List<FileDropdownMenuItem>> {
        val mainMenu = listOf(
            FileDropdownMenuItem(
                "New",
                onClick = {
                    viewModel.onEvent(TimetableBuilderEvent.NewTable)
                }
            ),
            FileDropdownMenuItem(
                "Import",
                isTerminal = false,
                subMenuId = DropdownMenuState.Import
            ),
            FileDropdownMenuItem(
                "Export",
                isTerminal = false,
                subMenuId = DropdownMenuState.Export
            ),
            FileDropdownMenuItem(
                "Inject Table",
                onClick = {
                    viewModel.onEvent(TimetableBuilderEvent.ToggleTimetableInject)
                },
                leadingIcon = {
                    Icon(
                        if (viewModel.isTableInjected.value) Icons.Rounded.CheckBox else Icons.Rounded.CheckBoxOutlineBlank,
                        contentDescription = "Timetable Injected"
                    )
                }
            ),
            FileDropdownMenuItem(
                "Refresh Color Palette",
                onClick = {
                    viewModel.onEvent(TimetableBuilderEvent.RefreshColorTable)
                }
            ),
        )
        val importMenu = listOf(
            FileDropdownMenuItem(
                "",
                isTerminal = false,
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.NavigateBefore, "Back") },
                onClick = { state = DropdownMenuState.Main }
            ),
            FileDropdownMenuItem(
                "File",
                onClick = {
                    filePickerData = FilePickerData(
                        arrayOf("application/json"),
                        openPicker = true
                    ) {
                        try {
                            viewModel.onEvent(TimetableBuilderEvent.LoadJson(it!!.toString(Charsets.UTF_8)))
                        } catch (e: Exception) {
                            Toast.makeText(context, "Failed to load file", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            ),
            FileDropdownMenuItem(
                "Clipboard",
                onClick = {
                    try {
                        viewModel.onEvent(TimetableBuilderEvent.LoadJson(clipboardManager.getText()!!.text))
                    } catch (e: Exception) {
                        Log.e("TAG", "provideFileMenu: $e")
                        Toast.makeText(context, "Failed to load", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            ),
        )
        val exportMenu = listOf(
            FileDropdownMenuItem(
                "",
                isTerminal = false,
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.NavigateBefore, "Back") },
                onClick = { state = DropdownMenuState.Main }
            ),
            FileDropdownMenuItem(
                "Json",
                onClick = {
                    pickerData = EntryData(
                        title = "Enter file name",
                        onSet = {
                            val fn =
                                saveFileToDownloads("$it.json", viewModel.getTimetableAsBytes())
                            Toast.makeText(context, "Exported to $fn", Toast.LENGTH_SHORT).show()
                            pickerData = null
                        },
                        onDismiss = {
                            pickerData = null
                        }
                    )

                }
            ),
            FileDropdownMenuItem(
                "Project",
                onClick = {

                }
            ),
            FileDropdownMenuItem(
                "Clipboard",
                onClick = {
                    clipboardManager.setText(
                        AnnotatedString(
                            viewModel.getTimetableAsBytes().toString(Charsets.UTF_8)
                        )
                    )
                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                }
            ),
        )
        return mapOf(
            DropdownMenuState.Main to mainMenu,
            DropdownMenuState.Import to importMenu,
            DropdownMenuState.Export to exportMenu
        )
    }

    val menus = provideFileMenu()
    DropdownMenu(
        expanded = railState.showMenu,
        offset = DpOffset(0.dp, 0.dp),
        onDismissRequest = {
            railState.showMenu = false
        },
    ) {
        menus[state]?.forEach {
            DropdownMenuItem(
                text = { Text(it.label) },
                leadingIcon = it.leadingIcon,
                modifier = Modifier.animateContentSize(),
                trailingIcon = {
                    if (it.subMenuId != null) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.NavigateNext,
                            contentDescription = "Next"
                        )
                    }
                },
                onClick = {
                    if (it.onClick == null) {
                        state = it.subMenuId!!
                    } else {
                        it.onClick.invoke()
                    }
                    if (it.isTerminal)
                        railState.showMenu = false
                })
        }
    }
    if (filePickerData != null)
        FilePicker(filePickerData!!.mimeType, filePickerData!!.openPicker) {
            filePickerData!!.onSet(it)
            filePickerData = null
        }

}


interface PickerData {
    val onDismiss: () -> Unit
    val onSet: (Any) -> Unit
}

data class TimePickerData(
    val data: Int,
    override val onDismiss: () -> Unit = {},
    override val onSet: (Any) -> Unit = {},
) : PickerData

data class EntryData(
    val title: String = "",
    val data: String = "",
    override val onDismiss: () -> Unit = {},
    override val onSet: (Any) -> Unit = {},
) : PickerData

data class FilePickerData(
    val mimeType: Array<String> = arrayOf(),
    val openPicker: Boolean = false,
    val onSet: (ByteArray?) -> Unit = {},
)

enum class DropdownMenuState {
    Main, Import, Export
}

data class FileDropdownMenuItem(
    val label: String,
    val onClick: (() -> Unit)? = null,
    val isTerminal: Boolean = true,
    val leadingIcon: (@Composable () -> Unit)? = null,
    val subMenuId: DropdownMenuState? = null
)

fun <T> Pair<T, T>.equals(i: Pair<T, T>): Boolean {
    return (this.first == i.first && this.second == i.second)
}