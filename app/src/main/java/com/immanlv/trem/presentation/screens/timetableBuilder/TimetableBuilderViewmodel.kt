package com.immanlv.trem.presentation.screens.timetableBuilder

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.immanlv.trem.domain.model.AppPreference
import com.immanlv.trem.domain.model.ClassType
import com.immanlv.trem.domain.model.Event
import com.immanlv.trem.domain.model.Subject
import com.immanlv.trem.domain.model.Timetable
import com.immanlv.trem.domain.use_case.AppPreferencesUseCases
import com.immanlv.trem.network.model.TimetableDto
import com.immanlv.trem.network.model.mapper.TimetableMapper
import com.immanlv.trem.presentation.screens.timetableBuilder.TimetableBuilderViewModel.RecordStateType.*
import com.immanlv.trem.presentation.screens.timetableBuilder.util.generateColorPalette
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.ArrayDeque
import javax.inject.Inject

@HiltViewModel
class TimetableBuilderViewModel
@Inject constructor(
    private val appPreferencesUseCases: AppPreferencesUseCases
) : ViewModel() {

    private var _colorTable = mutableStateOf(listOf<Color>())
    val colorTable: State<List<Color>> = _colorTable

    private var _appPreference = mutableStateOf(AppPreference())

    private var _timetable = mutableStateOf(Timetable())
    val timetable: State<Timetable> = _timetable

    private var _undoRedoStack = mutableStateOf(Pair(0, 0))
    val undoRedoStack: State<Pair<Int, Int>> = _undoRedoStack

    private var _isTableInjected = mutableStateOf(false)
    val isTableInjected: State<Boolean> = _isTableInjected

    val isSafeToExit: Boolean
        get() = calculateSafeToExit()


    init {
        populateColorPalette()
        getAppPreference()
    }


    fun onEvent(event: TimetableBuilderEvent) {
        when (event) {
            is TimetableBuilderEvent.AddTime -> addTimeStamp(event.time)

            is TimetableBuilderEvent.AddBlankEvent -> addNewEvent(event.row)

            is TimetableBuilderEvent.CopyEvent -> copyEvent(event.from, event.to)

            is TimetableBuilderEvent.DeleteEvent -> deleteEvent(event.from)

            is TimetableBuilderEvent.EditEvent -> editEvent(event.id, event.data)

            is TimetableBuilderEvent.PasteEventId -> pasteEventId(event.id, event.to)

            is TimetableBuilderEvent.RemoveTime -> removeTimeStamp(event.index)

            is TimetableBuilderEvent.EditTime -> editTimeStamp(event.index, event.time)

            is TimetableBuilderEvent.LoadJson -> loadStringJson(event.data)

            is TimetableBuilderEvent.MoveEvent -> moveEvent(event.from, event.to)

            TimetableBuilderEvent.NewTable -> newTable()

            TimetableBuilderEvent.RefreshColorTable -> populateColorPalette()

            TimetableBuilderEvent.ToggleTimetableInject -> toggleTimetableInjection()

            TimetableBuilderEvent.InjectTimetable -> injectTimetable()

            TimetableBuilderEvent.Redo -> redo()

            TimetableBuilderEvent.Undo -> undo()
        }
    }


    /*-------------------Event Time functions-------------------*/
    private fun addTimeStamp(time: Int) {
        // Record Timestamp State
        recordChangeState(TimeList)

        // Adding new timestamp
        _timetable.value = _timetable.value.copy(
            timeList = _timetable.value.timeList.toMutableList()
                .apply { add(time) }.sorted()

        )

    }

    private fun removeTimeStamp(index: Int) {

        // Record Timestamp state
        recordChangeState(TimeList)

        // Removing timestamp
        _timetable.value = _timetable.value.copy(
            timeList = _timetable.value.timeList.toMutableList()
                .apply { removeAt(index) })

    }

    private fun editTimeStamp(index: Int, time: Int) {

        // Record Timestamp state
        recordChangeState(TimeList)

        // Removing and Adding timestamp
        _timetable.value = _timetable.value.copy(
            timeList = _timetable.value.timeList.toMutableList()
                .apply { removeAt(index) }.apply { add(index, time) }
                .sorted()
        )
    }


    /*-------------------Event Table functions-------------------*/
    private fun deleteEvent(from: Pair<Int, Int>) {

        // Record Event Table State
        recordChangeState(EventTable)

        // 
        val dayList = _timetable.value.eventTable[from.first].toMutableList()
            .apply { removeAt(from.second) }
        val editedTable =
            _timetable.value.eventTable.toMutableList()
                .apply { removeAt(from.first) }
                .apply { add(from.first, dayList) }

        // Applying changes
        _timetable.value = _timetable.value.copy(
            eventTable = editedTable
        )

    }

    private fun editEvent(id: Int, data: Event) {

        // Record Separately for this event
        // recordChangeState(EventList)

        val e = _timetable.value.eventList.toMutableList().apply { removeAt(id) }
            .apply { add(id, data) }

        // Applying changes
        _timetable.value = _timetable.value.copy(
            eventList = e
        )

    }

    private fun pasteEventId(id: Int, to: Pair<Int, Int>) {

        // Record Event Table State
        recordChangeState(EventTable)

        pasteEventIdWithoutStateRecord(id, to)
    }

    private fun copyEvent(from: Pair<Int, Int>, to: Pair<Int, Int>) {

        // Record Event Table State
        recordChangeState(EventTable)

        //
        val id = _timetable.value.eventTable[from.first][from.second]
        pasteEventIdWithoutStateRecord(id, to)
    }

    private fun pasteEventIdWithoutStateRecord(id: Int, to: Pair<Int, Int>) {

        val dayList: List<Int> = try {
            _timetable.value.eventTable[to.first].toMutableList()
                .apply { add(to.second, id) }
        } catch (_: Exception) {
            _timetable.value.eventTable[to.first].toMutableList()
                .apply { add(id) }
        }
        val editedTable =
            _timetable.value.eventTable.toMutableList()
                .apply { removeAt(to.first) }
                .apply { add(to.first, dayList) }

        // Applying changes
        _timetable.value = _timetable.value.copy(
            eventTable = editedTable
        )

    }

    private fun moveEvent(from: Pair<Int, Int>, to: Pair<Int, Int>) {

        // Record Event Table State
        recordChangeState(EventTable)

        //
        val id = _timetable.value.eventTable[from.first][from.second]
        pasteEventIdWithoutStateRecord(id, to)

        val dayList = _timetable.value.eventTable[from.first].toMutableList()
            .apply { removeAt(from.second) }

        val editedTable =
            _timetable.value.eventTable.toMutableList()
                .apply { removeAt(from.first) }
                .apply { add(from.first, dayList) }

        // Applying changes
        _timetable.value = _timetable.value.copy(
            eventTable = editedTable
        )

    }


    /*-------------------Event List functions-------------------*/
    private fun addNewEvent(row: Int) {
        // Record Event Table and Event List
        recordChangeState(Minitial)

        //
        val newId = _timetable.value.eventList.size + 1
        val eventListWithNewBlankEvent = _timetable.value.eventList.toMutableList().apply {
            add(
                Event(
                    timeSpan = 1,
                    subjects = listOf(Subject("", "", "")),
                    classType = ClassType.Theory
                )
            )
        }

        //
        val newEventAddedDayList =
            _timetable.value.eventTable[row].toMutableList().apply { add(newId) }
        val editedTable =
            _timetable.value.eventTable.toMutableList().apply { removeAt(row) }
                .apply { add(row, newEventAddedDayList) }

        // Applying changes
        _timetable.value = _timetable.value.copy(
            eventTable = editedTable,
            eventList = eventListWithNewBlankEvent
        )

    }


    /*-------------------Timetable functions-------------------*/
    private fun loadStringJson(stringJson: String) {
        val parsedTable = TimetableMapper.mapToDomainModel(
            Gson().fromJson(
                stringJson,
                TimetableDto::class.java
            )
        )
        _timetable.value = parsedTable
    }

    private fun newTable() {
        _timetable.value = Timetable()
        redoStack.clear()
        undoStack.clear()
    }


    /*-------------------Misc functions-------------------*/
    private fun populateColorPalette() {
        _colorTable.value = generateColorPalette(
            Color.Unspecified,
            Color.Unspecified,
            opacity = 120
        )
    }

    private fun toggleTimetableInjection() {
        _isTableInjected.value = !_isTableInjected.value
        viewModelScope.launch {
            appPreferencesUseCases.setAppPreference(
                _appPreference.value.copy(
                    loadBuilderTimetable = if (!_isTableInjected.value) "" else Gson().toJson(
                        _timetable.value
                    )
                )
            )
        }
    }

    fun getTimetableAsBytes(): ByteArray {
        val timetableAsNetworkParsedJson =
            Gson().toJson(TimetableMapper.mapFromDomainModel(_timetable.value))
        return timetableAsNetworkParsedJson.toByteArray(Charsets.UTF_8)
    }

    private fun getAppPreference() {
        viewModelScope.launch {
            appPreferencesUseCases.getAppPreference().onEach {
                _appPreference.value = it
            }.launchIn(viewModelScope)
        }
    }

    private fun injectTimetable() {
        viewModelScope.launch {
            appPreferencesUseCases.setAppPreference(
                _appPreference.value.copy(
                    loadBuilderTimetable =
                    if (_isTableInjected.value)
                        Gson().toJson(_timetable.value)
                    else
                        ""
                )
            )
        }
    }

    private fun calculateSafeToExit():Boolean{
        return _appPreference.value.loadBuilderTimetable == Gson().toJson(_timetable.value)
    }


    /**
     ** Redo undo Implementation
     **/
    private val undoStack = ArrayDeque<TimetableDataState>()
    private val redoStack = ArrayDeque<TimetableDataState>()

    private val stackSize = 32

    private fun updateMoveStackSize() {
        _undoRedoStack.value = Pair(undoStack.size, redoStack.size)
    }

    private fun recordChangeState(type: RecordStateType) {
        redoStack.clear()
        when (type) {
            TimeList -> recordUndoState(TimetableDataState.TimeListState(_timetable.value.timeList))
            EventList -> recordUndoState(TimetableDataState.EventListState(_timetable.value.eventList))
            EventTable -> recordUndoState(TimetableDataState.EventTableState(_timetable.value.eventTable))
            Minitial -> recordUndoState(
                TimetableDataState.EventAddState(
                    _timetable.value.eventList,
                    _timetable.value.eventTable
                )
            )
        }
        updateMoveStackSize()
    }

    private fun recordUndoState(state: TimetableDataState) {
        undoStack.push(state)
        if (undoStack.size > stackSize) {
            undoStack.removeLast()
        }
        updateMoveStackSize()
    }

    private fun recordRedoState(state: TimetableDataState) {
        redoStack.push(state)
        if (redoStack.size > stackSize) {
            redoStack.removeLast()
        }
        updateMoveStackSize()
    }

    private fun undo(): Boolean {
        return try {
            val state = undoStack.pop()

            if (state != null) {
                when (state) {
                    is TimetableDataState.EventListState -> {
                        recordRedoState(TimetableDataState.EventListState(_timetable.value.eventList))
                        _timetable.value = _timetable.value.copy(
                            eventList = state.data
                        )

                    }

                    is TimetableDataState.EventTableState -> {
                        recordRedoState(TimetableDataState.EventTableState(_timetable.value.eventTable))
                        _timetable.value = _timetable.value.copy(
                            eventTable = state.data
                        )

                    }

                    is TimetableDataState.TimeListState -> {
                        recordRedoState(TimetableDataState.TimeListState(_timetable.value.timeList))
                        _timetable.value = _timetable.value.copy(
                            timeList = state.data
                        )

                    }

                    is TimetableDataState.EventAddState -> {
                        recordRedoState(
                            TimetableDataState.EventAddState(
                                _timetable.value.eventList,
                                _timetable.value.eventTable
                            )
                        )
                        _timetable.value = _timetable.value.copy(
                            eventList = state.eventList,
                            eventTable = state.eventTable
                        )

                    }
                }
                true
            } else
                false
        } catch (_: Exception) {
            false
        }
    }

    private fun redo(): Boolean {
        return try {

            val state = redoStack.pop()

            if (state != null) {
                when (state) {
                    is TimetableDataState.EventListState -> {
                        recordRedoState(TimetableDataState.EventListState(_timetable.value.eventList))
                        _timetable.value = _timetable.value.copy(
                            eventList = state.data
                        )

                    }

                    is TimetableDataState.EventTableState -> {
                        recordRedoState(TimetableDataState.EventTableState(_timetable.value.eventTable))
                        _timetable.value = _timetable.value.copy(
                            eventTable = state.data
                        )

                    }

                    is TimetableDataState.TimeListState -> {
                        recordRedoState(TimetableDataState.TimeListState(_timetable.value.timeList))
                        _timetable.value = _timetable.value.copy(
                            timeList = state.data
                        )

                    }

                    is TimetableDataState.EventAddState -> {
                        recordUndoState(
                            TimetableDataState.EventAddState(
                                _timetable.value.eventList,
                                _timetable.value.eventTable
                            )
                        )
                        _timetable.value = _timetable.value.copy(
                            eventList = state.eventList,
                            eventTable = state.eventTable
                        )

                    }
                }
                true
            } else false

        } catch (_: Exception) {
            false
        }
    }

    enum class RecordStateType {
        TimeList, EventList, EventTable, Minitial
    }


    sealed class TimetableDataState {
        class TimeListState(val data: List<Int>) : TimetableDataState()
        class EventTableState(val data: List<List<Int>>) : TimetableDataState()
        class EventListState(val data: List<Event>) : TimetableDataState()
        class EventAddState(val eventList: List<Event>, val eventTable: List<List<Int>>) :
            TimetableDataState()
    }


}