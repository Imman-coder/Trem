package com.immanlv.trem.presentation.screens.timetableBuilder

import android.util.Log
import android.view.SubMenu
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.NavigateBefore
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.NavigateBefore
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ContentCut
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.NavigateNext
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.immanlv.trem.domain.model.ClassType
import com.immanlv.trem.domain.model.Event
import com.immanlv.trem.domain.model.Subject
import com.immanlv.trem.domain.util.Constants
import com.immanlv.trem.network.model.EventDto
import com.immanlv.trem.network.model.SubjectDto
import com.immanlv.trem.presentation.LocalRailStatus
import com.immanlv.trem.presentation.screens.timetable.util.intToTime
import com.immanlv.trem.presentation.screens.timetableBuilder.components.AdderCard
import com.immanlv.trem.presentation.screens.timetableBuilder.components.DragDropItem
import com.immanlv.trem.presentation.screens.timetableBuilder.components.DragDropSurface
import com.immanlv.trem.presentation.screens.timetableBuilder.components.EventEditor
import com.immanlv.trem.presentation.screens.timetableBuilder.components.LocalDragTargetInfo
import com.immanlv.trem.presentation.screens.timetableBuilder.components.SubjectCard
import com.immanlv.trem.presentation.screens.timetableBuilder.util.generateColorPalette
import com.immanlv.trem.presentation.screens.timetableBuilder.util.simplifySubjectName
import com.immanlv.trem.presentation.screens.timetableBuilder.util.simplifyTeacherName
import com.touchlane.gridpad.GridPad
import com.touchlane.gridpad.GridPadCells
import java.util.Locale
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

@Composable
fun TimetableBuilderScreen(
    navController: NavController
) {

    var colorTable by remember {
        mutableStateOf(listOf<Color>())
    }

    val railState = LocalRailStatus.current

    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.background

    LaunchedEffect(key1 = Unit) {
        colorTable = generateColorPalette(
            backgroundColor = backgroundColor,
            textColor = textColor,
            opacity = 120
        )
        Log.d("TAG", "TimetableBuilderScreen: Color Table Populated")
    }


    val eventTable = remember {
        mutableStateListOf<List<Int>>(
            listOf(1, 2, 3, 4),
            listOf(1, 2, 3, 4),
            listOf(1, 2, 3, 4),
            listOf(1, 2, 3, 4),
            listOf(1, 2, 3, 4),
            listOf(1, 2, 3, 4),
        )
    }

    var timeList = remember {
        mutableStateListOf<Int>(555, 615, 675, 735, 795, 855, 915, 975)
    }

    val eventList = remember {
        mutableStateListOf<Event>(
            Event(
                time_span = 3,
                subjects = listOf(
                    Subject(
                        "Operating System (OS)",
                        "",
                        "Mousami Acharya"
                    )
                ),
                class_type = ClassType.Lab
            ),
            Event(
                time_span = 1,
                subjects = listOf(
                    Subject(
                        "Database Enginnering",
                        "",
                        "Sikheresh Barik"
                    )
                ),
                class_type = ClassType.Theory
            ),
            Event(
                time_span = 1,
                subjects = listOf(
                    Subject(
                        "Software Engineering",
                        "",
                        "Surodeep Mohanty"
                    )
                ),
                class_type = ClassType.Theory
            ),
            Event(
                time_span = 1,
                subjects = listOf(
                    Subject(
                        "Compiler Design",
                        "",
                        "Neva Tripathy"
                    )
                ),
                class_type = ClassType.Theory
            ),
        )
    }

    var selectedEvent by remember {
        mutableStateOf(Pair(-1, -1))
    }

    var enableCopy = false

    var clipboardEvent by remember { mutableIntStateOf(-1) }

    val density = LocalDensity.current

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

    fun removeTime(index: Int) {
        timeList.apply { removeAt(index) }
    }

    fun addTime(index: Int, time: Int) {
        timeList.apply { add(index, time) }
    }

    fun pasteEvent(to: Pair<Int, Int>) {
        eventTable[to.first] =
            eventTable[to.first].toMutableList().apply { add(to.second, clipboardEvent) }
    }

    fun copyTo(from: Pair<Int, Int>, to: Pair<Int, Int>) {
        val id = eventTable[from.first][from.second]
        try {
            eventTable[to.first] = eventTable[to.first].toMutableList().apply { add(to.second, id) }
        } catch (_: Exception) {
            eventTable[to.first] = eventTable[to.first].toMutableList().apply { add(id) }
        }
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
        val id = eventTable[from.first][from.second]
        eventTable[from.first] =
            eventTable[from.first].toMutableList().apply { removeAt(from.second) }
        try {
            eventTable[to.first] = eventTable[to.first].toMutableList().apply { add(to.second, id) }
        } catch (_: Exception) {
            eventTable[to.first] = eventTable[to.first].toMutableList().apply { add(id) }
        }
    }

    fun delete(from: Pair<Int, Int>) {
        if (from == selectedEvent) {
            selectedEvent = Pair(-1, -1)
        }
        eventTable[from.first] =
            eventTable[from.first].toMutableList().apply { removeAt(from.second) }
    }

    fun addEvent(row: Int) {
        Log.d("TAG", "addEvent: $row")
        val id = eventList.size + 1
        eventList += Event(
            time_span = 1,
            subjects = listOf(Subject("", "", "")),
            class_type = ClassType.Theory
        )
        eventTable[row] = eventTable[row].toMutableList().apply { add(id) }
        Log.d("TAG", "addEvent: id=$id")
    }

    Box(Modifier.fillMaxHeight()) {
        var openOffset by remember {
            mutableStateOf(DpOffset.Zero)
        }

        var showContextMenu by remember {
            mutableStateOf(false)
        }

        var contextMenuBy by remember {
            mutableIntStateOf(0)
        }

        var contextMenuFromId by remember {
            mutableStateOf<Pair<Int, Int>?>(null)
        }

        var itemHeight by remember {
            mutableStateOf(0.dp)
        }

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
                        m += eventList[a - 1].time_span
                    }
                    if (s < m) s = m
                }
                if (s < timeList.size) s = timeList.size
                Log.d("TAG", "TimetableBuilderScreen: s=$s")
                GridPad(
                    cells = GridPadCells(7, s + 1), modifier = Modifier
                ) {
                    eventTable.forEachIndexed { x, v ->
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

                        var ll = 1

                        v.forEachIndexed { y, eventId ->
                            item(
                                row = x + 1,
                                column = ll,
                                columnSpan = eventList[eventId - 1].time_span
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
                            ll += eventList[eventId - 1].time_span
                        }
                        item {
                            AdderCard(
                                row = x,
                                modifier = Modifier
                                    .graphicsLayer(alpha = if (showContextMenu) .7f else 1f),
                                addEvent = { addEvent(it) },
                                moveTo = { from, to -> moveTo(from, to) })
                        }
                    }
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
                                Text(text = intToTime(v))
                            }
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
                    event = eventList[selectedEventId],
                    onValueUpdate = {
                        eventList[selectedEventId] = it
                    }
                )
            }


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
                onClick = {}
            ),
            FileDropdownMenuItem(
                "Open",
                onClick = {}
            ),
            FileDropdownMenuItem(
                "Import",
                subMenuId = DropdownMenuState.Import
            ),
            FileDropdownMenuItem(
                "Export",
                subMenuId = DropdownMenuState.Export
            ),
        )
        val importMenu = listOf(
            FileDropdownMenuItem(
                "",
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.NavigateBefore, "Back") },
                onClick = { state = DropdownMenuState.Main }
            ),
            FileDropdownMenuItem(
                "File",
                onClick = {}
            ),
            FileDropdownMenuItem(
                "Clipboard",
                onClick = {}
            ),
        )
        val exportMenu = listOf(
            FileDropdownMenuItem(
                "",
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.NavigateBefore, "Back") },
                onClick = { state = DropdownMenuState.Main }
            ),
            FileDropdownMenuItem(
                "Json",
                onClick = {}
            ),
            FileDropdownMenuItem(
                "Project",
                onClick = {}
            ),
            FileDropdownMenuItem(
                "Clipboard",
                onClick = {}
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
                    } else it.onClick.invoke()
                })
        }
    }
}


enum class DropdownMenuState {
    Main, Import, Export
}

data class FileDropdownMenuItem(
    val label: String,
    val onClick: (() -> Unit)? = null,
    val leadingIcon: (@Composable () -> Unit)? = null,
    val subMenuId: DropdownMenuState? = null
)

fun <T> Pair<T, T>.equals(i: Pair<T, T>): Boolean {
    return (this.first == i.first && this.second == i.second)
}