package com.immanlv.trem.presentation.screens.timetableBuilder

import com.immanlv.trem.domain.model.Event

sealed class TimetableBuilderEvent {
    data class RemoveTime(val index: Int) : TimetableBuilderEvent()
    data class AddTime(val time: Int) : TimetableBuilderEvent()
    data class EditTime(val index:Int,val time: Int) : TimetableBuilderEvent()
    data class PasteEventId(val id: Int, val to :Pair<Int,Int>) : TimetableBuilderEvent()
    data class CopyEvent(val from: Pair<Int, Int>, val to :Pair<Int,Int>) : TimetableBuilderEvent()
    data class MoveEvent(val from: Pair<Int, Int>, val to :Pair<Int,Int>) : TimetableBuilderEvent()
    data class AddBlankEvent(val row: Int) : TimetableBuilderEvent()
    data class DeleteEvent(val from: Pair<Int, Int>) : TimetableBuilderEvent()
    data class EditEvent(val id:Int, val data: Event) : TimetableBuilderEvent()
    data class LoadJson(val data:String):TimetableBuilderEvent()
    data object NewTable : TimetableBuilderEvent()
    data object RefreshColorTable : TimetableBuilderEvent()
    data object ToggleTimetableInject : TimetableBuilderEvent()
    data object InjectTimetable : TimetableBuilderEvent()
    data object Undo : TimetableBuilderEvent()
    data object Redo : TimetableBuilderEvent()

}