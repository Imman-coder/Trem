package com.immanlv.trem.presentation.screens.timetable.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.immanlv.trem.domain.model.ClassType
import com.immanlv.trem.domain.model.Event
import com.immanlv.trem.domain.model.Subject


@Composable
fun TimetableCard(event: Event) {
    Card {
        Column(modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth()) {
            Text(text = event.subjects[0].subject)
            Text(text = event.subjects[0].subjectCode)
        }
    }
}

@Preview
@Composable
fun TimetableCardPreview() {
    TimetableCard(
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
        )
    )
}