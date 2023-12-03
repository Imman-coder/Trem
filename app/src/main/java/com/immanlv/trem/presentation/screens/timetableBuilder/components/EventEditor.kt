package com.immanlv.trem.presentation.screens.timetableBuilder.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import com.immanlv.trem.domain.model.Event
import com.touchlane.gridpad.GridPad
import com.touchlane.gridpad.GridPadCells

@Composable
fun EventEditor(
    event: Event,
    onValueUpdate: (Event) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
    ) {
        var subjects by remember {
            mutableStateOf(event.subjects)
        }
        var timeSpan by remember {
            mutableStateOf(event.time_span)
        }
        var classType by remember {
            mutableStateOf(event.class_type)
        }
        LaunchedEffect(key1 = subjects,timeSpan,classType) {
            onValueUpdate(Event(timeSpan,subjects,classType))
        }
        LaunchedEffect(key1 = event) {
            subjects = event.subjects
            timeSpan = event.time_span
            classType = event.class_type
        }
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Timespan")
            OutlinedButton(onClick = {
                timeSpan = (timeSpan - 1).let { if (it > 0) it else 1 }
            }) {
                Text(text = "-")
            }
            OutlinedTextField(
                readOnly = true,
                modifier = Modifier.width(60.dp),
                value = "$timeSpan",
                onValueChange = { timeSpan = it.toInt() })
            OutlinedButton(onClick = {
                timeSpan = (timeSpan + 1).let { if (it < 4) it else 4 }
            }) {
                Text(text = "+")
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            var k = 2
            if(subjects.size>k)k=subjects.size
            GridPad(cells = GridPadCells(rowCount =1, columnCount = k)){
                subjects.forEachIndexed { id, subject ->
                    item{
                        Column(Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Subject Name")
                                OutlinedTextField(
                                    value = subject.subject,
                                    onValueChange = {
                                        subjects = subjects.toMutableList()
                                            .apply { removeAt(id) }.apply {
                                                add(
                                                    id,
                                                    subjects[id].copy(subject = it)
                                                )
                                            }

                                    })
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Subject Code")
                                OutlinedTextField(
                                    value = subject.subject_code,
                                    onValueChange = {
                                        subjects = subjects.toMutableList()
                                            .apply { removeAt(id) }.apply {
                                                add(
                                                    id,
                                                    subjects[id].copy(subject_code = it)
                                                )
                                            }

                                    })
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Teacher Name")
                                OutlinedTextField(
                                    value = subject.teacher,
                                    onValueChange = {
                                        subjects = subjects.toMutableList()
                                            .apply { removeAt(id) }.apply {
                                                add(
                                                    id,
                                                    subjects[id].copy(teacher = it)
                                                )
                                            }

                                    })
                            }
                        }
                    }
                }
            }


        }
    }
}