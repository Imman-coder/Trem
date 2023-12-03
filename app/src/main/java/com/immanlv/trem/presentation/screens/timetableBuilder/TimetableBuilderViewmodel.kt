package com.immanlv.trem.presentation.screens.timetableBuilder

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.immanlv.trem.domain.model.Timetable
import javax.inject.Inject

class TimetableBuilderViewModel
    @Inject constructor(

    ) :ViewModel() {

        var colorTable = mutableListOf<Color>()

        private val _timetable = mutableStateOf(Timetable())
        val timetable : State<Timetable> = _timetable


        init {
//            colorTable = generateColorPalette(
//                backgroundColor = MaterialTheme.colorScheme.background,
//                textColor = MaterialTheme.colorScheme.onBackground,
//                opacity = 120
//            )
        }




    }