package com.immanlv.trem.presentation.screens.timetable

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.immanlv.trem.domain.model.ClassType
import com.immanlv.trem.domain.model.Event
import com.immanlv.trem.domain.model.Subject
import com.immanlv.trem.domain.model.Timetable
import com.immanlv.trem.presentation.screens.timetable.components.TimetableCard

@Composable
fun TimetableView(
    navController: NavController,
    viewModel: TimetableViewModel = hiltViewModel()
) {
    val timetable = viewModel.timetable.value
    TimetableViewP(timetable = timetable)
}


@Composable
private fun TimetableViewP(timetable: Timetable) {
    if (timetable!=Timetable())
        LazyColumn(contentPadding = PaddingValues(16.dp)){
            itemsIndexed(timetable.eventTable[0]){ _, id ->
                Row(Modifier.padding(vertical = 8.dp)) {
                    TimetableCard(event = timetable.eventList[id])
                }
            }
        }
}


@Preview
@Composable
private fun TimetableViewPreview() {
    Surface(modifier = Modifier.fillMaxSize()) {
        TimetableViewP(timetable = Timetable(
            eventList = listOf(
                Event(
                    timeSpan = 1,
                    subjects = listOf(
                        Subject(
                            subject = "Hellows",
                            subjectCode = "ETM222",
                            teacher = "Someone 1"
                        )
                    ),
                    classType = ClassType.Theory
                ),
                Event(
                    timeSpan = 1,
                    subjects = listOf(
                        Subject(
                            subject = "Hellows",
                            subjectCode = "ETM222",
                            teacher = "Someone 2"
                        )
                    ),
                    classType = ClassType.Lab
                ),
                Event(
                    timeSpan = 1,
                    subjects = listOf(
                        Subject(
                            subject = "Hellows",
                            subjectCode = "ETM222",
                            teacher = "Someone 3"
                        )
                    ),
                    classType = ClassType.Theory
                )
            ),
            eventTable = listOf(listOf(0,1,2)),
            timeList = listOf(915,980,1045,1110)
        ))
    }
}