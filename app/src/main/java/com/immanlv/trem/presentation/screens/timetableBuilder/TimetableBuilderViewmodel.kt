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


    init {
        populateColorPalette()

        viewModelScope.launch {
            appPreferencesUseCases.getAppPreference().onEach {
                _appPreference.value = it
            }.launchIn(viewModelScope)
        }
    }

    private fun setTimetable(timetable: Timetable) {
        _timetable.value = timetable
        if (_isTableInjected.value)
            viewModelScope.launch {
                appPreferencesUseCases.setAppPreference(
                    _appPreference.value.copy(
                        loadBuilderTimetable = Gson().toJson(timetable)
                    )
                )
            }
    }


    fun onEvent(event: TimetableBuilderEvent) {
        when (event) {
            is TimetableBuilderEvent.AddTime -> {
                // Record Timelist State
                recordChangeState(TimeList)

                // Adding new timestamp
                setTimetable(
                    _timetable.value.copy(
                        timeList = _timetable.value.timeList.toMutableList()
                            .apply { add(event.time) }.sorted()

                    )
                )
            }

            is TimetableBuilderEvent.AddBlankEvent -> {
                //
                recordChangeState(Minitial)

                //
                val id = _timetable.value.eventList.size + 1
                val eventL = _timetable.value.eventList.toMutableList().apply {
                    add(
                        Event(
                            timeSpan = 1,
                            subjects = listOf(Subject("", "", "")),
                            classType = ClassType.Theory
                        )
                    )
                }

                //
                val eventTable =
                    _timetable.value.eventTable[event.row].toMutableList().apply { add(id) }
                val editedTable =
                    _timetable.value.eventTable.toMutableList().apply { removeAt(event.row) }
                        .apply { add(event.row, eventTable) }


                setTimetable(
                    _timetable.value.copy(
                        eventTable = editedTable,
                        eventList = eventL
                    )
                )
            }

            is TimetableBuilderEvent.CopyEvent -> {

                //
                recordChangeState(EventTable)

                //
                val id = _timetable.value.eventTable[event.from.first][event.from.second]
                pasteEventIdTo(id, event.to)
            }

            is TimetableBuilderEvent.DeleteEvent -> {

                //
                recordChangeState(EventTable)

                //
                val dayList = _timetable.value.eventTable[event.from.first].toMutableList()
                    .apply { removeAt(event.from.second) }

                val editedTable =
                    _timetable.value.eventTable.toMutableList()
                        .apply { removeAt(event.from.first) }
                        .apply { add(event.from.first, dayList) }
                setTimetable(
                    _timetable.value.copy(
                        eventTable = editedTable
                    )
                )
            }

            is TimetableBuilderEvent.EditEvent -> {

                // Record Separately for this event
                // recordChangeState(EventList)

                //
                val e = _timetable.value.eventList.toMutableList().apply { removeAt(event.id) }
                    .apply { add(event.id, event.data) }
                setTimetable(
                    _timetable.value.copy(
                        eventList = e
                    )
                )
            }

            is TimetableBuilderEvent.PasteEventId -> {

                //
                recordChangeState(EventTable)

                //
                pasteEventIdTo(event.id, event.to)
            }

            is TimetableBuilderEvent.RemoveTime -> {

                //
                recordChangeState(TimeList)

                //
                setTimetable(
                    _timetable.value.copy(
                        timeList = _timetable.value.timeList.toMutableList()
                            .apply { removeAt(event.index) })
                )
            }

            is TimetableBuilderEvent.EditTime -> {

                //
                recordChangeState(TimeList)

                //
                setTimetable(_timetable.value.copy(
                    timeList = _timetable.value.timeList.toMutableList()
                        .apply { removeAt(event.index) }.apply { add(event.index, event.time) }
                        .sorted()
                ))
            }

            is TimetableBuilderEvent.LoadJson -> {
                val v = TimetableMapper.mapToDomainModel(
                    Gson().fromJson(
                        event.data,
                        TimetableDto::class.java
                    )
                )
                setTimetable(v)
            }

            TimetableBuilderEvent.NewTable -> {
                setTimetable(Timetable())
                redoStack.clear()
                undoStack.clear()
            }

            is TimetableBuilderEvent.MoveEvent -> {

                //
                recordChangeState(EventTable)

                //
                val id = _timetable.value.eventTable[event.from.first][event.from.second]
                pasteEventIdTo(id, event.to)

                val dayList = _timetable.value.eventTable[event.from.first].toMutableList()
                    .apply { removeAt(event.from.second) }

                val editedTable =
                    _timetable.value.eventTable.toMutableList()
                        .apply { removeAt(event.from.first) }
                        .apply { add(event.from.first, dayList) }
                setTimetable(
                    _timetable.value.copy(
                        eventTable = editedTable
                    )
                )
            }

            TimetableBuilderEvent.RefreshColorTable -> {
                populateColorPalette()
            }

            TimetableBuilderEvent.ToggleTimetableInject -> {
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

            TimetableBuilderEvent.Redo -> redo()
            TimetableBuilderEvent.Undo -> undo()
        }
    }

    fun getTimetableAsBytes(): ByteArray {

        val v = Gson().toJson(TimetableMapper.mapFromDomainModel(_timetable.value))

        return v.toByteArray(Charsets.UTF_8)
    }


    private fun pasteEventIdTo(id: Int, to: Pair<Int, Int>) {
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
        setTimetable(
            _timetable.value.copy(
                eventTable = editedTable
            )
        )
    }

    private fun populateColorPalette() {
        _colorTable.value = generateColorPalette(
            Color.Unspecified,
            Color.Unspecified,
            opacity = 120
        )
    }


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

    fun undo(): Boolean {
        return try {
            val state = undoStack.pop()

            if (state != null) {
                when (state) {
                    is TimetableDataState.EventListState -> {
                        recordRedoState(TimetableDataState.EventListState(_timetable.value.eventList))
                        setTimetable(
                            _timetable.value.copy(
                                eventList = state.data
                            )
                        )
                    }

                    is TimetableDataState.EventTableState -> {
                        recordRedoState(TimetableDataState.EventTableState(_timetable.value.eventTable))
                        setTimetable(
                            _timetable.value.copy(
                                eventTable = state.data
                            )
                        )
                    }

                    is TimetableDataState.TimeListState -> {
                        recordRedoState(TimetableDataState.TimeListState(_timetable.value.timeList))
                        setTimetable(
                            _timetable.value.copy(
                                timeList = state.data
                            )
                        )
                    }

                    is TimetableDataState.EventAddState -> {
                        recordRedoState(
                            TimetableDataState.EventAddState(
                                _timetable.value.eventList,
                                _timetable.value.eventTable
                            )
                        )
                        setTimetable(
                            _timetable.value.copy(
                                eventList = state.eventList,
                                eventTable = state.eventTable
                            )
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

    fun redo(): Boolean {
        return try {

            val state = redoStack.pop()

            if (state != null) {
                when (state) {
                    is TimetableDataState.EventListState -> {
                        recordRedoState(TimetableDataState.EventListState(_timetable.value.eventList))
                        setTimetable(
                            _timetable.value.copy(
                                eventList = state.data
                            )
                        )
                    }

                    is TimetableDataState.EventTableState -> {
                        recordRedoState(TimetableDataState.EventTableState(_timetable.value.eventTable))
                        setTimetable(
                            _timetable.value.copy(
                                eventTable = state.data
                            )
                        )
                    }

                    is TimetableDataState.TimeListState -> {
                        recordRedoState(TimetableDataState.TimeListState(_timetable.value.timeList))
                        setTimetable(
                            _timetable.value.copy(
                                timeList = state.data
                            )
                        )
                    }

                    is TimetableDataState.EventAddState -> {
                        recordUndoState(
                            TimetableDataState.EventAddState(
                                _timetable.value.eventList,
                                _timetable.value.eventTable
                            )
                        )
                        setTimetable(
                            _timetable.value.copy(
                                eventList = state.eventList,
                                eventTable = state.eventTable
                            )
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